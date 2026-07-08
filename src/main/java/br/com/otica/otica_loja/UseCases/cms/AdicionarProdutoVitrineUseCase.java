package br.com.otica.otica_loja.UseCases.cms;

import br.com.otica.otica_loja.Entity.CMS.Vitrine;
import br.com.otica.otica_loja.Entity.CMS.VitrineProduto;
import br.com.otica.otica_loja.Entity.CMS.VitrineProdutoId;
import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Repository.CMS.VitrineProdutoRepository;
import br.com.otica.otica_loja.Repository.CMS.VitrineRepository;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AdicionarProdutoVitrineUseCase {

    private final VitrineRepository vitrineRepository;
    private final ProdutoRepository produtoRepository;
    private final VitrineProdutoRepository vitrineProdutoRepository;

    // Injeção por construtor para manter o padrão sem usar @Autowired nos atributos
    public AdicionarProdutoVitrineUseCase(
            VitrineRepository vitrineRepository,
            ProdutoRepository produtoRepository,
            VitrineProdutoRepository vitrineProdutoRepository
    ) {
        this.vitrineRepository = vitrineRepository;
        this.produtoRepository = produtoRepository;
        this.vitrineProdutoRepository = vitrineProdutoRepository;
    }

    /**
     * Adiciona um produto a uma vitrine de forma transacional.
     */
    @Transactional
    public VitrineProduto adicionarProduto(UUID vitrineId, UUID produtoId, Integer ordem) {

        // 1. Buscar vitrine
        Vitrine vitrine = vitrineRepository.findById(vitrineId)
                .orElseThrow(() -> new IllegalArgumentException("Vitrine não encontrada."));

        // 2. Buscar produto
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        // 3. Verificar se já existe vínculo
        if (vitrineProdutoRepository.existsByVitrineAndProduto(vitrine, produto)) {
            throw new IllegalArgumentException("Este produto já está vinculado a esta vitrine.");
        }

        // 4. Criar e preencher a entidade de vínculo
        VitrineProduto vitrineProduto = new VitrineProduto();
        vitrineProduto.setId(new VitrineProdutoId(vitrine.getId(), produto.getId()));
        vitrineProduto.setVitrine(vitrine);
        vitrineProduto.setProduto(produto);
        vitrineProduto.setOrdem(ordem != null ? ordem : 0);

        // 5. Salva e retorna o vínculo estabelecido
        return vitrineProdutoRepository.save(vitrineProduto);
    }
}