package br.com.otica.otica_loja.UseCases.logistica;

import br.com.otica.otica_loja.Entity.Logistica.Transportadora;
import br.com.otica.otica_loja.Repository.Logistica.TransportadoraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarTransportadorasUseCase {

    private final TransportadoraRepository transportadoraRepository;

    public List<Transportadora> listarTodas() {
        return transportadoraRepository.findAll();
    }

    public List<Transportadora> listarAtivas() {
        return transportadoraRepository.findByAtivoTrue();
    }
}