package br.com.otica.otica_loja.dto.dashboard;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class DiaVendaDTO {
    private LocalDate data;
    private long quantidade;
    private String classeCor;
    private List<String> produtos;
}