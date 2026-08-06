package br.com.otica.otica_loja.dto.dashboard;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class RegistrarLogAcessoDTO {
    private UUID usuarioId;
    private String sessionId;
    private String ip;
    private String userAgent;
    private String rota;
    private String metodo;
}