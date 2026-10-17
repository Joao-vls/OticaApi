package br.com.otica.otica_loja.UseCases.produtos;

import br.com.otica.otica_loja.Repository.Catalogo.ProdutoVarianteRepository;
import br.com.otica.otica_loja.dto.ProdutoPdvDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BuscarProdutosPdvUseCase {

    private final ProdutoVarianteRepository varianteRepository;

    public List<ProdutoPdvDTO> buscar(String termo) {
        if (termo == null || termo.isBlank()) {
            return List.of();
        }

        // Limita a 10 resultados para não pesar no dropdown
        var projecoes = varianteRepository.buscarVariantesParaPDV(termo, PageRequest.of(0, 10));

        return projecoes.stream().map(proj -> {
            // Lógica do preço: PriceOverride se existir, senão Preço Base do Produto
            var preco = proj.getPriceOverride() != null ? proj.getPriceOverride() : proj.getPrecoBase();

            String nomeCompleto = proj.getNomeProduto() + " - " + proj.getNomeVariante();

            return new ProdutoPdvDTO(
                    proj.getVarianteId(),
                    nomeCompleto,
                    proj.getSku(),
                    proj.getStock(),
                    preco
            );
        }).collect(Collectors.toList());
    }
}