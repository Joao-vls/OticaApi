package br.com.otica.otica_loja.Repository.CMS;

import br.com.otica.otica_loja.Entity.CMS.VitrineProduto;
import br.com.otica.otica_loja.Entity.CMS.VitrineProdutoId;
import br.com.otica.otica_loja.Entity.CMS.Vitrine;
import br.com.otica.otica_loja.Entity.Catalogo.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VitrineProdutoRepository extends JpaRepository<VitrineProduto, VitrineProdutoId> {

    // Buscar todos os produtos de uma vitrine, ordenados pela ordem configurada
    List<VitrineProduto> findByVitrineOrderByOrdemAsc(Vitrine vitrine);

    // Buscar todas as vitrines em que um produto participa
    List<VitrineProduto> findByProduto(Produto produto);

    // Verificar se um produto já está vinculado a uma vitrine
    boolean existsByVitrineAndProduto(Vitrine vitrine, Produto produto);

    // Remover vínculo de produto em uma vitrine
    void deleteByVitrineAndProduto(Vitrine vitrine, Produto produto);
}
