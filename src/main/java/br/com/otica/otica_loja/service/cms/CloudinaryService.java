package br.com.otica.otica_loja.service.cms;

import br.com.otica.otica_loja.enums.TipoMidia;
import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String upload(MultipartFile file, TipoMidia tipoMidia) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        int maxTentativas = 3;
        int tentativa = 0;
        long tempoEsperaMs = 1500;

        // Configura as opções de upload baseadas no tipo de mídia real
        Map<String, Object> options = new HashMap<>();

        if (tipoMidia == TipoMidia.THREE_D) {
            options.put("resource_type", "raw"); // Arquivos .glb precisam ir como raw
            options.put("use_filename", true);
            options.put("unique_filename", true);

            String nomeOriginal = file.getOriginalFilename();
            if (nomeOriginal != null && !nomeOriginal.isBlank()) {
                int pontoIndex = nomeOriginal.lastIndexOf('.');
                // 🔥 GARANTE QUE A EXTENSÃO (ex: .glb) SEJA ADICIONADA AO PUBLIC_ID
                if (pontoIndex > 0) {
                    String extensao = nomeOriginal.substring(pontoIndex).toLowerCase(); // .glb
                    String nomeSemExtensao = nomeOriginal.substring(0, pontoIndex);

                    options.put("public_id", nomeSemExtensao + extensao);
                } else {
                    options.put("public_id", nomeOriginal);
                }
            }
        } else if (tipoMidia == TipoMidia.VIDEO) {
            options.put("resource_type", "video"); // Vídeos precisam ir como video
        } else {
            options.put("resource_type", "auto"); // Imagens podem ir como auto/image
        }

        while (tentativa < maxTentativas) {
            try {
                // Passa o mapa de opções configurado para o SDK
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
                return uploadResult.get("secure_url").toString();

            } catch (RuntimeException e) {
                tentativa++;

                if (e.getMessage().contains("429") && tentativa < maxTentativas) {
                    log.warn("Cloudinary limitou a taxa (429). Tentativa {} de {}. Aguardando {}ms...",
                            tentativa, maxTentativas, tempoEsperaMs);
                    try {
                        Thread.sleep(tempoEsperaMs);
                        tempoEsperaMs += 1000;
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Processo de upload interrompido.", ie);
                    }
                } else {
                    log.error("Erro crítico ao enviar arquivo para o Cloudinary na tentativa {}: {}", tentativa, e.getMessage());
                    throw e;
                }
            }
        }

        throw new IOException("Não foi possível completar o upload após " + maxTentativas + " tentativas.");
    }
}