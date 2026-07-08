package br.com.otica.otica_loja.Repository.FAQ;

import br.com.otica.otica_loja.Entity.FAQ.FaqItem;
import br.com.otica.otica_loja.Entity.FAQ.FaqCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface FaqItemRepository extends JpaRepository<FaqItem, UUID> {

    // Buscar itens por categoria
    List<FaqItem> findByCategoria(FaqCategoria categoria);

    // Buscar itens ativos
    List<FaqItem> findByAtivoTrue();

    // Buscar itens inativos
    List<FaqItem> findByAtivoFalse();

    // Buscar itens ordenados por ordem
    List<FaqItem> findAllByOrderByOrdemAsc();

    // Buscar itens de uma categoria ordenados por ordem
    List<FaqItem> findByCategoriaOrderByOrdemAsc(FaqCategoria categoria);

    // Buscar itens mais visualizados
    List<FaqItem> findByVisualizacoesGreaterThan(Long minVisualizacoes);

    // Buscar itens criados após uma data
    List<FaqItem> findByCriadoEmAfter(OffsetDateTime data);

    // Buscar itens atualizados após uma data
    List<FaqItem> findByAtualizadoEmAfter(OffsetDateTime data);
}
