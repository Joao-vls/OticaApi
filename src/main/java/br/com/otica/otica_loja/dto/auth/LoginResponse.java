package br.com.otica.otica_loja.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String tipo; // Ex: "Bearer"
    private OffsetDateTime expiraEm;
}