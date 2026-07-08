package br.com.otica.otica_loja.UseCases.logistica;

import br.com.otica.otica_loja.Entity.Logistica.Envio;
import br.com.otica.otica_loja.Entity.Logistica.EnvioEvento;
import br.com.otica.otica_loja.Repository.Logistica.EnvioEventoRepository;
import br.com.otica.otica_loja.Repository.Logistica.EnvioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BuscarRastreamentoUseCase {

    @Autowired
    private EnvioRepository envioRepository;

    @Autowired
    private EnvioEventoRepository envioEventoRepository;

    /**
     * Retorna todos os eventos de rastreamento de um envio.
     */
    public List<EnvioEvento> buscarEventos(UUID envioId) {
        // 1. Buscar envio
        Envio envio = envioRepository.findById(envioId)
                .orElseThrow(() -> new IllegalArgumentException("Envio não encontrado."));

        // 2. Buscar eventos ordenados por data
        return envioEventoRepository.findByEnvioOrderByCriadoEmAsc(envio);
    }
}
