package br.com.otica.otica_loja.Repository.Admin;

import br.com.otica.otica_loja.Entity.Admin.LogAcesso;
import br.com.otica.otica_loja.dto.dashboard.AcessoHorarioProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface LogAcessoRepository extends JpaRepository<LogAcesso, UUID> {

    // Query nativa ajustada com o schema admin.logs_acesso
    @Query(value = """
        SELECT 
            LPAD(EXTRACT(HOUR FROM criado_em AT TIME ZONE 'America/Sao_Paulo')::text, 2, '0') || ':00-' ||
            LPAD(CASE WHEN EXTRACT(HOUR FROM criado_em AT TIME ZONE 'America/Sao_Paulo') = 23 THEN '00' ELSE (EXTRACT(HOUR FROM criado_em AT TIME ZONE 'America/Sao_Paulo') + 1)::text END, 2, '0') || ':00' AS horario,
            COUNT(DISTINCT COALESCE(session_id, ip)) AS quantidade
        FROM admin.logs_acesso
        WHERE criado_em >= :dataInicio AND criado_em <= :dataFim
        GROUP BY EXTRACT(HOUR FROM criado_em AT TIME ZONE 'America/Sao_Paulo')
        ORDER BY EXTRACT(HOUR FROM criado_em AT TIME ZONE 'America/Sao_Paulo') ASC
    """, nativeQuery = true)
    List<AcessoHorarioProjection> buscarAcessosPorHorario(
            @Param("dataInicio") OffsetDateTime dataInicio,
            @Param("dataFim") OffsetDateTime dataFim
    );

    // Buscar logs por usuário
    List<LogAcesso> findByUsuarioId(UUID usuarioId);

    // Buscar logs por IP
    List<LogAcesso> findByIp(String ip);

    // Buscar logs por rota
    List<LogAcesso> findByRota(String rota);

    // Buscar logs por método (GET, POST, PUT, DELETE)
    List<LogAcesso> findByMetodo(String metodo);

    @Query("SELECT COUNT(l.id) FROM LogAcesso l WHERE l.criadoEm >= :inicio AND l.criadoEm <= :fim")
    long contarAcessosNoPeriodo(@Param("inicio") OffsetDateTime inicio, @Param("fim") OffsetDateTime fim);

    @Query("SELECT COUNT(DISTINCT COALESCE(l.sessionId, l.ip)) FROM LogAcesso l WHERE l.criadoEm >= :inicio AND l.criadoEm <= :fim")
    long contarVisitantesUnicosNoPeriodo(@Param("inicio") OffsetDateTime inicio, @Param("fim") OffsetDateTime fim);

    public interface RotaAcessoProjection {
        String getRota();
        Long getQuantidade();
    }

    @Query("SELECT l.rota as rota, COUNT(l.id) as quantidade " +
            "FROM LogAcesso l " +
            "WHERE l.criadoEm >= :inicio AND l.criadoEm <= :fim " +
            "GROUP BY l.rota ORDER BY quantidade DESC")
    List<RotaAcessoProjection> buscarTopRotas(@Param("inicio") OffsetDateTime inicio, @Param("fim") OffsetDateTime fim, org.springframework.data.domain.Pageable pageable);
}