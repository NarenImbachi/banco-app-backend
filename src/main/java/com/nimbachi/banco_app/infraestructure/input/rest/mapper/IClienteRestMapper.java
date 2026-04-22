package com.nimbachi.banco_app.infraestructure.input.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.nimbachi.banco_app.domain.model.Cuenta;
import com.nimbachi.banco_app.domain.model.Cliente;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.request.CreateClienteRequest;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.CuentaResponse;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.ClienteResponse;

@Mapper(componentModel = "spring")
public interface IClienteRestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cuentas", ignore = true)
    Cliente requestToDomain(CreateClienteRequest request);

    ClienteResponse domainToResponse(Cliente domain);

    @Mapping(target = "fechaApertura", ignore = true)
    CuentaResponse domainToResponse(Cuenta domain);

    List<ClienteResponse> domainListToResponseList(List<Cliente> domainList);
}
