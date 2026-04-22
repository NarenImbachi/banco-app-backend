package com.nimbachi.banco_app.infraestructure.input.rest.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateClienteRequest {

    @NotBlank(message = "El nombre es requerido")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @NotBlank(message = "El género es requerido")
    @Pattern(regexp = "[MF]", message = "El género debe ser M o F")
    private String genero;

    @NotNull(message = "La edad es requerida")
    @Min(value = 18, message = "Debe ser mayor de 18 años")
    @Max(value = 100, message = "La edad no puede ser mayor a 100")
    private Integer edad;

    @NotBlank(message = "La identificación es requerida")
    @Size(min = 5, max = 20, message = "La identificación debe tener entre 5 y 20 caracteres")
    private String identificacion;

    @NotBlank(message = "La dirección es requerida")
    private String direccion;

    @NotBlank(message = "El teléfono es requerido")
    @Pattern(regexp = "^[0-9]{10}$", message = "El teléfono debe tener 10 dígitos")
    private String telefono;

    @NotBlank(message = "El cliente ID es requerido")
    private String clienteId;

    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 4, max = 20, message = "La contraseña debe tener entre 4 y 20 caracteres")
    private String contraseña;

    @NotNull(message = "El estado es requerido")
    private boolean estado; // true para activo, false para inactivo
}
