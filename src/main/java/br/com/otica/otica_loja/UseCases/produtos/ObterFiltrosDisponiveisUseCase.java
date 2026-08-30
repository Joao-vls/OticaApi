package br.com.otica.otica_loja.UseCases.produtos;

import br.com.otica.otica_loja.Entity.Catalogo.Categoria;
import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Entity.Catalogo.ProdutoVariante;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;
import br.com.otica.otica_loja.dto.FiltrosDisponiveisDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ObterFiltrosDisponiveisUseCase {

    private final ProdutoRepository produtoRepository;

    public FiltrosDisponiveisDTO executar() {
        List<Produto> produtosAtivos = produtoRepository.findByAtivoTrue();

        // 1. Faixa de preço
        BigDecimal precoMin = produtosAtivos.stream()
                .map(Produto::getPreco)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        BigDecimal precoMax = produtosAtivos.stream()
                .map(Produto::getPreco)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        // 2. Extração de nomes de marcas
        Set<String> marcas = new HashSet<>();
        produtosAtivos.forEach(p -> {
            if (p.getMarca() != null && p.getMarca().getNome() != null) {
                marcas.add(p.getMarca().getNome());
            }
        });

        // 3. Extração de categorias
        Set<String> categorias = new HashSet<>();
        produtosAtivos.stream()
                .flatMap(p -> p.getCategorias().stream())
                .map(Categoria::getNome)
                .filter(Objects::nonNull)
                .forEach(categorias::add);

        // 4. Extração de cores das variantes
        Set<String> cores = new HashSet<>();
        produtosAtivos.stream()
                .flatMap(p -> p.getVariantes().stream())
                .map(ProdutoVariante::getColorName)
                .filter(Objects::nonNull)
                .forEach(cores::add);

        // 5. Extração de graus disponíveis
        Set<String> graus = new HashSet<>();
        produtosAtivos.stream()
                .flatMap(p -> p.getGrausDisponiveis().stream())
                .map(Enum::name)
                .forEach(graus::add);

        // 6. Mapeamento dinâmico das specs (JSONB)
        Map<String, Set<String>> specsMap = new HashMap<>();
        List<ProdutoRepository.SpecKeyValueProjection> specsProjections = produtoRepository.buscarTodasSpecsAtivas();

        for (ProdutoRepository.SpecKeyValueProjection spec : specsProjections) {
            specsMap.computeIfAbsent(spec.getChave(), k -> new HashSet<>()).add(spec.getValor());
        }

        return FiltrosDisponiveisDTO.builder()
                .precoMinimo(precoMin)
                .precoMaximo(precoMax)
                .marcasDisponiveis(marcas)
                .categoriasDisponiveis(categorias)
                .coresDisponiveis(cores)
                .grausDisponiveis(graus)
                .specs(specsMap)
                .build();
    }
}