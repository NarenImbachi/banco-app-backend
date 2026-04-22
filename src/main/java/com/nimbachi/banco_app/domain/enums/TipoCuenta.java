package com.nimbachi.banco_app.domain.enums;

public enum TipoCuenta {
    AHORRO("Ahorro"),
    CORRIENTE("Corriente");

    private String descripcion;

    TipoCuenta(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
