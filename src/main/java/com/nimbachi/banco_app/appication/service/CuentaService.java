package com.nimbachi.banco_app.appication.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nimbachi.banco_app.appication.input.ICuentaCommandUseCase;
import com.nimbachi.banco_app.appication.input.ICuentaQueryUseCase;
import com.nimbachi.banco_app.appication.output.IClientePersistencePort;
import com.nimbachi.banco_app.appication.output.ICuentaPersistencePort;
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
    public List<Cuenta> obtenerPorCliente(Long clienteId) {
        log.debug("Obteniendo cuentas para el cliente con ID: {}", clienteId);
        return cuentaPersistencePort.findByClienteId(clienteId);
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
    public Cuenta crearCuenta(Cuenta cuenta) {
        log.info("Creando nueva cuenta: {}", cuenta.getNumeroCuenta());

        // Validar que el cliente existe
        if (!clientePersistencePort.findById(cuenta.getClienteId()).isPresent()) {
            throw new RuntimeException("El cliente con ID " + cuenta.getClienteId() + " no existe");
        }

        // Validar que numeroCuenta sea único
        if (cuentaPersistencePort.existsByNumeroCuenta(cuenta.getNumeroCuenta())) {
            throw new RuntimeException("El número de cuenta " + cuenta.getNumeroCuenta() + " ya existe");
        }

        cuenta.setEstado(true);

        Cuenta cuentaCreada = cuentaPersistencePort.save(cuenta);
        log.info("Cuenta creada exitosamente. Número: {}, Cliente ID: {}",
                cuentaCreada.getNumeroCuenta(), cuentaCreada.getClienteId());

        return cuentaCreada;
    }

    @Override
    @Transactional
    public Cuenta actualizar(Long id, Cuenta cuenta) {
        log.info("Actualizando cuenta: {}", cuenta.getId());

        Optional<Cuenta> cuentaExistente = cuentaPersistencePort.findById(cuenta.getId());
        if (cuentaExistente.isEmpty()) {
            throw new RuntimeException("La cuenta con ID " + cuenta.getId() + " no existe");
        }

        Cuenta cuentabd = cuentaExistente.get();

        if (!cuentabd.getNumeroCuenta().equals(cuenta.getNumeroCuenta())) {
            throw new RuntimeException("No se puede cambiar el número de cuenta");
        }

        if (!cuentabd.getClienteId().equals(cuenta.getClienteId())) {
            throw new RuntimeException("No se puede cambiar el cliente de la cuenta");
        }

        cuentabd.setTipo(cuenta.getTipo());
        cuentabd.setEstado(cuenta.isEstado());

        Cuenta cuentaActualizada = cuentaPersistencePort.save(cuentabd);
        log.info("Cuenta actualizada exitosamente. Número: {}, Estado: {}", cuentaActualizada.getNumeroCuenta(), cuentaActualizada.isEstado());

        return cuentaActualizada;
    }

    @Override
    @Transactional
    public void eliminar(Long cuentaId) {
        log.info("Eliminando cuenta con ID: {}", cuentaId);

        Optional<Cuenta> cuentaExistente = cuentaPersistencePort.findById(cuentaId);
        if (cuentaExistente.isEmpty()) {
            throw new RuntimeException("La cuenta con ID " + cuentaId + " no existe");
        }

        Cuenta cuenta = cuentaExistente.get();

        if (cuenta.getMovimientos() != null && !cuenta.getMovimientos().isEmpty()) {
            throw new RuntimeException("No se puede eliminar una cuenta que tiene movimientos registrados");
        }

        cuentaPersistencePort.delete(cuentaId);
        log.info("Cuenta eliminada exitosamente. Número: {}", cuenta.getNumeroCuenta());
    }

}
