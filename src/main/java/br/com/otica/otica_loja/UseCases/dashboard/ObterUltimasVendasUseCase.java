package br.com.otica.otica_loja.UseCases.dashboard;

import br.com.otica.otica_loja.Repository.Pedidos.PedidoItemRepository;
import br.com.otica.otica_loja.dto.dashboard.UltimaVendaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable; // ✅ Import do Spring Data correto
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ObterUltimasVendasUseCase {

    @Autowired
    private PedidoItemRepository pedidoItemRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public List<UltimaVendaDTO> executar(int limite) {
        return pedidoItemRepository.obterUltimasVendas(PageRequest.of(0, limite))
                .stream()
                .map(p -> new UltimaVendaDTO(
                        p.getProduct(),
                        p.getClient(),
                        p.getLocation(),
                        p.getDateTime() != null ? p.getDateTime().format(FORMATTER) : ""
                ))
                .collect(Collectors.toList());
    }
}