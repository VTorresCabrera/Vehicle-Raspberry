# Stage 1: Build
FROM gradle:8.12-jdk21 AS build
WORKDIR /app
COPY . .
RUN ./gradlew installDist --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:21-jre
WORKDIR /app
# the installDist task creates the executable in build/install/project-name/bin
# we copy the entire install directory
COPY --from=build /app/build/install/vehiculos-raspi /app
EXPOSE 8081
ENTRYPOINT ["/app/bin/vehiculos-raspi"]
