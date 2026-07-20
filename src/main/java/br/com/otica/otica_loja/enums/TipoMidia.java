package br.com.otica.otica_loja.enums;


import com.fasterxml.jackson.annotation.JsonCreator;

public enum TipoMidia {
    IMAGE,
    VIDEO,
    THREE_D;
    @JsonCreator
    public static TipoMidia fromString(String value) {
        return TipoMidia.valueOf(value.toUpperCase());
    }
}