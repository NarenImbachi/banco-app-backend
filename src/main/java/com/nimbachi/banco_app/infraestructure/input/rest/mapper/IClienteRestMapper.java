package com.nimbachi.banco_app.infraestructure.input.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.nimbachi.banco_app.domain.model.Cliente;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.request.CreateClienteRequest;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.ClienteResponse;

@Mapper(componentModel = "spring")
public interface IClienteRestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cuentas", ignore = true)
    Cliente requestToDomain(CreateClienteRequest request);

    @Mapping(
        target = "estadoTexto",
        expression = "java(domain.getEstado() ? \"Activo\" : \"Inactivo\")"
    )
    @Mapping(target = "cuentas", ignore = true)
    ClienteResponse domainToResponse(Cliente domain);

    List<ClienteResponse> domainListToResponseList(List<Cliente> domainList);
}
