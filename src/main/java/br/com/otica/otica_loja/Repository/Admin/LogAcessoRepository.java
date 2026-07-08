package br.com.otica.otica_loja.Repository.Admin;

import br.com.otica.otica_loja.Entity.Admin.LogAcesso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LogAcessoRepository extends JpaRepository<LogAcesso, UUID> {

    // Buscar logs por usuário
    List<LogAcesso> findByUsuarioId(UUID usuarioId);

    // Buscar logs por IP
    List<LogAcesso> findByIp(String ip);

    // Buscar logs por rota
    List<LogAcesso> findByRota(String rota);

    // Buscar logs por método (GET, POST, PUT, DELETE)
    List<LogAcesso> findByMetodo(String metodo);
}
