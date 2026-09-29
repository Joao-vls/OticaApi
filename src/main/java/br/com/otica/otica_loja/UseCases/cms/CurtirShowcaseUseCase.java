package br.com.otica.otica_loja.UseCases.cms;

import br.com.otica.otica_loja.Entity.CMS.ShowcaseCurtida;
import br.com.otica.otica_loja.Entity.CMS.SocialShowcase;
import br.com.otica.otica_loja.Entity.Avaliacao.FavoritoId;
import br.com.otica.otica_loja.Repository.CMS.ShowcaseCurtidaRepository;
import br.com.otica.otica_loja.Repository.CMS.SocialShowcaseRepository;
import br.com.otica.otica_loja.Repository.Avaliacao.FavoritoRepository;
import br.com.otica.otica_loja.UseCases.favoritos.AdicionarFavoritoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CurtirShowcaseUseCase {

    private final SocialShowcaseRepository showcaseRepository;
    private final ShowcaseCurtidaRepository curtidaRepository; // INJETADO NOVO REPOSITÓRIO
    private final AdicionarFavoritoUseCase adicionarFavoritoUseCase;
    private final FavoritoRepository favoritoRepository;

    @Transactional
    public void executar(UUID showcaseId, UUID usuarioId) {

        // 1. Verifica se o usuário JÁ CURTIU este showcase
        if (curtidaRepository.existsByUsuarioIdAndShowcaseId(usuarioId, showcaseId)) {
            throw new IllegalArgumentException("Você já curtiu este vídeo.");
        }

        SocialShowcase showcase = showcaseRepository.findById(showcaseId)
                .orElseThrow(() -> new IllegalArgumentException("Showcase não encontrado."));

        // 2. Incrementa as curtidas gerais do Reel
        showcase.setCurtidas(showcase.getCurtidas() + 1);
        showcaseRepository.save(showcase);

        // 3. Salva o registro para o usuário não curtir de novo
        ShowcaseCurtida curtida = new ShowcaseCurtida();
        curtida.setUsuarioId(usuarioId);
        curtida.setShowcaseId(showcaseId);
        curtidaRepository.save(curtida);

        // 4. Se houver produto vinculado, salva nos favoritos do cliente
        if (showcase.getProduto() != null) {
            FavoritoId favId = new FavoritoId();
            favId.setUsuarioId(usuarioId);
            favId.setProdutoId(showcase.getProduto().getId());

            // Só adiciona se já não estiver nos favoritos
            if (!favoritoRepository.existsById(favId)) {
                adicionarFavoritoUseCase.adicionar(usuarioId, showcase.getProduto().getId());
            }
        }
    }
}