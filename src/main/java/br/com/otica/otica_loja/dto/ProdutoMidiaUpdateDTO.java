package br.com.otica.otica_loja.dto;

import br.com.otica.otica_loja.enums.TipoMidia;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class ProdutoMidiaUpdateDTO {
    private UUID id;
    private String uniqueId;       // 👈 Adicionado para mapear o id temporário do frontend em mídias novas
    private String refVariante;
    private TipoMidia tipo;
    private String origemMidia;
    private String path;           // Recebe o caminho local/Cloudinary existente
    private String thumbnailPath;  // Mantém a thumbnail existente
    private String urlExterna;
    private Integer ordem;
    private Boolean remover;
}