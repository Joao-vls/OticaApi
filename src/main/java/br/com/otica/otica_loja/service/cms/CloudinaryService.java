package br.com.otica.otica_loja.service.cms;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String upload(MultipartFile arquivo) throws IOException {
        Map<String, Object> options = ObjectUtils.asMap(
                "resource_type", "auto",
                "folder", "cms/banners"
        );

        // 1. Criamos um arquivo temporário no sistema para que o HTTP5 do Cloudinary consiga ler
        File arquivoTemporario = File.createTempFile("upload-", arquivo.getOriginalFilename());

        try {
            // Transferimos o conteúdo do MultipartFile para o arquivo temporário
            arquivo.transferTo(arquivoTemporario);

            // 2. Enviamos o File diretamente, o que é totalmente suportado pelo cloudinary-http5
            Map<?, ?> resultado = cloudinary.uploader().upload(
                    arquivoTemporario,
                    options
            );

            // Retorna a URL segura gerada pelo Cloudinary
            return resultado.get("secure_url").toString();

        } finally {
            // 3. Deleta o arquivo temporário do servidor após o upload para não acumular lixo
            if (arquivoTemporario.exists()) {
                Files.delete(arquivoTemporario.toPath());
            }
        }
    }
}