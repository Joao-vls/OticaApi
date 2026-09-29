package br.com.otica.otica_loja.UseCases.cms;

import br.com.otica.otica_loja.Entity.CMS.SocialShowcase;
import br.com.otica.otica_loja.Repository.CMS.SocialShowcaseRepository;

import br.com.otica.otica_loja.dto.ShowcasePublicoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListarShowcasesPublicosUseCase {

    private final SocialShowcaseRepository repository;

    public List<ShowcasePublicoDTO> executar() {
        List<SocialShowcase> showcases = repository.findByAtivoTrueOrderByOrdemAsc();

        return showcases.stream().map(s -> {
            ShowcasePublicoDTO.ProdutoVinculadoDTO produtoDto = null;

            // Se o showcase tiver produto vinculado, mapeia para o frontend saber para onde redirecionar
            if (s.getProduto() != null) {
                // Ajuste a forma como você pega a imagem principal do produto conforme sua lógica atual
                String imagem = s.getProduto().getMidias().stream().findFirst().map(m -> m.getPath()).orElse(null);

                produtoDto = new ShowcasePublicoDTO.ProdutoVinculadoDTO(
                        s.getProduto().getId(),
                        s.getProduto().getNome(),
                        s.getProduto().getSlug(),
                        imagem,
                        s.getProduto().getPreco()
                );
            }

            return new ShowcasePublicoDTO(
                    s.getId(), s.getMarcaNome(), s.getModeloNome(),
                    s.getUsername(), s.getThumbnailPath(), s.getVideoPath(),
                    s.getCurtidas(), s.getVisualizacoes(), produtoDto
            );
        }).collect(Collectors.toList());
    }
}