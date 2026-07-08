package br.com.otica.otica_loja.Repository.Admin;

import br.com.otica.otica_loja.Entity.Admin.ConfiguracaoSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfiguracaoSistemaRepository extends JpaRepository<ConfiguracaoSistema, UUID> {

    // Buscar configuração pela chave
    Optional<ConfiguracaoSistema> findByChave(String chave);

    // Verificar se existe configuração com determinada chave
    boolean existsByChave(String chave);
}
