# Docker Setup Guide

## Prerequisites
- Docker and Docker Compose installed
- At least 4GB RAM available for Docker

## Quick Start

### 1. Start all services
```bash
docker-compose up -d
```

This will start:
- PostgreSQL (port 5432) - Main database
- Judge0 Server (port 2358) - Code execution engine
- Judge0 PostgreSQL (internal) - Judge0 metadata
- Judge0 Redis (internal) - Judge0 queue
- Backend API (port 8080) - Spring Boot application

### 2. Check service status
```bash
docker-compose ps
```

### 3. View logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f backend
docker-compose logs -f judge0-server
docker-compose logs -f postgres
```

### 4. Stop services
```bash
docker-compose down
```

### 5. Stop and remove volumes (clean slate)
```bash
docker-compose down -v
```

## Testing Judge0 Directly

Once services are running, test Judge0:

```bash
# Submit code
curl -X POST http://localhost:2358/submissions \
  -H "Content-Type: application/json" \
  -d '{
    "source_code": "console.log(\"Hello, Judge0!\");",
    "language_id": 63
  }'

# Response will include a token like: {"token":"abc-123-def"}

# Get result (replace TOKEN with actual token from above)
curl http://localhost:2358/submissions/TOKEN?base64_encoded=false
```

## Testing Backend API

### 1. Register a user
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }'
```

### 2. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

Save the JWT token from the response.

### 3. Submit code
```bash
curl -X POST http://localhost:8080/api/submissions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "challengeId": 1,
    "code": "function add(a, b) { return a + b; }"
  }'
```

## Troubleshooting

### Judge0 not starting
- Check logs: `docker-compose logs judge0-server`
- Ensure judge0-db and judge0-redis are healthy
- Wait 30-60 seconds after `docker-compose up` for full initialization

### Backend can't connect to PostgreSQL
- Check if postgres container is healthy: `docker-compose ps`
- Verify environment variables in docker-compose.yml
- Check backend logs: `docker-compose logs backend`

### Port conflicts
If ports 5432, 2358, or 8080 are already in use:
- Edit docker-compose.yml and change the left side of port mappings
- Example: `"5433:5432"` instead of `"5432:5432"`

## Development Workflow

### Rebuild backend after code changes
```bash
docker-compose up -d --build backend
```

### Connect to PostgreSQL
```bash
docker exec -it codifai-postgres psql -U codifai -d codifai
```

### Execute commands in backend container
```bash
docker exec -it codifai-backend sh
```

## Resource Usage

Monitor Docker resource usage:
```bash
docker stats
```

Expected resource usage:
- Total: ~2-3GB RAM, ~20-30% CPU during code execution
- Backend: ~512MB RAM
- PostgreSQL: ~256MB RAM
- Judge0: ~1GB RAM
- Redis: ~128MB RAM
