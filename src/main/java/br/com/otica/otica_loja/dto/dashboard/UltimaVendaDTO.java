package br.com.otica.otica_loja.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UltimaVendaDTO {
    private String product;
    private String client;
    private String location;
    private String dateTime;

}