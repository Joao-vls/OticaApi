package br.com.otica.otica_loja.Repository.Comercial;

import br.com.otica.otica_loja.Entity.Comercial.Cupom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CupomRepository extends JpaRepository<Cupom, UUID> {

    // Buscar cupom pelo código
    Optional<Cupom> findByCodigo(String codigo);

    // Verificar se já existe cupom com determinado código
    boolean existsByCodigo(String codigo);

    // Buscar cupons ativos
    List<Cupom> findByAtivoTrue();

    // Buscar cupons inativos
    List<Cupom> findByAtivoFalse();

    // Buscar cupons válidos (ativos, dentro da data e com quantidade disponível)
    @Query("SELECT c FROM Cupom c " +
            "WHERE c.ativo = true " +
            "AND c.dataInicio <= :now1 " +
            "AND c.dataFim >= :now2 " +
            "AND c.quantidadeUtilizada < c.quantidadeTotal")
    List<Cupom> findValidCupons(@Param("now1") OffsetDateTime now1,
                                @Param("now2") OffsetDateTime now2);


    // Buscar cupons expirados
    List<Cupom> findByDataFimBefore(OffsetDateTime now);

    // Buscar cupons futuros (não iniciados ainda)
    List<Cupom> findByDataInicioAfter(OffsetDateTime now);
}
