package com.nimbachi.banco_app.infraestructure.input.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.nimbachi.banco_app.domain.model.Cuenta;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.request.CreateCuentaRequest;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.CuentaResponse;

@Mapper(componentModel = "spring")
public interface ICuentaRestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "movimientos", ignore = true)
    Cuenta requestToDomain(CreateCuentaRequest request);

    @Mapping(target = "fechaApertura", ignore = true)
    CuentaResponse domainToResponse(Cuenta domain);

    List<CuentaResponse> domainListToResponseList(List<Cuenta> domainList);
}
