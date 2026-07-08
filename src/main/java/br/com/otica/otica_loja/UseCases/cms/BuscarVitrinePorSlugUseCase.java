package br.com.otica.otica_loja.UseCases.cms;


import br.com.otica.otica_loja.Entity.CMS.Vitrine;
import br.com.otica.otica_loja.Entity.CMS.VitrineProduto;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoMidia; // Importação correta do pacote
import br.com.otica.otica_loja.Repository.CMS.VitrineRepository;
import br.com.otica.otica_loja.dto.cms.VitrineResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class BuscarVitrinePorSlugUseCase {

    private final VitrineRepository vitrineRepository;

    public BuscarVitrinePorSlugUseCase(VitrineRepository vitrineRepository) {
        this.vitrineRepository = vitrineRepository;
    }

    @Transactional(readOnly = true)
    public VitrineResponseDTO executar(String slug) {

        // 1. Busca a vitrine ativa pelo slug
        Vitrine vitrine = vitrineRepository.findBySlug(slug)
                .filter(Vitrine::getAtivo)
                .orElseThrow(() -> new IllegalArgumentException("Vitrine não encontrada ou inativa."));

        // 2. Mapeia os produtos associados respeitando a ordem da vitrine
        List<VitrineResponseDTO.ProductCardDTO> produtosDTO = vitrine.getProdutos().stream()
                .sorted(Comparator.comparing(VitrineProduto::getOrdem))
                .map(vitrineProduto -> {
                    var produto = vitrineProduto.getProduto();

                    // 3. Filtra apenas mídias do tipo "image", remove nulos/vazios e ordena pela 'ordem' da mídia
                    List<String> caminhosImagens = produto.getMidias().stream()
                            .filter(midia -> "image".equalsIgnoreCase(midia.getTipo()))
                            .sorted(Comparator.comparing(ProdutoMidia::getOrdem))
                            .map(ProdutoMidia::getPath)
                            .filter(path -> path != null && !path.isBlank())
                            .toList();

                    // 4. Define o fallback caso o produto de alguma forma não tenha imagens salvas
                    String imagemPrincipal = caminhosImagens.isEmpty() ? "assets/images/placeholder.jpg" : caminhosImagens.get(0);

                    // 5. Garante o limite máximo de 5 imagens que o seu componente Angular espera para o preview
                    List<String> galeriaPreview = caminhosImagens.stream().limit(5).toList();

                    return new VitrineResponseDTO.ProductCardDTO(
                            produto.getId(),
                            produto.getNome(),
                            produto.getSlug(),
                            produto.getPreco(),
                            imagemPrincipal,
                            galeriaPreview
                    );
                })
                .toList();

        // 6. Monta a resposta final
        return new VitrineResponseDTO(
                vitrine.getTitulo(),
                vitrine.getSubtitulo(),
                produtosDTO
        );
    }
}