-- BaseDatos.sql
-- Script de esquema y datos base para la prueba tecnica Banco App.
-- Este archivo esta pensado para PostgreSQL y para ejecutarse sobre una base ya creada
-- con nombre banco_db. En Docker Compose, la base se crea con POSTGRES_DB=banco_db
-- y este script se ejecuta automaticamente al iniciar el contenedor por primera vez.

CREATE TABLE IF NOT EXISTS clientes (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    genero VARCHAR(30),
    edad INTEGER,
    identificacion VARCHAR(50) NOT NULL UNIQUE,
    direccion VARCHAR(255),
    telefono VARCHAR(30),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    cliente_id VARCHAR(50) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    estado BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS cuentas (
    id BIGSERIAL PRIMARY KEY,
    numero_cuenta VARCHAR(20) NOT NULL UNIQUE,
    tipo VARCHAR(20) NOT NULL,
    saldo_inicial NUMERIC(15,2) NOT NULL,
    saldo_disponible NUMERIC(15,2) NOT NULL,
    estado BOOLEAN NOT NULL,
    cliente_id BIGINT NOT NULL,
    CONSTRAINT fk_cuentas_clientes
        FOREIGN KEY (cliente_id)
        REFERENCES clientes(id)
        ON DELETE RESTRICT,
    CONSTRAINT chk_cuentas_tipo
        CHECK (tipo IN ('AHORRO', 'CORRIENTE')),
    CONSTRAINT chk_cuentas_saldo_inicial
        CHECK (saldo_inicial >= 0),
    CONSTRAINT chk_cuentas_saldo_disponible
        CHECK (saldo_disponible >= 0)
);

CREATE TABLE IF NOT EXISTS movimientos (
    id BIGSERIAL PRIMARY KEY,
    fecha DATE NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    valor NUMERIC(15,2) NOT NULL,
    saldo NUMERIC(15,2) NOT NULL,
    cuenta_id BIGINT NOT NULL,
    CONSTRAINT fk_movimientos_cuentas
        FOREIGN KEY (cuenta_id)
        REFERENCES cuentas(id)
        ON DELETE RESTRICT,
    CONSTRAINT chk_movimientos_tipo
        CHECK (tipo IN ('DEPOSITO', 'RETIRO'))
);

CREATE INDEX IF NOT EXISTS idx_cuentas_cliente_id ON cuentas(cliente_id);
CREATE INDEX IF NOT EXISTS idx_movimientos_cuenta_id ON movimientos(cuenta_id);
CREATE INDEX IF NOT EXISTS idx_movimientos_fecha ON movimientos(fecha);

-- Datos semilla basados en el enunciado de la prueba.
INSERT INTO clientes (
    id, nombre, genero, edad, identificacion, direccion, telefono,
    created_at, updated_at, cliente_id, contrasena, estado
)
VALUES
    (1, 'Jose Lema', 'MASCULINO', 35, '1718137159', 'Otavalo sn y principal', '098254785', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'CL-001', '1234', TRUE),
    (2, 'Marianela Montalvo', 'FEMENINO', 28, '1718137160', 'Amazonas y NNUU', '097548965', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'CL-002', '5678', TRUE),
    (3, 'Juan Osorio', 'MASCULINO', 40, '1718137161', '13 junio y Equinoccial', '098874587', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'CL-003', '1245', TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO cuentas (
    id, numero_cuenta, tipo, saldo_inicial, saldo_disponible, estado, cliente_id
)
VALUES
    (1, '478758', 'AHORRO', 2000.00, 1425.00, TRUE, 1),
    (2, '225487', 'CORRIENTE', 100.00, 700.00, TRUE, 2),
    (3, '495878', 'AHORRO', 0.00, 150.00, TRUE, 3),
    (4, '496825', 'AHORRO', 540.00, 0.00, TRUE, 2),
    (5, '585545', 'CORRIENTE', 1000.00, 1000.00, TRUE, 1)
ON CONFLICT (id) DO NOTHING;

INSERT INTO movimientos (
    id, fecha, tipo, valor, saldo, cuenta_id
)
VALUES
    (1, DATE '2022-02-10', 'RETIRO', -575.00, 1425.00, 1),
    (2, DATE '2022-02-10', 'DEPOSITO', 600.00, 700.00, 2),
    (3, DATE '2022-02-09', 'DEPOSITO', 150.00, 150.00, 3),
    (4, DATE '2022-02-08', 'RETIRO', -540.00, 0.00, 4)
ON CONFLICT (id) DO NOTHING;

SELECT setval('clientes_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM clientes), 1), TRUE);
SELECT setval('cuentas_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM cuentas), 1), TRUE);
SELECT setval('movimientos_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM movimientos), 1), TRUE);
