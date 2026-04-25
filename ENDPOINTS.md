# Endpoints API - Banco App Backend

Documentacion funcional de los endpoints expuestos por el backend.

## Base URL

```text
http://localhost:8080
```

Prefijo comun:

```text
/api
```

## Formato de respuesta

### Exito

```json
{
  "success": true,
  "message": "Operacion exitosa.",
  "code": "OK",
  "data": {}
}
```

### Error

```json
{
  "success": false,
  "message": "Descripcion del error",
  "code": "VALIDATION_ERROR",
  "status": 400,
  "path": "/api/clientes"
}
```

## Clientes

### Crear cliente

`POST /api/clientes`

Body:

```json
{
  "nombre": "Jose Lema",
  "genero": "M",
  "edad": 45,
  "identificacion": "1234567890",
  "direccion": "Otavalo sn y principal",
  "telefono": "0982547850",
  "clienteId": "CLI001",
  "contrasena": "1234",
  "estado": true
}
```

Validaciones:

- `nombre`: obligatorio, entre 3 y 100 caracteres
- `genero`: obligatorio, solo `M` o `F`
- `edad`: obligatoria, entre 18 y 100
- `identificacion`: obligatoria, entre 5 y 20 caracteres
- `direccion`: obligatoria
- `telefono`: obligatorio, 10 digitos
- `clienteId`: obligatorio
- `contrasena`: obligatoria, entre 4 y 20 caracteres
- `estado`: obligatorio

Respuesta exitosa:

```json
{
  "success": true,
  "message": "Cliente creado exitosamente",
  "code": "OK",
  "data": {
    "id": 1,
    "nombre": "Jose Lema",
    "genero": "M",
    "edad": 45,
    "identificacion": "1234567890",
    "direccion": "Otavalo sn y principal",
    "telefono": "0982547850",
    "clienteId": "CLI001",
    "estado": true,
    "estadoTexto": "ACTIVO",
    "cuentas": []
  }
}
```

### Listar clientes

`GET /api/clientes`

Respuesta:

```json
{
  "success": true,
  "message": "Clientes obtenidos exitosamente",
  "code": "OK",
  "data": []
}
```

### Obtener cliente por ID

`GET /api/clientes/{id}`

Ejemplo:

```text
GET /api/clientes/1
```

Posibles respuestas:

- `200 OK` si existe
- `404 Not Found` con codigo `CLIENTE_NOT_FOUND` si no existe

### Actualizar cliente

`PUT /api/clientes/{id}`

Body:

```json
{
  "nombre": "Jose Lema Actualizado",
  "direccion": "Nueva direccion 123",
  "telefono": "0999999999",
  "estado": true
}
```

Nota:

- El request DTO exige los campos del body.
- La logica actual del controlador actualiza `nombre`, `direccion` y `telefono`.

### Eliminar cliente

`DELETE /api/clientes/{id}`

Respuesta exitosa:

```json
{
  "success": true,
  "message": "Cliente eliminado exitosamente",
  "code": "CLIENTE_DELETED"
}
```

## Cuentas

### Crear cuenta

`POST /api/cuentas`

Body:

```json
{
  "numeroCuenta": "478758",
  "tipo": "AHORRO",
  "saldoInicial": 2000,
  "estado": true,
  "clienteId": 1
}
```

Validaciones:

- `numeroCuenta`: obligatorio
- `tipo`: obligatorio, `AHORRO` o `CORRIENTE`
- `saldoInicial`: obligatorio
- `estado`: obligatorio
- `clienteId`: obligatorio

Reglas:

- el cliente debe existir
- el numero de cuenta debe ser unico
- el saldo inicial no puede ser negativo

### Listar cuentas

`GET /api/cuentas`

### Obtener cuenta por ID

`GET /api/cuentas/{id}`

Si no existe:

```json
{
  "success": false,
  "message": "Cuenta no encontrada",
  "code": "CUENTA_NOT_FOUND",
  "status": 404,
  "path": "/api/cuentas/99"
}
```

### Actualizar cuenta

`PUT /api/cuentas/{id}`

Body:

```json
{
  "tipo": "CORRIENTE",
  "estado": true
}
```

### Eliminar cuenta

`DELETE /api/cuentas/{id}`

Regla:

- no se puede eliminar si tiene movimientos registrados

Respuesta exitosa:

```json
{
  "success": true,
  "message": "Cuenta eliminada exitosamente",
  "code": "CUENTA_DELETED"
}
```

## Movimientos

### Registrar movimiento

`POST /api/movimientos`

Body para deposito:

```json
{
  "fecha": "2026-04-24",
  "tipo": "DEPOSITO",
  "valor": 500,
  "cuentaId": 1
}
```

Body para retiro:

```json
{
  "fecha": "2026-04-24",
  "tipo": "RETIRO",
  "valor": -200,
  "cuentaId": 1
}
```

Reglas:

- `DEPOSITO` debe ir con valor positivo
- `RETIRO` debe ir con valor negativo
- si el saldo no alcanza, retorna `Saldo no disponible`
- si supera el limite diario, retorna `Cupo diario Excedido`

