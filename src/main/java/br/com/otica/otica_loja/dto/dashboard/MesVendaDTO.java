package br.com.otica.otica_loja.dto.dashboard;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class MesVendaDTO {
    private String nome;
    private List<DiaVendaDTO> dias;
}