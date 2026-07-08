package br.com.otica.otica_loja.dto.dashboard;

public class IndicadoresClientesDTO {
    private long totalClientes;
    private long clientesBronze;
    private long clientesPrata;
    private long clientesOuro;
    private long clientesDiamante;
    private long clientesEngajados;
    private long clientesInativos;

    // Getters e Setters
    public long getTotalClientes() { return totalClientes; }
    public void setTotalClientes(long totalClientes) { this.totalClientes = totalClientes; }

    public long getClientesBronze() { return clientesBronze; }
    public void setClientesBronze(long clientesBronze) { this.clientesBronze = clientesBronze; }

    public long getClientesPrata() { return clientesPrata; }
    public void setClientesPrata(long clientesPrata) { this.clientesPrata = clientesPrata; }

    public long getClientesOuro() { return clientesOuro; }
    public void setClientesOuro(long clientesOuro) { this.clientesOuro = clientesOuro; }

    public long getClientesDiamante() { return clientesDiamante; }
    public void setClientesDiamante(long clientesDiamante) { this.clientesDiamante = clientesDiamante; }

    public long getClientesEngajados() { return clientesEngajados; }
    public void setClientesEngajados(long clientesEngajados) { this.clientesEngajados = clientesEngajados; }

    public long getClientesInativos() { return clientesInativos; }
    public void setClientesInativos(long clientesInativos) { this.clientesInativos = clientesInativos; }
}
