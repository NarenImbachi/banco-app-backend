package com.nimbachi.banco_app.infraestructure.output.jpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nimbachi.banco_app.infraestructure.output.jpa.entity.ClienteEntity;

@Repository
public interface ClienteRepository extends JpaRepository<ClienteEntity, Long> {
    Optional<ClienteEntity> findByClienteId(String clienteId);
    boolean existsByClienteId(String clienteId);
}
