package br.com.otica.otica_loja.UseCases.produtos;

import br.com.otica.otica_loja.Repository.Catalogo.ProdutoMidiaRepository; // 👈 Import necessário
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoVarianteRepository;
import br.com.otica.otica_loja.dto.ProdutoPdvDTO;
import br.com.otica.otica_loja.enums.TipoMidia; // 👈 Import necessário
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BuscarProdutosPdvUseCase {

    private final ProdutoVarianteRepository varianteRepository;
    private final ProdutoMidiaRepository midiaRepository; // 👈 Injetado

    public List<ProdutoPdvDTO> buscar(String termo) {
        if (termo == null || termo.isBlank()) {
            return List.of();
        }

        var projecoes = varianteRepository.buscarVariantesParaPDV(termo, PageRequest.of(0, 10));

        return projecoes.stream().map(proj -> {
            var preco = proj.getPriceOverride() != null ? proj.getPriceOverride() : proj.getPrecoBase();
            String nomeCompleto = proj.getNomeProduto() + " - " + proj.getNomeVariante();

            // 👇 Lógica para resgatar a Imagem Principal
            // 1. Tenta pegar a imagem atrelada exclusivamente à variante
            String imagemUrl = midiaRepository.findFirstImagemByVarianteId(proj.getVarianteId(), TipoMidia.IMAGE)
                    .orElseGet(() ->
                            // 2. Se a variante não tem imagem, pega a imagem principal do Produto pai
                            midiaRepository.findFirstImagemByProdutoId(proj.getProdutoId(), TipoMidia.IMAGE).orElse(null)
                    );

            return new ProdutoPdvDTO(
                    proj.getVarianteId(),
                    nomeCompleto,
                    proj.getSku(),
                    proj.getStock(),
                    preco,
                    imagemUrl // 👈 Repassado ao DTO
            );
        }).collect(Collectors.toList());
    }
}