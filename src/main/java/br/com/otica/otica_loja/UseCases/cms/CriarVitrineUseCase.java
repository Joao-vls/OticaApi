package br.com.otica.otica_loja.UseCases.cms;

import br.com.otica.otica_loja.Entity.CMS.Vitrine;
import br.com.otica.otica_loja.Entity.CMS.VitrineProduto;
import br.com.otica.otica_loja.Entity.CMS.VitrineProdutoId;
import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Repository.CMS.VitrineRepository;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CriarVitrineUseCase {

    private final VitrineRepository vitrineRepository;
    private final ProdutoRepository produtoRepository;

    public CriarVitrineUseCase(VitrineRepository vitrineRepository, ProdutoRepository produtoRepository) {
        this.vitrineRepository = vitrineRepository;
        this.produtoRepository = produtoRepository;
    }

    public record Command(
            String nome,
            String slug,
            String titulo,
            String subtitulo,
            Integer ordem,
            Boolean ativo,
            List<UUID> produtosIds
    ) {}

    @Transactional
    public Vitrine executar(Command command) {
        if (vitrineRepository.existsBySlug(command.slug())) {
            throw new IllegalArgumentException("Já existe uma vitrine com o slug informado: " + command.slug());
        }

        Vitrine vitrine = new Vitrine();
        vitrine.setNome(command.nome());
        vitrine.setSlug(command.slug());
        vitrine.setTitulo(command.titulo());
        vitrine.setSubtitulo(command.subtitulo());
        vitrine.setOrdem(command.ordem() != null ? command.ordem() : 0);
        vitrine.setAtivo(command.ativo() == null || command.ativo());
        vitrine.setCriadoEm(OffsetDateTime.now());
        vitrine.setAtualizadoEm(OffsetDateTime.now());

        // Vincula os produtos se forem passados no DTO
        if (command.produtosIds() != null && !command.produtosIds().isEmpty()) {
            for (int i = 0; i < command.produtosIds().size(); i++) {
                UUID produtoId = command.produtosIds().get(i);
                Produto produto = produtoRepository.findById(produtoId)
                        .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + produtoId));

                VitrineProduto vp = new VitrineProduto();
                vp.setId(new VitrineProdutoId(vitrine.getId(), produto.getId()));
                vp.setVitrine(vitrine);
                vp.setProduto(produto);
                vp.setOrdem(i);

                vitrine.getProdutos().add(vp);
            }
        }

        return vitrineRepository.save(vitrine);
    }
}