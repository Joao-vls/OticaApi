package br.com.otica.otica_loja.Repository.CMS;

import br.com.otica.otica_loja.Entity.CMS.CampanhaProduto;
import br.com.otica.otica_loja.Entity.CMS.CampanhaProdutoId;
import br.com.otica.otica_loja.Entity.CMS.Campanha;
import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampanhaProdutoRepository extends JpaRepository<CampanhaProduto, CampanhaProdutoId> {

    // Buscar todos os produtos de uma campanha
    List<CampanhaProduto> findByCampanha(Campanha campanha);

    // Buscar todas as campanhas de um produto
    List<CampanhaProduto> findByProduto(Produto produto);

    // Verificar se um produto já está vinculado a uma campanha
    boolean existsByCampanhaAndProduto(Campanha campanha, Produto produto);

    // Remover vínculo de produto em uma campanha
    void deleteByCampanhaAndProduto(Campanha campanha, Produto produto);
}
