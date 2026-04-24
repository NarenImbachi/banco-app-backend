package com.nimbachi.banco_app.domain.exception;

public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException(String mensaje) {
        super(mensaje);
    }

    public SaldoInsuficienteException() {
        super("Saldo insuficiente para realizar la operación");
    }
}
