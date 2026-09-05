package br.com.otica.otica_loja.UseCases.pagamentos;

import br.com.otica.otica_loja.Entity.Auth.Endereco;
import br.com.otica.otica_loja.Entity.Pedidos.Pedido;
import br.com.otica.otica_loja.Repository.Pedidos.PedidoRepository;
import br.com.otica.otica_loja.UseCases.usuario.ListarEnderecosUseCase;
import br.com.otica.otica_loja.dto.BoletoPagamentoRequest;
import br.com.otica.otica_loja.dto.BoletoPagamentoResponse;
import br.com.otica.otica_loja.enums.StatusPedido;

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

        Endereco enderecoCliente;
        try {
            enderecoCliente = listarEnderecosUseCase.buscarEnderecoPadrao(request.usuarioId());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("O cliente precisa ter um endereço padrão cadastrado para gerar o boleto.");
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();

            String totalAmountStr = String.format(Locale.US, "%.2f", pedido.getTotal());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            // ==========================================
            // PASSO 1: CRIAR A ORDER (Modo automático ou manual ajustado)
            // ==========================================
            Map<String, Object> body = new HashMap<>();
            body.put("type", "online");
            body.put("external_reference", pedido.getId().toString());
            body.put("processing_mode", "automatic"); // 👈 Alterado para automatic para gerar e processar direto
            body.put("total_amount", totalAmountStr);
            body.put("description", "Pedido Ótica - " + pedido.getId());

            Map<String, Object> payer = new HashMap<>();
            payer.put("email", request.emailCliente());
            payer.put("first_name", request.nomeCliente() != null && !request.nomeCliente().isBlank() ? request.nomeCliente() : "Cliente");
            payer.put("last_name", request.sobrenomeCliente() != null && !request.sobrenomeCliente().isBlank() ? request.sobrenomeCliente() : "Teste");

            Map<String, Object> identification = new HashMap<>();
            identification.put("type", "CPF");
            // Limpa o CPF para conter apenas números
            String cpfLimpo = request.cpfCliente() != null ? request.cpfCliente().replaceAll("[^0-9]", "") : "19119119119";
            identification.put("number", cpfLimpo);
            payer.put("identification", identification);

            Map<String, Object> address = new HashMap<>();
            address.put("zip_code", enderecoCliente.getCep() != null ? enderecoCliente.getCep().replaceAll("[^0-9]", "") : "39480000");
            address.put("street_name", enderecoCliente.getLogradouro() != null ? enderecoCliente.getLogradouro() : "Rua Principal");
            address.put("street_number", enderecoCliente.getNumero() != null && !enderecoCliente.getNumero().isBlank() ? enderecoCliente.getNumero() : "S/N");
            address.put("neighborhood", enderecoCliente.getBairro() != null ? enderecoCliente.getBairro() : "Centro");
            address.put("city", enderecoCliente.getCidade() != null ? enderecoCliente.getCidade() : "Januaria");

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
            paymentItem.put("expiration_time", "P3D"); // 👈 Adicionado o prazo de vencimento recomendado pelo MP

            Map<String, Object> transactions = new HashMap<>();
            transactions.put("payments", List.of(paymentItem));
            body.put("transactions", transactions);

            headers.set("X-Idempotency-Key", UUID.randomUUID().toString());
            HttpEntity<Map<String, Object>> createEntity = new HttpEntity<>(body, headers);

            System.out.println("Enviando requisição de Boleto para Orders API...");
            ResponseEntity<String> createResponse = restTemplate.postForEntity(
                    "https://api.mercadopago.com/v1/orders",
                    createEntity,
                    String.class
            );

            JsonNode createRoot = mapper.readTree(createResponse.getBody());

            // Como usamos processing_mode = automatic, o boleto já deve vir dentro da resposta da Order!
            JsonNode paymentsArray = createRoot.path("transactions").path("payments");

            String boletoUrl = "";
            String linhaDigitavel = "";

            if (paymentsArray.isArray() && !paymentsArray.isEmpty()) {
                JsonNode firstPayment = paymentsArray.get(0);
                JsonNode methodResponse = firstPayment.path("payment_method");

                boletoUrl = methodResponse.path("ticket_url").asText("");
                linhaDigitavel = methodResponse.path("digitable_line").asText("");

                if (linhaDigitavel.isEmpty()) {
                    linhaDigitavel = methodResponse.path("barcode_content").asText("");
                }
            }

            // Se por acaso a URL vier vazia, tentamos o endpoint de process explícito como fallback
            if (boletoUrl.isEmpty()) {
                String orderId = createRoot.path("id").asText(null);
                if (orderId != null) {
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

                    boletoUrl = methodResponse.path("ticket_url").asText("");
                    linhaDigitavel = methodResponse.path("digitable_line").asText("");

                    if (linhaDigitavel.isEmpty()) {
                        linhaDigitavel = methodResponse.path("barcode_content").asText("");
                    }
                }
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
            System.err.println("ERRO MP BOLETO HTTP: " + e.getStatusCode());
            System.err.println("CORPO DO ERRO MP BOLETO: " + e.getResponseBodyAsString());
            throw new RuntimeException("Erro da API do Mercado Pago (Boleto): " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao gerar Boleto no Mercado Pago (Orders API): " + e.getMessage(), e);
        }
    }
}