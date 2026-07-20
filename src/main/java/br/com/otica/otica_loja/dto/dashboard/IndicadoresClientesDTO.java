package br.com.otica.otica_loja.dto.dashboard;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class IndicadoresClientesDTO {
    // Getters e Setters
    private long totalClientes;
    private long clientesBronze;
    private long clientesPrata;
    private long clientesOuro;
    private long clientesDiamante;
    private long clientesEngajados;
    private long clientesInativos;

}
