# Quick Deploy Local - Entrega 1

### First time / Cleanup
```bash
./gradlew clean build installDist
```

### Build Docker Image
```bash
docker build -t vehiculos-api .
```

### Run with Docker Compose
```bash
docker-compose -f backend/docker-compose.yml up -d
```

### Verify
```bash
curl -i http://localhost:8081/health
```

### Logs
```bash
docker-compose -f backend/docker-compose.yml logs -f app
```
