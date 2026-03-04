# Vehículos API (Ktor) – Entrega 1

API REST en **Ktor (Kotlin)** para gestionar vehículos.  
En esta **Entrega 1** el objetivo es tener la API **sin login/seguridad**, dockerizada y lista para desplegar después en Raspberry Pi.

---

## Objetivo Entrega 1
- API funcionando en local.
- Endpoint de salud: `GET /health`.
- CRUD de vehículos (sin autenticación).
- Dockerfile para construir y ejecutar la API.
- (Opcional según avance) Docker Compose con MariaDB para entorno local.
- Pruebas de endpoints con Postman o curl.

---

## Requisitos
- **JDK 21**
- **Docker** y **Docker Compose**
- (Opcional) Postman

---

## Ejecutar en local (sin Docker)
Desde la raíz del proyecto:

```bash
./gradlew run
```

Comprobar que está viva:

```bash
curl -i http://localhost:8081/health
```

## Ejecutar en local (con Docker)
### Build de la imagen
```bash
docker build -t vehiculos-api:local .
```

### Ejecutar contenedor
```bash
docker run --rm -p 8081:8081 vehiculos-api:local
```

### Comprobar salud:
```bash
curl -i http://localhost:8081/health
```

## Ejecutar en local con Docker Compose (API + MariaDB)

Útil si el backend ya está conectado a base de datos.

1. **Crear tu archivo .env a partir del ejemplo (no se sube a Git):**
```bash
cp .env.example .env
```

2. **Levantar servicios:**
```bash
docker compose -f backend/docker-compose.yml up -d
```

3. **Comprobar:**
```bash
docker ps
curl -i http://localhost:8081/health
```

4. **Parar servicios:**
```bash
docker compose -f backend/docker-compose.yml down
```

---

## Endpoints

- **GET /health** → devuelve el estado del servicio (200 + JSON).
- **CRUD de vehículos** (cuando esté implementado):
    - **GET /vehicles**
    - **GET /vehicles/{id}**
    - **POST /vehicles**
    - **PUT /vehicles/{id}**
    - **DELETE /vehicles/{id}**

---

## Configuración

La configuración principal está en `src/main/resources/application.yaml`:
- **host**: 0.0.0.0
- **port**: 8081

Variables de entorno (si se usa MariaDB en local) están documentadas en `.env.example`.
