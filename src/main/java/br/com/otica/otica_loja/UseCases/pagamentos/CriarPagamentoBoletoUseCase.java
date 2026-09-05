package br.com.otica.otica_loja.UseCases.pagamentos;

import br.com.otica.otica_loja.Entity.Auth.Endereco;
import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.UseCases.usuario.ListarEnderecosUseCase;
import br.com.otica.otica_loja.dto.BoletoPagamentoRequest;
import br.com.otica.otica_loja.dto.BoletoPagamentoResponse;
import br.com.otica.otica_loja.enums.StatusPedido;

// 1. IMPORTS CORRETOS DO JACKSON DO SPRING BOOT (com.fasterxml.jackson)
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Locale;

@Service
public class CriarPagamentoBoletoUseCase {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ListarEnderecosUseCase listarEnderecosUseCase;

    @Value("${mercadopago.access-token}")
    private String accessToken;

    public BoletoPagamentoResponse criarPagamento(BoletoPagamentoRequest request) {
        Pedido pedido = pedidoRepository.findById(request.pedidoId())
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        if (pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new IllegalArgumentException("Pedido não está aguardando pagamento.");
        }

        // 1. Busca o endereço padrão do usuário no banco
        Endereco enderecoCliente;
        try {
            enderecoCliente = listarEnderecosUseCase.buscarEnderecoPadrao(request.usuarioId());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("O cliente precisa ter um endereço padrão cadastrado para gerar o boleto.");
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();

            // Formata rigorosamente para 2 casas decimais (ex: 50.00) com Locale US
            String totalAmountStr = String.format(Locale.US, "%.2f", pedido.getTotal());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            // ==========================================
            // PASSO 1: CRIAR A ORDER (Modo Manual)
            // ==========================================
            Map<String, Object> body = new HashMap<>();
            body.put("type", "online");
            body.put("external_reference", pedido.getId().toString());
            body.put("processing_mode", "manual");
            body.put("total_amount", totalAmountStr);
            body.put("description", "Pedido Ótica - " + pedido.getId());

            Map<String, Object> payer = new HashMap<>();
            payer.put("email", request.emailCliente());
            payer.put("first_name", request.nomeCliente());
            payer.put("last_name", request.sobrenomeCliente());

            Map<String, Object> identification = new HashMap<>();
            identification.put("type", "CPF");
            identification.put("number", request.cpfCliente());
            payer.put("identification", identification);

            // Endereço formatado para regras do Mercado Pago (Estado com exatamente 2 letras)
            Map<String, Object> address = new HashMap<>();
            address.put("zip_code", enderecoCliente.getCep() != null ? enderecoCliente.getCep().replaceAll("[^0-9]", "") : "00000000");
            address.put("street_name", enderecoCliente.getLogradouro() != null ? enderecoCliente.getLogradouro() : "Rua Principal");
            address.put("street_number", enderecoCliente.getNumero() != null && !enderecoCliente.getNumero().isBlank() ? enderecoCliente.getNumero() : "S/N");
            address.put("neighborhood", enderecoCliente.getBairro() != null ? enderecoCliente.getBairro() : "Centro");
            address.put("city", enderecoCliente.getCidade() != null ? enderecoCliente.getCidade() : "Cidade");

            // Garante que o estado tenha exatamente 2 caracteres maiúsculos (ex: "MG", "SP")
            String uf = enderecoCliente.getEstado() != null ? enderecoCliente.getEstado().trim().toUpperCase() : "MG";
            if (uf.length() > 2) {
                uf = uf.substring(0, 2);
            }
            address.put("state", uf);

            payer.put("address", address);
            body.put("payer", payer);

            Map<String, Object> paymentMethod = new HashMap<>();
            paymentMethod.put("id", "boleto");
            paymentMethod.put("type", "ticket");

            Map<String, Object> paymentItem = new HashMap<>();
            paymentItem.put("amount", totalAmountStr);
            paymentItem.put("payment_method", paymentMethod);

            Map<String, Object> transactions = new HashMap<>();
            transactions.put("payments", List.of(paymentItem));
            body.put("transactions", transactions);

            headers.set("X-Idempotency-Key", UUID.randomUUID().toString());
            HttpEntity<Map<String, Object>> createEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> createResponse = restTemplate.postForEntity(
                    "https://api.mercadopago.com/v1/orders",
                    createEntity,
                    String.class
            );

            JsonNode createRoot = mapper.readTree(createResponse.getBody());
            // Correção do método do Jackson: usar asText(null) em vez de asString()
            String orderId = createRoot.path("id").asText(null);

            if (orderId == null || orderId.isEmpty()) {
                throw new RuntimeException("Falha ao obter o ID da Order no Mercado Pago.");
            }

            // ==========================================
            // PASSO 2: PROCESSAR A ORDER
            // ==========================================
            headers.set("X-Idempotency-Key", UUID.randomUUID().toString());
            HttpEntity<String> processEntity = new HttpEntity<>(null, headers);

            ResponseEntity<String> processResponse = restTemplate.exchange(
                    "https://api.mercadopago.com/v1/orders/" + orderId + "/process",
                    HttpMethod.POST,
                    processEntity,
                    String.class
            );

            JsonNode processRoot = mapper.readTree(processResponse.getBody());

            JsonNode firstPayment = processRoot.path("transactions").path("payments").get(0);
            JsonNode methodResponse = firstPayment.path("payment_method");

            // Correção de asString() para asText("") do Jackson
            String boletoUrl = methodResponse.path("ticket_url").asText("");
            String linhaDigitavel = methodResponse.path("digitable_line").asText("");

            if (linhaDigitavel.isEmpty()) {
                linhaDigitavel = methodResponse.path("barcode_content").asText("");
            }

            pedido.setStatus(StatusPedido.PROCESSANDO);
            pedido.setAtualizadoEm(OffsetDateTime.now());
            pedidoRepository.save(pedido);

            return new BoletoPagamentoResponse(
                    pedido.getId(),
                    pedido.getTotal(),
                    boletoUrl,
                    linhaDigitavel,
                    "Boleto gerado com sucesso"
            );

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            // Log detalhado caso o Mercado Pago recuse por validação de campos
            System.err.println("ERRO MP BOLETO HTTP: " + e.getStatusCode());
            System.err.println("CORPO DO ERRO MP BOLETO: " + e.getResponseBodyAsString());
            throw new RuntimeException("Erro da API do Mercado Pago (Boleto): " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao gerar Boleto no Mercado Pago (Orders API): " + e.getMessage(), e);
        }
    }
}