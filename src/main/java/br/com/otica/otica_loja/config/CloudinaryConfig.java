package br.com.otica.otica_loja.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        // Monta a string de conexão padrão do Cloudinary: cloudinary://api_key:api_secret@cloud_name
        String cloudinaryUrl = String.format("cloudinary://%s:%s@%s", apiKey, apiSecret, cloudName);

        // Inicializa a instância passando a URL diretamente no construtor
        Cloudinary instance = new Cloudinary(cloudinaryUrl);

        // Garante o retorno de links HTTPS seguros
        instance.config.secure = true;

        return instance;
    }
}