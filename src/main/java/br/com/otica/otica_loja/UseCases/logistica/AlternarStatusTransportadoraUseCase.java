package br.com.otica.otica_loja.UseCases.logistica;

import br.com.otica.otica_loja.Entity.Logistica.Transportadora;
import br.com.otica.otica_loja.Repository.Logistica.TransportadoraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlternarStatusTransportadoraUseCase {

    private final TransportadoraRepository transportadoraRepository;

    public Transportadora alternarStatus(UUID id) {
        Transportadora transportadora = transportadoraRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transportadora não encontrada."));

        transportadora.setAtivo(!transportadora.getAtivo()); // Inverte o valor booleano
        return transportadoraRepository.save(transportadora);
    }
}