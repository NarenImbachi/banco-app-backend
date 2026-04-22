package com.nimbachi.banco_app.infraestructure.output.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.nimbachi.banco_app.domain.model.Movimiento;
import com.nimbachi.banco_app.infraestructure.output.jpa.entity.CuentaEntity;
import com.nimbachi.banco_app.infraestructure.output.jpa.entity.MovimientoEntity;

@Mapper(componentModel = "spring")
public interface IMovimientoEntityMapper {

    @Mapping(source = "cuenta.id", target = "cuentaId")
    Movimiento entityToDomain(MovimientoEntity entity);

    @Mapping(source = "cuentaId", target = "cuenta.id")
    MovimientoEntity domainToEntity(Movimiento domain);

    default CuentaEntity map(Long cuentaId) {
        if (cuentaId == null) return null;

        CuentaEntity cuenta = new CuentaEntity();
        cuenta.setId(cuentaId);
        return cuenta;
    }
}
