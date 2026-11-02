package br.com.otica.otica_loja.UseCases.produtos;

import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoVarianteRepository;
import br.com.otica.otica_loja.dto.ProdutoDetalheDTO;
import br.com.otica.otica_loja.mapper.ProdutoDetalheMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuscarProdutoPorVarianteUseCase {

    private final ProdutoVarianteRepository produtoVarianteRepository;
    private final ProdutoDetalheMapper produtoDetalheMapper; // 🔥 Injeção do Mapper

    /**
     * Localiza a variante pelo seu ID, valida e retorna o produto pai associado a ela em formato DTO.
     */
    @Cacheable(value = "produto", key = "'variante:' + #varianteIdStr") // 🔥 Cache seguro
    @Transactional(readOnly = true)
    public ProdutoDetalheDTO executar(String varianteIdStr) {
        try {
            UUID varianteId = UUID.fromString(varianteIdStr);

            ProdutoVariante variante = produtoVarianteRepository.findById(varianteId)
                    .orElseThrow(() -> new IllegalArgumentException("Variante não encontrada."));

            Produto produto = variante.getProduto();
            if (produto == null) {
                throw new IllegalArgumentException("Produto não associado a esta variante.");
            }

            // 🔥 Retorna o DTO Limpo sem laços do Hibernate
            return produtoDetalheMapper.toDTO(produto);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("ID de variante inválido ou produto não encontrado.");
        }
    }
}