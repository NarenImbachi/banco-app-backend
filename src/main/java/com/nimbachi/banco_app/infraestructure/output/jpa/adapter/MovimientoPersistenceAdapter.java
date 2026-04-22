package com.nimbachi.banco_app.infraestructure.output.jpa.adapter;

import com.nimbachi.banco_app.appication.output.IMovimientoPersistencePort;
import com.nimbachi.banco_app.domain.model.Movimiento;
import com.nimbachi.banco_app.infraestructure.output.jpa.entity.MovimientoEntity;
import com.nimbachi.banco_app.infraestructure.output.jpa.mapper.IMovimientoEntityMapper;
import com.nimbachi.banco_app.infraestructure.output.jpa.repository.MovimientoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class MovimientoPersistenceAdapter implements IMovimientoPersistencePort {

    private final MovimientoRepository movimientoRepository;
    private final IMovimientoEntityMapper movimientoEntityMapper;

    @Override
    public Movimiento save(Movimiento movimiento) {
        log.debug("Persistiendo movimiento en cuenta ID: {}", movimiento.getCuentaId());
        MovimientoEntity entity = movimientoEntityMapper.domainToEntity(movimiento);
        MovimientoEntity saved = movimientoRepository.save(entity);
        return movimientoEntityMapper.entityToDomain(saved);
    }

    @Override
    public Optional<Movimiento> findById(Long movimientoId) {
        log.debug("Buscando movimiento por ID: {}", movimientoId);
        return movimientoRepository.findById(movimientoId)
                .map(movimientoEntityMapper::entityToDomain);
    }

    @Override
    public List<Movimiento> findAll() {
        log.debug("Obteniendo todos los movimientos");
        return movimientoRepository.findAll().stream()
                .map(movimientoEntityMapper::entityToDomain)
                .toList();
    }

    @Override
    public List<Movimiento> findByCuentaId(Long cuentaId) {
        log.debug("Obteniendo movimientos de la cuenta ID: {}", cuentaId);
        return movimientoRepository.findByCuentaId(cuentaId).stream()
                .map(movimientoEntityMapper::entityToDomain)
                .toList();
    }

    @Override
    public List<Movimiento> findByCuentaIdAndFechaBetween(Long cuentaId, LocalDate fechaInicio, LocalDate fechaFin) {
        log.debug("Obteniendo movimientos de cuenta ID: {} entre {} y {}", cuentaId, fechaInicio, fechaFin);
        return movimientoRepository.findByCuentaIdAndFechaBetween(cuentaId, fechaInicio, fechaFin).stream()
                .map(movimientoEntityMapper::entityToDomain)
                .toList();
    }

    @Override
    public List<Movimiento> findByCuentaIdAndFecha(Long cuentaId, LocalDate fecha) {
        log.debug("Obteniendo movimientos de cuenta ID: {} en fecha {}", cuentaId, fecha);
        return movimientoRepository.findByCuentaIdAndFecha(cuentaId, fecha).stream()
                .map(movimientoEntityMapper::entityToDomain)
                .toList();
    }

    @Override
    public void delete(Long movimientoId) {
        log.debug("Eliminando movimiento con ID: {}", movimientoId);
        movimientoRepository.deleteById(movimientoId);
    }
}
