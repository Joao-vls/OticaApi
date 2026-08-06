package br.com.otica.otica_loja.UseCases.dashboard;

import br.com.otica.otica_loja.Entity.Admin.LogAcesso;
import br.com.otica.otica_loja.Repository.Admin.LogAcessoRepository;
import br.com.otica.otica_loja.dto.dashboard.RegistrarLogAcessoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrarAcessoUseCase {

    @Autowired
    private LogAcessoRepository logAcessoRepository;

    public void executar(RegistrarLogAcessoDTO dto) {
        LogAcesso log = new LogAcesso();
        log.setUsuarioId(dto.getUsuarioId());
        log.setSessionId(dto.getSessionId());
        log.setIp(dto.getIp());
        log.setUserAgent(dto.getUserAgent());
        log.setRota(dto.getRota());
        log.setMetodo(dto.getMetodo() != null ? dto.getMetodo() : "GET");

        logAcessoRepository.save(log);
    }
}