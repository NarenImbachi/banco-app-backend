package com.nimbachi.banco_app.infraestructure.input.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.nimbachi.banco_app.domain.model.Movimiento;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.request.CreateMovimientoRequest;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.MovimientoResponse;

@Mapper(componentModel = "spring")
public interface IMovimientoRestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "saldo", ignore = true)
    Movimiento requestToDomain(CreateMovimientoRequest request);

    @Mapping(target = "cuentaNumero", ignore = true)
    MovimientoResponse domainToResponse(Movimiento domain);

    List<MovimientoResponse> domainListToResponseList(List<Movimiento> domainList);
}
