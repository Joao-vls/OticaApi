package br.com.otica.otica_loja.UseCases.cms;

import br.com.otica.otica_loja.Entity.CMS.Banner;
import br.com.otica.otica_loja.Repository.CMS.BannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ExcluirBannerUseCase {

    @Autowired
    private BannerRepository bannerRepository;

    /**
     * Exclui (desativa) um banner existente.
     * Exclusão lógica: apenas marca como inativo.
     */
    public Banner excluirBanner(UUID bannerId) {
        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new IllegalArgumentException("Banner não encontrado."));

        // Exclusão lógica
        banner.setAtivo(false);

        return bannerRepository.save(banner);
    }

    /**
     * Exclusão física: remove o banner do banco de dados.
     */
    public void excluirBannerFisicamente(UUID bannerId) {
        if (!bannerRepository.existsById(bannerId)) {
            throw new IllegalArgumentException("Banner não encontrado.");
        }
        bannerRepository.deleteById(bannerId);
    }
}
