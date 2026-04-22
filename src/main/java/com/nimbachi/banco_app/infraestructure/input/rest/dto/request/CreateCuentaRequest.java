package com.nimbachi.banco_app.infraestructure.input.rest.dto.request;

import java.math.BigDecimal;

import com.nimbachi.banco_app.domain.enums.TipoCuenta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCuentaRequest {

    @NotBlank(message = "El número de cuenta es requerido")
    private String numeroCuenta;

    @NotNull(message = "El tipo de cuenta es requerido")
    private TipoCuenta tipo;

    @NotNull(message = "El saldo inicial es requerido")
    private BigDecimal saldoInicial;

    @NotNull(message = "El estado es requerido")
    private boolean estado; // true para activo, false para inactivo

    @NotNull(message = "El cliente ID es requerido")
    private Long clienteId;
}
