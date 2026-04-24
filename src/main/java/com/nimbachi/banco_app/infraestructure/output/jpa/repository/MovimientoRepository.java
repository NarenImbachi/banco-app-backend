package com.nimbachi.banco_app.infraestructure.output.jpa.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.MovimientoListadoResponse;
import com.nimbachi.banco_app.infraestructure.output.jpa.entity.MovimientoEntity;

@Repository
public interface MovimientoRepository extends JpaRepository<MovimientoEntity, Long> {
    List<MovimientoEntity> findByCuentaId(Long cuentaId);

    List<MovimientoEntity> findByCuentaIdAndFechaBetween(Long cuentaId, LocalDate fechaInicio, LocalDate fechaFin);

    List<MovimientoEntity> findByCuentaIdAndFecha(Long cuentaId, LocalDate fecha);

    @Query("""
                SELECT new com.nimbachi.banco_app.infraestructure.input.rest.dto.response.MovimientoListadoResponse(
                    m.id,
                    c.id,
                    m.fecha,
                    c.numeroCuenta,
                    c.tipo,
                    c.saldoInicial,
                    c.estado,
                    CASE
                        WHEN m.valor < 0
                            THEN CONCAT('Retiro de ', ABS(m.valor))
                        ELSE
                            CONCAT('Depósito de ', ABS(m.valor))
                    END
                )
                FROM MovimientoEntity m
                JOIN m.cuenta c
                ORDER BY m.fecha DESC
            """)
    List<MovimientoListadoResponse> obtenerListadoMovimientos();

    @Query("""
                SELECT m
                FROM MovimientoEntity m
                JOIN m.cuenta c
                WHERE c.id = :cuentaId
                  AND m.fecha = :fecha
                  AND m.valor < 0
            """)
    List<MovimientoEntity> findRetirosByCuentaIdAndFecha(Long cuentaId, LocalDate fecha);
}
