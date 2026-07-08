package br.com.otica.otica_loja.Repository.Catalogo;

import br.com.otica.otica_loja.Entity.Catalogo.Lente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LenteRepository extends JpaRepository<Lente, UUID> {

    // Buscar lente pelo nome
    Optional<Lente> findByNome(String nome);

    // Verificar se já existe lente com determinado nome
    boolean existsByNome(String nome);

    // Buscar todas as lentes ativas
    List<Lente> findByAtivoTrue();

    // Buscar todas as lentes inativas
    List<Lente> findByAtivoFalse();

    // Buscar lentes por índice (ex.: 1.50, 1.67, 1.74)
    List<Lente> findByIndice(BigDecimal indice);

    // Buscar lentes por faixa de preço
    List<Lente> findByPrecoBetween(BigDecimal min, BigDecimal max);
}
