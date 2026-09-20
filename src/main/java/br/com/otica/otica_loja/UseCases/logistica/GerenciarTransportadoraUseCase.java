package br.com.otica.otica_loja.UseCases.logistica;

import br.com.otica.otica_loja.Entity.Logistica.Transportadora;
import br.com.otica.otica_loja.Repository.Logistica.TransportadoraRepository;
import br.com.otica.otica_loja.dto.TransportadoraRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GerenciarTransportadoraUseCase {

    private final TransportadoraRepository transportadoraRepository;

    public Transportadora criar(TransportadoraRequest request) {
        if (transportadoraRepository.existsByCodigo(request.codigo())) {
            throw new IllegalArgumentException("Já existe uma transportadora com o código: " + request.codigo());
        }

        Transportadora transportadora = new Transportadora();
        transportadora.setNome(request.nome());
        transportadora.setCodigo(request.codigo());
        transportadora.setAtivo(request.ativo());

        return transportadoraRepository.save(transportadora);
    }

    public Transportadora atualizar(UUID id, TransportadoraRequest request) {
        Transportadora transportadora = transportadoraRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transportadora não encontrada."));

        // Se o código mudou, valida se já não existe outro igual
        if (!transportadora.getCodigo().equals(request.codigo()) &&
                transportadoraRepository.existsByCodigo(request.codigo())) {
            throw new IllegalArgumentException("Já existe uma transportadora com o código: " + request.codigo());
        }

        transportadora.setNome(request.nome());
        transportadora.setCodigo(request.codigo());
        transportadora.setAtivo(request.ativo());

        return transportadoraRepository.save(transportadora);
    }
}