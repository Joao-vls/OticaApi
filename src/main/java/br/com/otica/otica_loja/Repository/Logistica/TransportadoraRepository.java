package br.com.otica.otica_loja.Repository.Logistica;

import br.com.otica.otica_loja.Entity.Logistica.Transportadora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransportadoraRepository extends JpaRepository<Transportadora, UUID> {

    // Buscar transportadora pelo nome
    Optional<Transportadora> findByNome(String nome);

    // Buscar transportadora pelo código único
    Optional<Transportadora> findByCodigo(String codigo);

    // Verificar se já existe transportadora com determinado código
    boolean existsByCodigo(String codigo);

    // Buscar transportadoras ativas
    List<Transportadora> findByAtivoTrue();

    // Buscar transportadoras inativas
    List<Transportadora> findByAtivoFalse();
}
