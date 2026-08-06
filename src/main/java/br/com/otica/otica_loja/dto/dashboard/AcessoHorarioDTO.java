package br.com.otica.otica_loja.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AcessoHorarioDTO {
    private String label; // Ex: "14:00-15:00"
    private Long value;  // Ex: 365
}