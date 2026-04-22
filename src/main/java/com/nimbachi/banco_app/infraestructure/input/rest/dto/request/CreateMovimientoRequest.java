package com.nimbachi.banco_app.infraestructure.input.rest.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.nimbachi.banco_app.domain.enums.TipoMovimiento;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMovimientoRequest {

    @NotNull(message = "La fecha es requerida")
    private LocalDate fecha; 

    @NotNull(message = "El tipo de movimiento es requerido")
    private TipoMovimiento tipo;

    @NotNull(message = "El valor es requerido")
    private BigDecimal valor;

    @NotNull(message = "El ID de la cuenta es requerido")
    private Long cuentaId;
}