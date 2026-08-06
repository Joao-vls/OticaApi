package br.com.otica.otica_loja.UseCases.cms;

import br.com.otica.otica_loja.Entity.CMS.Vitrine;
import br.com.otica.otica_loja.Entity.CMS.VitrineProduto;
import br.com.otica.otica_loja.Repository.CMS.VitrineRepository;
import br.com.otica.otica_loja.dto.cms.VitrineResponseDTO;
import br.com.otica.otica_loja.enums.TipoMidia;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class ListarVitrinesAtivasUseCase {

    private final VitrineRepository vitrineRepository;

    public ListarVitrinesAtivasUseCase(VitrineRepository vitrineRepository) {
        this.vitrineRepository = vitrineRepository;
    }

    @Transactional(readOnly = true)
    public List<VitrineResponseDTO> executar() {
        List<Vitrine> vitrinesAtivas = vitrineRepository.findByAtivoTrueOrderByOrdemAsc();

        return vitrinesAtivas.stream()
                .map(this::mapearParaDTO)
                .toList();
    }

    private VitrineResponseDTO mapearParaDTO(Vitrine vitrine) {
        List<VitrineResponseDTO.ProductCardDTO> produtosDTO = vitrine.getProdutos().stream()
                .sorted(Comparator.comparing(VitrineProduto::getOrdem))
                .map(vitrineProduto -> {
                    var produto = vitrineProduto.getProduto();

                    // Filtra apenas se o produto individual também estiver ativo (opcional, dependendo da regra de negócio)
                    if (produto.getAtivo() != null && !produto.getAtivo()) {
                        return null;
                    }

                    int totalCores = produto.getVariantes() != null ? produto.getVariantes().size() : 0;
                    List<String> caminhosImagens = new ArrayList<>();

                    if (produto.getVariantes() != null && !produto.getVariantes().isEmpty()) {
                        var primeiraVariante = produto.getVariantes().iterator().next();

                        caminhosImagens = primeiraVariante.getMidias().stream()
                                .filter(midia -> midia.getTipo() == TipoMidia.IMAGE)
                                .map(midia -> midia.getPath())
                                .filter(path -> path != null && !path.isBlank())
                                .toList();
                    }

                    String imagemPrincipal = caminhosImagens.isEmpty()
                            ? "assets/images/placeholder.jpg"
                            : caminhosImagens.getFirst();

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
                .filter(Objects::nonNull)
                .toList();

        return new VitrineResponseDTO(
                vitrine.getTitulo(),
                vitrine.getSubtitulo(),
                produtosDTO
        );
    }
}