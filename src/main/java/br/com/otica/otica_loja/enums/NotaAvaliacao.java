package br.com.otica.otica_loja.enums;

public enum NotaAvaliacao {
    UM(1), DOIS(2), TRES(3), QUATRO(4), CINCO(5);

    private final int valor;

    NotaAvaliacao(int valor) { this.valor = valor; }
    public int getValor() { return valor; }

    public static NotaAvaliacao fromInt(int valor) {
        return switch (valor) {
            case 1 -> UM;
            case 2 -> DOIS;
            case 3 -> TRES;
            case 4 -> QUATRO;
            case 5 -> CINCO;
            default -> throw new IllegalArgumentException("Nota inválida: " + valor);
        };
    }
}
