# Stage 1: Build
# Usamos una imagen base con JDK 21 que es lo que requiere el proyecto (build.gradle.kts)
# eclipse-temurin soporta multi-arquitectura, incluyendo ARM64 (Raspberry Pi 5)
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copiamos los ficheros necesarios para descargar dependencias
COPY gradle/ gradle/
COPY gradlew build.gradle.kts settings.gradle.kts gradle.properties ./

# Damos permisos de ejecución al wrapper
RUN chmod +x ./gradlew

# Descargamos las dependencias. Esto se cacheará si no cambian los ficheros de gradle
RUN ./gradlew dependencies --no-daemon

# Copiamos el código fuente
COPY src/ src/

# Compilamos y creamos la distribución
# Esto generará los ejecutables en /app/build/install/vehiculos-raspi
RUN ./gradlew installDist --no-daemon --info

# Stage 2: Runtime
# Imagen ligera para ejecución (JRE 21)
FROM eclipse-temurin:21-jre

WORKDIR /app

# Creamos un usuario no-root por seguridad
RUN groupadd --system app && useradd --system -g app app

# Copiamos la aplicación compilada desde la etapa anterior
# IMPORTANTE: El nombre del directorio coincide con rootProject.name en settings.gradle.kts ("vehiculos-raspi")
COPY --from=build /app/build/install/vehiculos-raspi /app

# Asignamos permisos al usuario app
RUN chown -R app:app /app

# Cambiamos al usuario app
USER app

# Exponemos el puerto
EXPOSE 8081

# Ejecutamos el script de arranque
CMD ["/app/bin/vehiculos-raspi"]

