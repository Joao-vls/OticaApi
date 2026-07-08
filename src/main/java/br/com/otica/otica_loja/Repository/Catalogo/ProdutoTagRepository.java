package br.com.otica.otica_loja.Repository.Catalogo;

import br.com.otica.otica_loja.Entity.Catalogo.ProdutoTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProdutoTagRepository extends JpaRepository<ProdutoTag, UUID> {

    // Buscar tag pelo nome
    Optional<ProdutoTag> findByNome(String nome);

    // Verificar se já existe tag com determinado nome
    boolean existsByNome(String nome);

    // Buscar todas as tags que contenham parte do nome
    List<ProdutoTag> findByNomeContainingIgnoreCase(String nome);
}
