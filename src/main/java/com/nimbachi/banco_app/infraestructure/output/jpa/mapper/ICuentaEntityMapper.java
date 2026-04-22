package com.nimbachi.banco_app.infraestructure.output.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.nimbachi.banco_app.domain.model.Cuenta;
import com.nimbachi.banco_app.infraestructure.output.jpa.entity.ClienteEntity;
import com.nimbachi.banco_app.infraestructure.output.jpa.entity.CuentaEntity;

@Mapper(componentModel = "spring", uses = {IMovimientoEntityMapper.class})
public interface ICuentaEntityMapper {

    // Entity → Domain
    @Mapping(source = "cliente.id", target = "clienteId")
    Cuenta toDomain(CuentaEntity entity);

    // Domain → Entity
    @Mapping(source = "clienteId", target = "cliente.id")
    CuentaEntity toEntity(Cuenta domain);

    // 🔥 ESTE MÉTODO ES CLAVE
    default ClienteEntity map(Long clienteId) {
        if (clienteId == null) return null;

        ClienteEntity cliente = new ClienteEntity();
        cliente.setId(clienteId);
        return cliente;
    }

}