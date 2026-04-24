package com.nimbachi.banco_app.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nimbachi.banco_app.application.input.ICuentaCommandUseCase;
import com.nimbachi.banco_app.application.input.ICuentaQueryUseCase;
import com.nimbachi.banco_app.application.output.IClientePersistencePort;
import com.nimbachi.banco_app.application.output.ICuentaPersistencePort;
import com.nimbachi.banco_app.domain.model.Cuenta;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.CuentaResponse;
import com.nimbachi.banco_app.infraestructure.input.rest.mapper.ICuentaRestMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CuentaService implements ICuentaCommandUseCase, ICuentaQueryUseCase {

    private final IClientePersistencePort clientePersistencePort;
    private final ICuentaPersistencePort cuentaPersistencePort;
    private final ICuentaRestMapper cuentaRestMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<Cuenta> obtenerPorId(Long id) {
        log.debug("Obteniendo cuenta con ID: {}", id);
        return cuentaPersistencePort.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaResponse> listarTodas() {
        log.debug("Listando todas las cuentas");
        return cuentaPersistencePort.findAll().stream()
                .map(cuentaRestMapper::domainToResponse)
                .toList();
    }

    @Override
    @Transactional
    public CuentaResponse crearCuenta(Cuenta cuenta) {
        log.info("Creando nueva cuenta: {}", cuenta.getNumeroCuenta());

        // Validar que el cliente existe
        if (!clientePersistencePort.findById(cuenta.getClienteId()).isPresent()) {
            throw new RuntimeException("El cliente con ID " + cuenta.getClienteId() + " no existe");
        }

        // Validar que numeroCuenta sea único
        if (cuentaPersistencePort.existsByNumeroCuenta(cuenta.getNumeroCuenta())) {
            throw new RuntimeException("El número de cuenta " + cuenta.getNumeroCuenta() + " ya existe");
        }

        Cuenta nuevaCuenta = Cuenta.crear(
            cuenta.getNumeroCuenta(),
            cuenta.getTipo(),
            cuenta.getSaldoInicial(),
            cuenta.getClienteId()
        );


        Cuenta cuentaCreada = cuentaPersistencePort.save(nuevaCuenta);
        log.info("Cuenta creada exitosamente. Número: {}, Cliente ID: {}",
                cuentaCreada.getNumeroCuenta(), cuentaCreada.getClienteId());

        return cuentaRestMapper.domainToResponse(cuentaCreada);
    }

    @Override
    @Transactional
    public CuentaResponse actualizar(Long id, Cuenta cuentaActualizada) {
        log.info("Actualizando cuenta con ID: {}", id);

        Cuenta cuentaExistente = cuentaPersistencePort.findById(id).orElseThrow(() -> new RuntimeException("La cuenta con ID " + id + " no existe"));

        /*if (!cuentaExistente.getNumeroCuenta().equals(cuentaActualizada.getNumeroCuenta())) 
            throw new RuntimeException("No se puede cambiar el número de cuenta");
        
        if (!cuentaExistente.getClienteId().equals(cuentaActualizada.getClienteId())) 
            throw new RuntimeException("No se puede cambiar el cliente de la cuenta");*/
        

        cuentaExistente.actualizarDatos(
            cuentaActualizada.getTipo(),
            cuentaActualizada.isEstado()
        );

        Cuenta cuentaGuardada = cuentaPersistencePort.save(cuentaExistente);
        log.info("Cuenta actualizada exitosamente. Número: {}", cuentaGuardada.getNumeroCuenta());

        return cuentaRestMapper.domainToResponse(cuentaGuardada);
    }

    @Override
    @Transactional
    public void eliminar(Long cuentaId) {
        log.info("Eliminando cuenta con ID: {}", cuentaId);

        Cuenta cuenta = cuentaPersistencePort.findById(cuentaId)
            .orElseThrow(() -> new RuntimeException("La cuenta con ID " + cuentaId + " no existe"));

        if (cuenta.getMovimientos() != null && !cuenta.getMovimientos().isEmpty()) {
            throw new RuntimeException("No se puede eliminar una cuenta que tiene movimientos registrados");
        }

        cuentaPersistencePort.delete(cuentaId);
        log.info("Cuenta eliminada exitosamente. Número: {}", cuenta.getNumeroCuenta());
    }

}
