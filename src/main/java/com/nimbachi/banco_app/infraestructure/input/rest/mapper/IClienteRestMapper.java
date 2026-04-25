package com.nimbachi.banco_app.infraestructure.input.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.nimbachi.banco_app.domain.model.Cliente;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.request.CreateClienteRequest;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.request.UpdateClienteRequest;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.ClienteResponse;

@Mapper(componentModel = "spring")
public interface IClienteRestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cuentas", ignore = true)
    @Mapping(target = "estado", expression = "java(true)") // Por defecto, el cliente se crea como activo
    @Mapping(target = "clienteId", source = "clienteId")
    @Mapping(target = "contrasena", source = "contrasena")
    @Mapping(target = "nombre", source = "nombre")
    Cliente requestToDomain(CreateClienteRequest request);

    @Mapping(
        target = "estadoTexto",
        expression = "java(domain.isEstado() ? \"Activo\" : \"Inactivo\")"
    )
    @Mapping(target = "cuentas", ignore = true)
    ClienteResponse domainToResponse(Cliente domain);

    List<ClienteResponse> domainListToResponseList(List<Cliente> domainList);

    Object updateRequestToDomain(UpdateClienteRequest any);
}
