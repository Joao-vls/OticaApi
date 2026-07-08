package br.com.otica.otica_loja.Repository.Catalogo;

import br.com.otica.otica_loja.Entity.Catalogo.Marca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, UUID> {

    // Buscar marca pelo nome
    Optional<Marca> findByNome(String nome);

    // Buscar marca pelo slug
    Optional<Marca> findBySlug(String slug);

    // Verificar se já existe marca com determinado nome
    boolean existsByNome(String nome);

    // Verificar se já existe marca com determinado slug
    boolean existsBySlug(String slug);

    // Buscar todas as marcas ativas
    List<Marca> findByAtivoTrue();

    // Buscar todas as marcas inativas
    List<Marca> findByAtivoFalse();

    // Buscar marcas que não foram deletadas (soft delete)
    List<Marca> findByDeletadoEmIsNull();


    List<Marca> findByAtivoTrueAndDeletadoEmIsNull();

    List<Marca> findByAtivoFalseAndDeletadoEmIsNull();
    // Buscar marcas deletadas (soft delete)
    List<Marca> findByDeletadoEmIsNotNull();
}
