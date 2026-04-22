package com.nimbachi.banco_app.domain.exception;

public class CupoExcedidoException extends RuntimeException {
    public CupoExcedidoException(String mensaje) {
        super(mensaje);
    }

    public CupoExcedidoException() {
        super("Cupo diario Excedido");
    }
}
