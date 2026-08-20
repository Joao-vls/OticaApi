package br.com.otica.otica_loja.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String tipo; // "Bearer"
    private OffsetDateTime expiraEm;
    private List<String> roles; // Ex: ["ADMIN"] ou ["GERENTE"]
}