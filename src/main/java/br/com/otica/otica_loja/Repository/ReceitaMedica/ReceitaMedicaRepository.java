package br.com.otica.otica_loja.Repository.ReceitaMedica;

import br.com.otica.otica_loja.Entity.ReceitaMedica.ReceitaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReceitaMedicaRepository extends JpaRepository<ReceitaMedica, UUID> {

    // Buscar receitas médicas de um usuário
    List<ReceitaMedica> findByUsuarioId(UUID usuarioId);

    // Buscar receitas médicas por nome de arquivo
    List<ReceitaMedica> findByNomeArquivoContainingIgnoreCase(String nomeArquivo);

    // Buscar receitas médicas criadas após uma data
    List<ReceitaMedica> findByCriadoEmAfter(OffsetDateTime data);

    // Buscar receitas médicas criadas antes de uma data
    List<ReceitaMedica> findByCriadoEmBefore(OffsetDateTime data);

    // Verificar se já existe receita médica com determinado arquivoPath
    boolean existsByArquivoPath(String arquivoPath);
}
