# Pruebas de Integración - Entrega 1

Este directorio contiene dos suites de pruebas para validar la API de Vehículos (Ktor).

## 1. Postman Collection

Ubicación: `tests/postman/VehiculosAPI-Entrega1.postman_collection.json`

### Importación
1. Importa el archivo JSON en Postman.

### Ejecución
1. Ejecuta la colección "VehiculosAPI-Entrega1".
2. Verifica que `baseUrl` sea `http://localhost:8081` (default).
3. Comprueba que todos los tests pasan (Status 200/201/204/404).

## 2. Script Bash (Curl)

Ubicación: `tests/curl/test_all.sh`

### Requisitos
- Entorno tipo Unix (Linux, macOS, Git Bash, WSL).
- `curl` instalado.
- **JSON Parser**: Se recomienda `jq`, pero si no está instalado, el script intentará usar `python` (o `python3`) automáticamente.

### Ejecución
```bash
# Permisos
chmod +x tests/curl/test_all.sh

# Ejecutar (localhost:8081 por defecto)
./tests/curl/test_all.sh
```

## 3. Cambio de Entorno (Remoto)

### Postman
Edita la variable de colección `baseUrl`.

### Curl
Define la variable de entorno `BASE_URL`:

```bash
export BASE_URL=http://192.168.1.50:8081
./tests/curl/test_all.sh
```
