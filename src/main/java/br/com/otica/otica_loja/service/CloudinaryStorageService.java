package br.com.otica.otica_loja.service;

import br.com.otica.otica_loja.Entity.Catalogo.ProdutoMidia;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryStorageService {

    private final Cloudinary cloudinary;

    /**
     * Deleta todos os arquivos vinculados a uma entidade ProdutoMidia (path, thumbnail, poster).
     */
    public void deletarMidiaCompleta(ProdutoMidia midia) {
        if (midia == null) return;

        deletarArquivoPorUrl(midia.getPath());
        deletarArquivoPorUrl(midia.getThumbnailPath());
        deletarArquivoPorUrl(midia.getPosterPath());
    }

    /**
     * Extrai o public_id e remove o arquivo do Cloudinary.
     */
    public void deletarArquivoPorUrl(String url) {
        if (url == null || url.isBlank()) {
            return;
        }

        try {
            String publicId = extrairPublicId(url);
            String resourceType = determinarTipoRecurso(url);

            Map response = cloudinary.uploader().destroy(publicId, ObjectUtils.asMap(
                    "resource_type", resourceType,
                    "invalidate", true
            ));

            log.info("Arquivo removido do Cloudinary [Public ID: {}]: {}", publicId, response.get("result"));
        } catch (Exception e) {
            log.error("Erro ao deletar arquivo no Cloudinary para a URL: {}", url, e);
        }
    }

    private String extrairPublicId(String url) {
        String semParametros = url.split("\\?")[0];
        String subPath = semParametros.substring(semParametros.indexOf("/upload/") + 8);

        if (subPath.matches("^v\\d+/.*")) {
            subPath = subPath.substring(subPath.indexOf("/") + 1);
        }

        int ultimoPonto = subPath.lastIndexOf('.');
        if (ultimoPonto != -1) {
            subPath = subPath.substring(0, ultimoPonto);
        }

        return subPath;
    }

    private String determinarTipoRecurso(String url) {
        if (url.contains("/video/upload/")) {
            return "video";
        } else if (url.contains("/raw/upload/")) {
            return "raw";
        }
        return "image";
    }
}