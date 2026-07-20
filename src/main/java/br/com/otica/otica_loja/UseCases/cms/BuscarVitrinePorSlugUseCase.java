package br.com.otica.otica_loja.UseCases.cms;

import br.com.otica.otica_loja.Entity.CMS.Vitrine;
import br.com.otica.otica_loja.Entity.CMS.VitrineProduto;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoMidia;
import br.com.otica.otica_loja.Repository.CMS.VitrineRepository;
import br.com.otica.otica_loja.dto.cms.VitrineResponseDTO;
import br.com.otica.otica_loja.enums.TipoMidia;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

        Vitrine vitrine = vitrineRepository.findBySlug(slug)
                .filter(Vitrine::getAtivo)
                .orElseThrow(() -> new IllegalArgumentException("Vitrine não encontrada ou inativa."));

        List<VitrineResponseDTO.ProductCardDTO> produtosDTO = vitrine.getProdutos().stream()
                .sorted(Comparator.comparing(VitrineProduto::getOrdem))
                .map(vitrineProduto -> {
                    var produto = vitrineProduto.getProduto();

                    // 1. Conta a quantidade de cores/variantes (mantendo o contador dinâmico)
                    int totalCores = produto.getVariantes() != null ? produto.getVariantes().size() : 0;

                    // 2. Busca as imagens da primeira variante para respeitar a sequência de cor
                    List<String> caminhosImagens = new ArrayList<>();

                    if (produto.getVariantes() != null && !produto.getVariantes().isEmpty()) {
                        // Pega a primeira variante da lista ordenada pelo banco
                        var primeiraVariante = produto.getVariantes().iterator().next();

                        // Extrai as imagens específicas dessa variante na sequência original
                        caminhosImagens = primeiraVariante.getMidias().stream()
                                .filter(midia -> midia.getTipo() == TipoMidia.IMAGE)
                                .map(midia -> midia.getPath()) // Altere para o método getter correto da sua mídia de variante se necessário (ex: getPath)
                                .filter(path -> path != null && !path.isBlank())
                                .toList();
                    }

                    // 3. Fallback caso a variante não tenha imagens cadastradas
                    String imagemPrincipal = caminhosImagens.isEmpty()
                            ? "assets/images/placeholder.jpg"
                            : caminhosImagens.getFirst();

                    // 4. Limita a galeria ao máximo de 6 imagens que o carrossel do Angular usa
                    List<String> galeriaPreview = caminhosImagens.stream().limit(8).toList();

                    return new VitrineResponseDTO.ProductCardDTO(
                            produto.getId(),
                            produto.getNome(),
                            produto.getSlug(),
                            produto.getPreco(),
                            imagemPrincipal,
                            galeriaPreview,
                            totalCores
                    );
                })
                .toList();

        return new VitrineResponseDTO(
                vitrine.getTitulo(),
                vitrine.getSubtitulo(),
                produtosDTO
        );
    }
}