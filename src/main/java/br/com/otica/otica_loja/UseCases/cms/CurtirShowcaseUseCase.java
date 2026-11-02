package br.com.otica.otica_loja.UseCases.cms;

import br.com.otica.otica_loja.Entity.CMS.ShowcaseCurtida;
import br.com.otica.otica_loja.Entity.CMS.SocialShowcase;
import br.com.otica.otica_loja.Entity.Avaliacao.FavoritoId;
import br.com.otica.otica_loja.Repository.CMS.ShowcaseCurtidaRepository;
import br.com.otica.otica_loja.Repository.CMS.SocialShowcaseRepository;
import br.com.otica.otica_loja.Repository.Avaliacao.FavoritoRepository;
import br.com.otica.otica_loja.UseCases.favoritos.AdicionarFavoritoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CurtirShowcaseUseCase {

    private final SocialShowcaseRepository showcaseRepository;
    private final ShowcaseCurtidaRepository curtidaRepository;
    private final AdicionarFavoritoUseCase adicionarFavoritoUseCase;
    private final FavoritoRepository favoritoRepository;

    @Transactional
    // 🔥 Limpa o cache da lista de curtidas DESTE usuário específico
    // 🔥 Limpa o cache público para que o "+1" de curtida apareça para todos os outros usuários
    @Caching(evict = {
            @CacheEvict(value = "showcases_curtidas_v1", key = "#usuarioId.toString()"),
            @CacheEvict(value = "showcases_publicos_v1", allEntries = true)
    })
    public void executar(UUID showcaseId, UUID usuarioId) {

        if (curtidaRepository.existsByUsuarioIdAndShowcaseId(usuarioId, showcaseId)) {
            throw new IllegalArgumentException("Você já curtiu este vídeo.");
        }

        SocialShowcase showcase = showcaseRepository.findById(showcaseId)
                .orElseThrow(() -> new IllegalArgumentException("Showcase não encontrado."));

        showcase.setCurtidas(showcase.getCurtidas() + 1);
        showcaseRepository.save(showcase);

        ShowcaseCurtida curtida = new ShowcaseCurtida();
        curtida.setUsuarioId(usuarioId);
        curtida.setShowcaseId(showcaseId);
        curtidaRepository.save(curtida);

        if (showcase.getProduto() != null) {
            FavoritoId favId = new FavoritoId();
            favId.setUsuarioId(usuarioId);
            favId.setProdutoId(showcase.getProduto().getId());

            if (!favoritoRepository.existsById(favId)) {
                adicionarFavoritoUseCase.adicionar(usuarioId, showcase.getProduto().getId());
            }
        }
    }
}