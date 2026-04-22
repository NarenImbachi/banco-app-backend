package com.nimbachi.banco_app.infraestructure.output.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.nimbachi.banco_app.domain.model.Cliente;
import com.nimbachi.banco_app.infraestructure.output.jpa.entity.ClienteEntity;

@Mapper(componentModel = "spring")
public interface IClienteEntityMapper {

    @Mapping(target = "cuentas", ignore = true)
    Cliente toDomain(ClienteEntity entity);

    @Mapping(target = "cuentas", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ClienteEntity toEntity(Cliente domain);
}