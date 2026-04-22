package com.nimbachi.banco_app.domain.enums;

public enum TipoMovimiento {
    DEPOSITO("Depósito", 1),
    RETIRO("Retiro", -1);

    private String descripcion;
    private Integer signo;

    TipoMovimiento(String descripcion, Integer signo) {
        this.descripcion = descripcion;
        this.signo = signo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Integer getSigno() {
        return signo;
    }
}
