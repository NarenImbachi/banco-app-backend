# Banco App Backend

API REST para la gestion bancaria de la prueba tecnica Full-Stack BP, construida con Spring Boot 3, Java 17 y PostgreSQL.

## Resumen

Este proyecto implementa la capa backend para administrar:

- Clientes
- Cuentas
- Movimientos
- Reportes de estado de cuenta

Tambien aplica reglas de negocio bancarias como validacion de saldo disponible, limite diario de retiros y almacenamiento del saldo resultante por transaccion.

## Stack tecnologico

- Java 17
- Spring Boot 3.5.13
- Spring Web
- Spring Data JPA
- PostgreSQL
- MapStruct
- Lombok
- JUnit 5
- Mockito
- Docker

## Caracteristicas implementadas

- API REST con respuestas uniformes mediante `ApiResponse`
- Persistencia con JPA/Hibernate
- Arquitectura por capas con enfoque hexagonal
- Repository Pattern mediante puertos y adaptadores
- DTOs para entrada y salida
- Validacion de request con Jakarta Validation
- Manejo global de excepciones con `@RestControllerAdvice`
- Pruebas unitarias y pruebas web de controladores
- Dockerfile para empaquetado del backend

## Estructura del proyecto

```text
banco-app/
|-- src/main/java/com/nimbachi/banco_app/
|   |-- application/
|   |   |-- input/        # Puertos de entrada
|   |   |-- output/       # Puertos de salida
|   |   `-- service/      # Casos de uso
|   |-- domain/
|   |   |-- enums/        # Tipos de cuenta y movimiento
|   |   |-- exception/    # Excepciones de negocio
|   |   `-- model/        # Modelo de dominio
|   `-- infraestructure/
|       |-- input/rest/
|       |   |-- controller/
|       |   |-- dto/
|       |   `-- mapper/
|       `-- output/
|           |-- config/
|           |-- exception/
|           `-- jpa/
|               |-- adapter/
|               |-- entity/
|               |-- mapper/
|               `-- repository/
|-- src/main/resources/application.yaml
|-- src/test/java/
|-- Dockerfile
|-- BaseDatos.sql
|-- ENDPOINTS.md
`-- pom.xml
```

## Arquitectura

El proyecto separa responsabilidades en tres bloques principales:

- `domain`: contiene el modelo y las reglas de negocio.
- `application`: orquesta casos de uso mediante puertos de entrada y salida.
- `infraestructure`: expone endpoints REST, implementa adaptadores JPA y maneja detalles tecnicos.

Patrones y decisiones usadas:

- `Repository Pattern`: desacopla dominio de persistencia.
- `Hexagonal Architecture`: el dominio no depende de controladores ni de JPA.
- `DTO Pattern`: evita exponer directamente entidades internas en operaciones de entrada.
- `Global Exception Handling`: centraliza errores de validacion y negocio.

## Modelo funcional

### Cliente

Representa al usuario bancario e incluye datos personales y de acceso:

- nombre
- genero
- edad
- identificacion
- direccion
- telefono
- clienteId
- contrasena
- estado

### Cuenta

Representa una cuenta bancaria asociada a un cliente:

- numeroCuenta
- tipo
- saldoInicial
- saldoDisponible
- estado
- clienteId

### Movimiento

Representa una transaccion sobre una cuenta:

- fecha
- tipo
- valor
- saldo
- cuentaId

## Reglas de negocio implementadas

- Los depositos deben manejar valores positivos.
- Los retiros deben manejar valores negativos.
- Cada movimiento guarda el saldo disponible resultante.
- Si un retiro deja el saldo por debajo de cero, la operacion falla con `Saldo no disponible`.
- El limite diario de retiro por cuenta es `1000`.
- Si el retiro diario acumulado supera ese tope, la operacion falla con `Cupo diario Excedido`.
- No se puede crear una cuenta para un cliente inexistente.
- No se puede crear una cuenta con numero repetido.
- No se puede eliminar una cuenta que tenga movimientos registrados.

## Configuracion

La aplicacion se configura mediante variables de entorno en [application.yaml](/C:/Users/naren/OneDrive/Desktop/Naren%20Unicauca/CHAMBA/PruebaTecnicaDevsuNimbachi/banco-app/src/main/resources/application.yaml).

Valores por defecto:

