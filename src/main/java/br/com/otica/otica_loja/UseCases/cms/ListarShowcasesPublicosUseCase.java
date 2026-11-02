package br.com.otica.otica_loja.UseCases.cms;

import br.com.otica.otica_loja.Entity.CMS.SocialShowcase;
import br.com.otica.otica_loja.Repository.CMS.SocialShowcaseRepository;
import br.com.otica.otica_loja.dto.ShowcasePublicoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListarShowcasesPublicosUseCase {

    private final SocialShowcaseRepository repository;

    // 🔥 Cacheia a lista inteira de showcases.
    @Cacheable(value = "showcases_publicos_v1", key = "'ativos'")
    @Transactional(readOnly = true) // 🔥 Essencial para ler as mídias do produto sem erro Lazy!
    public List<ShowcasePublicoDTO> executar() {
        List<SocialShowcase> showcases = repository.findByAtivoTrueOrderByOrdemAsc();

        return showcases.stream().map(s -> {
            ShowcasePublicoDTO.ProdutoVinculadoDTO produtoDto = null;

            if (s.getProduto() != null) {
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