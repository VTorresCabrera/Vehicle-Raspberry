# Vehicle Raspberry Backend

Backend REST hecho con Kotlin y Ktor para una aplicación de compraventa de vehículos. La idea del proyecto es ofrecer una API sencilla pero bastante completa: gestión de usuarios, login con JWT, publicación de vehículos, favoritos y subida de imágenes.

El nombre del repositorio viene de que está pensado para poder desplegarse también en entornos ligeros como una Raspberry Pi, aunque durante el desarrollo se puede ejecutar sin problema en un PC normal con Docker o desde IntelliJ.

## Objetivo del proyecto

Este proyecto forma parte de un trabajo de 2º DAM orientado a practicar una API real con:

- arquitectura por capas
- persistencia en base de datos
- autenticación
- subida de ficheros
- despliegue con contenedores

No es un ejemplo mínimo de clase. La API ya cubre un caso de uso bastante cercano a una aplicación real.

## Qué hace la API

La aplicación permite:

- registrar usuarios y hacer login
- generar y validar tokens JWT
- consultar vehículos publicados
- filtrar vehículos por marca, texto, estado y rango de precio
- crear, editar y borrar vehículos asociados a un usuario
- subir imagen de perfil y de vehículo
- guardar vehículos favoritos por usuario

## Tecnologías usadas

- Kotlin
- Ktor
- Exposed
- MariaDB
- H2
- JWT
- BCrypt
- Docker y Docker Compose
- Gradle Kotlin DSL

## Estructura del proyecto

```text
src/main/kotlin/com/example/
├── ktor/                  # Configuración principal de Ktor
├── domain/                # Modelos, use cases, repositorios y seguridad
└── data/persistence/      # Tablas, DAO y repositorios de acceso a datos

src/main/resources/
├── application.conf       # Configuración principal usada por Ktor
├── application.yaml       # Configuración alternativa
└── static/                # Recursos estáticos

backend/
└── docker-compose.yml     # Entorno con API + MariaDB + phpMyAdmin
```

## Modelo de datos principal

### Usuario

Un usuario tiene, entre otros, estos campos:

- `id`
- `username`
- `email`
- `password`
- `description`
- `phone`
- `urlImage`
- `active`
- `role`
- `token`

### Vehículo

Un vehículo guarda:

- `id`
- `marca`
- `modelo`
- `año`
- `precio`
- `kilometros`
- `potencia`
- `imagen`
- `status`
- `userId`

## Puesta en marcha

### Opción recomendada: Docker Compose

Es la forma más cómoda porque la configuración actual de la base de datos apunta al host `db`, que es el nombre del servicio de MariaDB dentro de Docker.

```bash
docker compose -f backend/docker-compose.yml up --build
```

Servicios disponibles:

- API: `http://localhost:8081`
- phpMyAdmin: `http://localhost:8000`
- MariaDB expuesto en host: puerto `3307`

Credenciales de la base de datos definidas en el `docker-compose`:

- base de datos: `dbVehicle`
- usuario: `usuario`
- contraseña: `usuario`

### Opción local desde IDE o terminal

Requisitos:

- Java 21
- una base de datos MariaDB disponible

La configuración activa está en `src/main/resources/application.conf` y ahora mismo usa:

```conf
url = "jdbc:mariadb://db:3306/dbVehicle"
driver = "org.mariadb.jdbc.Driver"
username = "usuario"
password = "usuario"
```

Si vas a ejecutar la API fuera de Docker, lo normal es cambiar `db` por `localhost` y usar el puerto que tengas abierto en tu máquina, por ejemplo `3307` si levantas MariaDB con el `docker-compose`.

Después puedes arrancar la aplicación con:

```bash
bash ./gradlew run
```

La API escucha en:

```text
http://0.0.0.0:8081
```

## Autenticación

La API usa JWT para proteger las rutas de usuarios, vehículos privados y favoritos.

Flujo básico:

1. El cliente se registra o hace login.
2. El servidor devuelve un token.
3. Ese token se envía en la cabecera `Authorization`.

