package com.nimbachi.banco_app.infraestructure.input.rest.dto.request;

import com.nimbachi.banco_app.domain.enums.TipoCuenta;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCuentaRequest {
    
    private TipoCuenta tipo;
    
    @NotNull(message = "El estado es requerido")
    private boolean estado;
}
