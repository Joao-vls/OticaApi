package br.com.otica.otica_loja.UseCases.membros;

import br.com.otica.otica_loja.Entity.Auth.Usuario;
import br.com.otica.otica_loja.Entity.CRM.AssinaturaClube;
import br.com.otica.otica_loja.Repository.Auth.UsuarioRepository;
import br.com.otica.otica_loja.Repository.CRM.AssinaturaClubeRepository;
import br.com.otica.otica_loja.dto.AtivarAssinaturaAdminRequestDTO;
import br.com.otica.otica_loja.enums.StatusAssinatura;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AtivarAssinaturaAdminUseCase {

    private final AssinaturaClubeRepository assinaturaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public AssinaturaClube executar(UUID usuarioId, AtivarAssinaturaAdminRequestDTO dto) {
        // Verifica se o usuário existe
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        // Busca se já existe uma assinatura ou cria uma nova
        Optional<AssinaturaClube> assinaturaOpt = assinaturaRepository.findByUsuarioId(usuario.getId());
        AssinaturaClube assinatura = assinaturaOpt.orElseGet(() -> {
            AssinaturaClube nova = new AssinaturaClube();
            nova.setUsuarioId(usuario.getId());
            return nova;
        });

        // Atualiza os dados de renovação
        assinatura.setStatus(StatusAssinatura.ATIVA);
        assinatura.setDataInicio(OffsetDateTime.now());
        assinatura.setDataRenovacao(OffsetDateTime.now().plusMonths(dto.mesesValidade()));
        assinatura.setCanalPagamento(dto.canalPagamento());
        assinatura.setAnotacoesAdmin(dto.anotacoesAdmin());
        assinatura.setAtualizadoEm(OffsetDateTime.now());

        return assinaturaRepository.save(assinatura);
    }
}