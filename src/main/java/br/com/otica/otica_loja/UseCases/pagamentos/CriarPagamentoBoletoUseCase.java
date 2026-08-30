package br.com.otica.otica_loja.UseCases.pagamentos;

import br.com.otica.otica_loja.Entity.Auth.Endereco;
import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.UseCases.usuario.ListarEnderecosUseCase;
import br.com.otica.otica_loja.dto.BoletoPagamentoRequest;
import br.com.otica.otica_loja.dto.BoletoPagamentoResponse;
import br.com.otica.otica_loja.enums.StatusPedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CriarPagamentoBoletoUseCase {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ListarEnderecosUseCase listarEnderecosUseCase; // 👈 Injetado para buscar endereço

    @Value("${mercadopago.access-token}")
    private String accessToken;

    public BoletoPagamentoResponse criarPagamento(BoletoPagamentoRequest request) {
        Pedido pedido = pedidoRepository.findById(request.pedidoId())
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        if (pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new IllegalArgumentException("Pedido não está aguardando pagamento.");
        }

        // 1. 🔍 Busca o endereço padrão do usuário no banco
        Endereco enderecoCliente;
        try {
            enderecoCliente = listarEnderecosUseCase.buscarEnderecoPadrao(request.usuarioId());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("O cliente precisa ter um endereço padrão cadastrado para gerar o boleto.");
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();

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
            body.put("total_amount", String.valueOf(pedido.getTotal()));
            body.put("description", "Pedido Ótica - " + pedido.getId());

            Map<String, Object> payer = new HashMap<>();
            payer.put("email", request.emailCliente());
            payer.put("first_name", request.nomeCliente());
            payer.put("last_name", request.sobrenomeCliente());

            Map<String, Object> identification = new HashMap<>();
            identification.put("type", "CPF");
            identification.put("number", request.cpfCliente());
            payer.put("identification", identification);

            // 🎯 ENDEREÇO REAL VINDO DO BANCO DE DADOS
            Map<String, Object> address = new HashMap<>();
            address.put("zip_code", enderecoCliente.getCep().replaceAll("[^0-9]", ""));
            address.put("street_name", enderecoCliente.getLogradouro());
            address.put("street_number", enderecoCliente.getNumero() != null && !enderecoCliente.getNumero().isBlank() ? enderecoCliente.getNumero() : "S/N");
            address.put("neighborhood", enderecoCliente.getBairro());
            address.put("city", enderecoCliente.getCidade());
            address.put("state", enderecoCliente.getEstado());
            payer.put("address", address);

            body.put("payer", payer);

            Map<String, Object> paymentMethod = new HashMap<>();
            paymentMethod.put("id", "boleto");
            paymentMethod.put("type", "ticket");

            Map<String, Object> paymentItem = new HashMap<>();
            paymentItem.put("amount", String.valueOf(pedido.getTotal()));
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
            String orderId = createRoot.path("id").asString();

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

            String boletoUrl = methodResponse.path("ticket_url").asString("");
            String linhaDigitavel = methodResponse.path("digitable_line").asString("");

            if (linhaDigitavel.isEmpty()) {
                linhaDigitavel = methodResponse.path("barcode_content").asString("");
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

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar Boleto no Mercado Pago (Orders API): " + e.getMessage(), e);
        }
    }
}