| Variable | Valor por defecto | Descripcion |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/banco_db` | URL de conexion a PostgreSQL |
| `DB_USER` | `postgres` | Usuario de base de datos |
| `DB_PASSWORD` | `root` | Contrasena de base de datos |
| `DB_DRIVER` | `org.postgresql.Driver` | Driver JDBC |
| `DB_HIBERNATE_DDL_AUTO` | `update` | Estrategia de Hibernate |
| `DB_HIBERNATE_DIALECT` | `org.hibernate.dialect.PostgreSQLDialect` | Dialecto SQL |
| `PORT` | `8080` | Puerto HTTP de la API |

## Requisitos

Para ejecucion local:

- Java 17 o superior
- Maven 3.8 o superior
- PostgreSQL

Verificacion rapida:

```bash
java -version
mvn -version
psql --version
```

## Base de datos

La base relacional esperada por el backend es `banco_db`.

El archivo [BaseDatos.sql](/C:/Users/naren/OneDrive/Desktop/Naren%20Unicauca/CHAMBA/PruebaTecnicaDevsuNimbachi/banco-app/BaseDatos.sql) se incluye como script de esquema y datos base. Esto permite:

- reproducir el entorno de ejecucion
- crear la estructura sin depender de auto-creacion por Hibernate
- disponer de datos semilla para pruebas funcionales

## Ejecucion local

1. Crear la base de datos `banco_db` en PostgreSQL.
2. Ejecutar `BaseDatos.sql`.
3. Configurar variables de entorno si deseas cambiar los valores por defecto.
4. Iniciar la aplicacion.

En Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

En terminal bash:

```bash
./mvnw spring-boot:run
```

La API queda disponible en:

```text
http://localhost:8080
```

## Construccion y pruebas

Compilar el proyecto:

```bash
./mvnw clean install
```

Ejecutar pruebas:

```bash
./mvnw test
```

Pruebas destacadas presentes en el proyecto:

- `ClienteControllerTest`
- `CuentaControllerTest`
- `MovimientoControllerTest`
- `ReporteControllerTest`
- `ClienteServiceTest`
- `CuentaServiceTest`
- `MovimientoServiceTest`
- `ReporteServiceTest`
- `ClienteTest`
- `CuentaTest`
- `MovimientoTest`

## Docker

El backend incluye un [Dockerfile](/C:/Users/naren/OneDrive/Desktop/Naren%20Unicauca/CHAMBA/PruebaTecnicaDevsuNimbachi/banco-app/Dockerfile) multi-stage para construir una imagen liviana.

Construir imagen:

```bash
docker build -t banco-app-backend .
```

Ejecutar contenedor:

```bash
docker run -p 8080:8080 ^
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/banco_db ^
  -e DB_USER=postgres ^
  -e DB_PASSWORD=root ^
  banco-app-backend
```

## Endpoints

La documentacion detallada se encuentra en [ENDPOINTS.md](/C:/Users/naren/OneDrive/Desktop/Naren%20Unicauca/CHAMBA/PruebaTecnicaDevsuNimbachi/banco-app/ENDPOINTS.md).

Resumen rapido:

### Clientes

- `POST /api/clientes`
- `GET /api/clientes`
- `GET /api/clientes/{id}`
- `PUT /api/clientes/{id}`
- `DELETE /api/clientes/{id}`

### Cuentas

- `POST /api/cuentas`
- `GET /api/cuentas`
- `GET /api/cuentas/{id}`
- `PUT /api/cuentas/{id}`
- `DELETE /api/cuentas/{id}`

### Movimientos

- `POST /api/movimientos`
- `DELETE /api/movimientos/{id}`
- `GET /api/movimientos/listado`

### Reportes

- `GET /api/reportes/estado-cuenta`

## Formato general de respuesta

Respuesta exitosa:

```json
{
  "success": true,
  "message": "Operacion exitosa.",
  "code": "OK",
  "data": {}
}
```

Respuesta de error:

```json
{
  "success": false,
  "message": "Descripcion del error",
  "code": "VALIDATION_ERROR",
  "status": 400,
  "path": "/api/clientes"
}
```

## Manejo de errores

El proyecto cuenta con manejo global de excepciones para:

- errores de validacion de request
- errores de logica de negocio
- errores inesperados del servidor

Codigos frecuentes:

- `VALIDATION_ERROR`
- `BUSINESS_LOGIC_ERROR`
- `CLIENTE_NOT_FOUND`
- `CUENTA_NOT_FOUND`
- `MOVIMIENTO_NOT_FOUND`
- `CLIENTE_DELETED`
- `CUENTA_DELETED`
- `MOVIMIENTO_DELETED`

## Notas tecnicas importantes

- El backend usa PostgreSQL por defecto y H2 solo en pruebas.
- El endpoint de movimientos recibe `fecha` en el request DTO, pero la fecha final del movimiento es registrada por el dominio al momento de crear la operacion.
- Las respuestas de `GET /api/clientes/{id}` y `GET /api/cuentas/{id}` retornan el modelo de dominio envuelto en `ApiResponse`.
- Los retiros deben enviarse con valor negativo para que cumplan la regla de negocio implementada en el dominio.

## Autor
Ing. Naren Alejandro Imbachi Quinayas

Proyecto backend desarrollado para la prueba tecnica bancaria Full-Stack BP.
