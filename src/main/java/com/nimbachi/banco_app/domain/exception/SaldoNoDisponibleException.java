package com.nimbachi.banco_app.domain.exception;

public class SaldoNoDisponibleException extends RuntimeException {
    public SaldoNoDisponibleException(String mensaje) {
        super(mensaje);
    }

    public SaldoNoDisponibleException() {
        super("Saldo no disponible");
    }
}