Ejemplo:

```http
Authorization: Bearer TU_TOKEN
```

## Endpoints principales

### Públicos

| Método | Ruta | Descripción |
| --- | --- | --- |
| `GET` | `/` | Comprueba que la API responde |
| `GET` | `/health` | Health check |
| `GET` | `/vehicles` | Lista global de vehículos |
| `GET` | `/vehicles?brand=Toyota&minPrice=10000&maxPrice=30000` | Filtros de búsqueda |
| `GET` | `/vehicles/brand/{brand}` | Vehículos por marca |
| `GET` | `/vehicle/{id}` | Vehículo por id |
| `POST` | `/register` | Registro de usuario |
| `POST` | `/login` | Login y obtención de token |
| `POST` | `/upload/{id}` | Subida de imagen de perfil |

### Protegidos con JWT

| Método | Ruta | Descripción |
| --- | --- | --- |
| `GET` | `/users` | Listado de usuarios |
| `GET` | `/users/{id}` | Obtener un usuario |
| `PUT` | `/users/{id}` | Actualizar usuario |
| `DELETE` | `/users/{id}` | Borrar usuario |
| `GET` | `/users/{userId}/vehicles` | Vehículos del usuario |
| `POST` | `/users/{userId}/vehicles` | Crear vehículo |
| `DELETE` | `/users/{userId}/vehicles` | Borrar todos los vehículos del usuario |
| `GET` | `/users/{userId}/vehicles/{vehicleId}` | Obtener un vehículo concreto |
| `PUT` | `/users/{userId}/vehicles/{vehicleId}` | Actualizar vehículo |
| `DELETE` | `/users/{userId}/vehicles/{vehicleId}` | Borrar vehículo |
| `POST` | `/users/{userId}/vehicles/{vehicleId}/upload` | Subir imagen del vehículo |
| `GET` | `/users/{userId}/favorites` | Listar favoritos |
| `GET` | `/users/{userId}/favorites/ids` | Listar ids favoritos |
| `POST` | `/users/{userId}/favorites/{vehicleId}` | Añadir favorito |
| `DELETE` | `/users/{userId}/favorites/{vehicleId}` | Quitar favorito |

## Ejemplos rápidos

### Registrar usuario

```http
POST /register
Content-Type: application/json

{
  "id": "user_test",
  "username": "Victor",
  "email": "victor@test.com",
  "password": "123456",
  "description": "Usuario de prueba",
  "phone": "600123456"
}
```

### Login

```http
POST /login
Content-Type: application/json

{
  "email": "victor@test.com",
  "password": "123456"
}
```

### Crear vehículo para un usuario autenticado

```http
POST /users/user_test/vehicles
Authorization: Bearer TU_TOKEN
Content-Type: application/json

{
  "id": "vehicle_test",
  "marca": "Toyota",
  "modelo": "Corolla",
  "año": 2023,
  "precio": 28000.0,
  "kilometros": 5000,
  "potencia": 140,
  "userId": "user_test"
}
```

## Archivos útiles para probar la API

- `test_api.http`
- `test_api_v2.http`

Se pueden abrir directamente desde IntelliJ IDEA o Android Studio y lanzar las peticiones una a una.

## Estado actual

Actualmente el backend ya incluye:

- conexión a base de datos
- creación de tablas
- autenticación con JWT
- hash de contraseña con BCrypt en actualizaciones
- gestión de favoritos
- subida de imágenes
- despliegue con Docker

## Mejoras pendientes

Algunas mejoras razonables para seguir evolucionando el proyecto:

- añadir tests automáticos de rutas y repositorios
- validar mejor los datos de entrada
- separar documentación de la API con Swagger u OpenAPI
- mover secretos y configuración sensible a variables de entorno reales
- controlar permisos por rol de forma más estricta

## Autoría

Proyecto de 2º DAM centrado en construir un backend funcional y entendible, intentando aplicar una estructura limpia y tecnologías que sí se usan en proyectos reales.
