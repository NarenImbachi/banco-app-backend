package com.nimbachi.banco_app.infraestructure.input.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClienteRequest {
    
    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;
    
    @NotBlank(message = "La dirección no puede estar vacía")
    private String direccion;
    
    @NotBlank(message = "El teléfono no puede estar vacío")
    private String telefono;
    
    @NotNull(message = "El estado es requerido")
    private boolean estado;
}