Nota tecnica:

- el request exige `fecha`, pero la fecha definitiva del movimiento la asigna el dominio al registrar la operacion

Respuesta exitosa:

```json
{
  "success": true,
  "message": "Movimiento registrado exitosamente",
  "code": "OK",
  "data": {
    "id": 1,
    "fecha": "2026-04-24",
    "tipo": "DEPOSITO",
    "valor": 500,
    "saldo": 1500,
    "cuentaId": 1,
    "cuentaNumero": null
  }
}
```

Errores comunes:

```json
{
  "success": false,
  "message": "Saldo no disponible",
  "code": "BUSINESS_LOGIC_ERROR",
  "status": 400,
  "path": "/api/movimientos"
}
```

```json
{
  "success": false,
  "message": "Cupo diario Excedido",
  "code": "BUSINESS_LOGIC_ERROR",
  "status": 400,
  "path": "/api/movimientos"
}
```

### Eliminar movimiento

`DELETE /api/movimientos/{id}`

Si no existe:

```json
{
  "success": false,
  "message": "Movimiento no encontrado",
  "code": "MOVIMIENTO_NOT_FOUND",
  "status": 404,
  "path": "/api/movimientos/99"
}
```

### Listado formateado de movimientos

`GET /api/movimientos/listado`

Respuesta:

```json
{
  "success": true,
  "message": "Movimientos listados correctamente",
  "code": "OK",
  "data": [
    {
      "id": 1,
      "cuentaId": 1,
      "fecha": "2026-04-24",
      "numeroCuenta": "478758",
      "tipoCuenta": "AHORRO",
      "saldoInicial": 2000.0,
      "estado": true,
      "movimiento": "DEPOSITO 500"
    }
  ]
}
```

## Reportes

### Estado de cuenta por cliente y rango de fechas

`GET /api/reportes/estado-cuenta`

Parametros:

- `clienteId`: obligatorio
- `fechaInicio`: obligatorio, formato `yyyy-MM-dd`
- `fechaFin`: obligatorio, formato `yyyy-MM-dd`

Ejemplo:

```text
GET /api/reportes/estado-cuenta?clienteId=2&fechaInicio=2022-02-08&fechaFin=2022-02-10
```

Respuesta:

```json
{
  "success": true,
  "message": "Reporte generado exitosamente",
  "code": "OK",
  "data": [
    {
      "fecha": "2022-02-10",
      "cliente": "Marianela Montalvo",
      "numeroCuenta": "225487",
      "tipoCuenta": "CORRIENTE",
      "saldoInicial": 100,
      "estado": true,
      "movimiento": 600,
      "saldoDisponible": 700
    }
  ]
}
```

## Codigos HTTP frecuentes

| Codigo | Uso |
|---|---|
| `200` | Consulta, actualizacion o eliminacion exitosa |
| `201` | Creacion exitosa |
| `400` | Error de validacion o regla de negocio |
| `404` | Recurso no encontrado |
| `500` | Error inesperado del servidor |

## Codigos internos usados en la API

| Codigo | Significado |
|---|---|
| `OK` | Operacion exitosa |
| `VALIDATION_ERROR` | Error de validacion |
| `BUSINESS_LOGIC_ERROR` | Error de negocio |
| `CLIENTE_NOT_FOUND` | Cliente no encontrado |
| `CUENTA_NOT_FOUND` | Cuenta no encontrada |
| `MOVIMIENTO_NOT_FOUND` | Movimiento no encontrado |
| `CLIENTE_DELETED` | Cliente eliminado |
| `CUENTA_DELETED` | Cuenta eliminada |
| `MOVIMIENTO_DELETED` | Movimiento eliminado |

## Ejemplos rapidos con cURL

Crear cliente:

```bash
curl -X POST http://localhost:8080/api/clientes \
  -H "Content-Type: application/json" \
  -d "{\"nombre\":\"Jose Lema\",\"genero\":\"M\",\"edad\":45,\"identificacion\":\"1234567890\",\"direccion\":\"Otavalo sn y principal\",\"telefono\":\"0982547850\",\"clienteId\":\"CLI001\",\"contrasena\":\"1234\",\"estado\":true}"
```

Crear cuenta:

```bash
curl -X POST http://localhost:8080/api/cuentas \
  -H "Content-Type: application/json" \
  -d "{\"numeroCuenta\":\"478758\",\"tipo\":\"AHORRO\",\"saldoInicial\":2000,\"estado\":true,\"clienteId\":1}"
```

Registrar deposito:

```bash
curl -X POST http://localhost:8080/api/movimientos \
  -H "Content-Type: application/json" \
  -d "{\"fecha\":\"2026-04-24\",\"tipo\":\"DEPOSITO\",\"valor\":500,\"cuentaId\":1}"
```

Consultar reporte:

```bash
curl "http://localhost:8080/api/reportes/estado-cuenta?clienteId=2&fechaInicio=2022-02-08&fechaFin=2022-02-10"
```
