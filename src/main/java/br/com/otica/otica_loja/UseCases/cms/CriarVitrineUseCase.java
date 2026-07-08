package br.com.otica.otica_loja.UseCases.cms;

import br.com.otica.otica_loja.Entity.CMS.Vitrine;
import br.com.otica.otica_loja.Repository.CMS.VitrineRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class CriarVitrineUseCase {

    private final VitrineRepository vitrineRepository;

    // Injeção por construtor (dispensa o @Autowired e facilita testes)
    public CriarVitrineUseCase(VitrineRepository vitrineRepository) {
        this.vitrineRepository = vitrineRepository;
    }

    /**
     * DTO interno (Command) para agrupar os dados de entrada
     */
    public record Command(
            String nome,
            String slug,
            String titulo,
            String subtitulo,
            Integer ordem,
            Boolean ativo
    ) {}

    /**
     * Cria uma nova vitrine no CMS.
     */
    public Vitrine executar(Command command) {

        // Validação de regra de negócio
        if (vitrineRepository.existsBySlug(command.slug())) {
            throw new IllegalArgumentException("Já existe uma vitrine com este slug.");
        }

        Vitrine vitrine = new Vitrine();
        vitrine.setNome(command.nome());
        vitrine.setSlug(command.slug());
        vitrine.setTitulo(command.titulo());
        vitrine.setSubtitulo(command.subtitulo());
        vitrine.setOrdem(command.ordem() != null ? command.ordem() : 0);
        vitrine.setAtivo(command.ativo() != null ? command.ativo() : true);

        OffsetDateTime agora = OffsetDateTime.now();
        vitrine.setCriadoEm(agora);
        vitrine.setAtualizadoEm(agora);

        return vitrineRepository.save(vitrine);
    }
}