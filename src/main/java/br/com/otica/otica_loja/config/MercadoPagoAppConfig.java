package br.com.otica.otica_loja.config;

import com.mercadopago.MercadoPagoConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MercadoPagoAppConfig { // <-- Nome da classe alterado aqui!

    @Value("${mercadopago.access-token}")
    private String accessToken;

    @PostConstruct
    public void init() {
        // Agora o Java sabe que estamos chamando a classe do SDK
        MercadoPagoConfig.setAccessToken(accessToken);
    }
}