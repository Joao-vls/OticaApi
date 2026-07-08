package br.com.otica.otica_loja.UseCases.cms;

import br.com.otica.otica_loja.Entity.CMS.Vitrine;
import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import br.com.otica.otica_loja.Repository.CMS.VitrineProdutoRepository;
import br.com.otica.otica_loja.Repository.CMS.VitrineRepository;
import br.com.otica.otica_loja.Repository.Catalogo.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RemoverProdutoVitrineUseCase {

    @Autowired
    private VitrineRepository vitrineRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private VitrineProdutoRepository vitrineProdutoRepository;

    /**
     * Remove o vínculo de um produto em uma vitrine.
     */
    public void removerProduto(UUID vitrineId, UUID produtoId) {
        // Buscar vitrine
        Vitrine vitrine = vitrineRepository.findById(vitrineId)
                .orElseThrow(() -> new IllegalArgumentException("Vitrine não encontrada."));

        // Buscar produto
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        // Verificar se existe vínculo
        if (!vitrineProdutoRepository.existsByVitrineAndProduto(vitrine, produto)) {
            throw new IllegalArgumentException("Este produto não está vinculado a esta vitrine.");
        }

        // Remover vínculo
        vitrineProdutoRepository.deleteByVitrineAndProduto(vitrine, produto);
    }
}
