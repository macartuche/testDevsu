# DevSu - Sistema Bancario

Sistema bancario para gestión de clientes, cuentas y movimientos con Spring Boot y Angular.

**Repositorio:** https://github.com/macartuche/testDevsu

## Estructura del Proyecto

```
testDevsu/
├── backend/                    # API REST Spring Boot
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── ec/gob/loja/devsu/backend/
│   │   │   │       ├── controller/     # Controladores REST
│   │   │   │       ├── domain/
│   │   │   │       │   ├── entity/     # Entidades JPA (Persona, Cliente, Cuenta, Movimiento)
│   │   │   │       │   └── repository/ # Repositorios JPA
│   │   │   │       ├── dto/            # Data Transfer Objects
│   │   │   │       ├── mapper/         # Mapeadores DTO-Entidad
│   │   │   │       ├── service/        # Interfaces y lógica de negocio
│   │   │   │       └── exception/      # Excepciones personalizadas
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/                      # Pruebas unitarias
│   ├── BaseDatos.sql                  # Script de base de datos
│   ├── Dockerfile
│   └── pom.xml                        # Configuración Maven
│
├── frontend/                   # Aplicación Angular 17
│   ├── src/
│   │   ├── app/
│   │   │   ├── models/         # Interfaces TypeScript
│   │   │   ├── services/       # Servicios HTTP
│   │   │   └── pages/          # Componentes (clientes, cuentas, movimientos, reportes)
│   │   ├── styles.scss         # Estilos globales
│   │   └── main.ts
│   ├── Dockerfile
│   ├── nginx.conf              # Configuración Nginx
│   └── package.json
│
├── docker-compose.yml          # Orquestación de contenedores
└── DevSu.postman_collection.json # Colección Postman
```

## Tecnologías

| Componente | Tecnología |
|------------|-------------|
| Backend | Java 17, Spring Boot 3.x |
| Frontend | Angular 17, TypeScript |
| Base de Datos | PostgreSQL 15 |
| Contenedores | Docker, Docker Compose |
| API Testing | Postman |

## Compilación Local

### Backend (requiere Java 17+ y Maven)

```bash
cd backend

# Compilar
./mvnw clean package -DskipTests

# Ejecutar pruebas unitarias
./mvnw test

# Ejecutar aplicación (requiere PostgreSQL corriendo)
./mvnw spring-boot:run
```

### Frontend (requiere Node.js 18+)

```bash
cd frontend

# Instalar dependencias
npm install

# Ejecutar pruebas unitarias
npm test

# Iniciar servidor de desarrollo
npm start
```

## Docker

### Requisitos

- Docker Engine 20.10+
- Docker Compose 2.0+

### Levantar la aplicación completa

```bash
# Desde la raíz del proyecto
docker compose up --build

# Ejecutar en segundo plano
docker compose up -d --build
```

### Servicios

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| PostgreSQL | 5435 | Base de datos |
| Backend API | 8085 | API REST |
| Frontend | 80 | Aplicación web |

### URLs de acceso

- **Frontend**: http://localhost
- **API Backend**: http://localhost:8085/api
- **Swagger UI**: http://localhost:8085/swagger-ui.html

### Detener los servicios

```bash
docker compose down

# Eliminar volúmenes (base de datos)
docker compose down -v
```

## Colección Postman

El archivo `DevSu.postman_collection.json` contiene todos los endpoints para validación.

### Importar en Postman

1. Abrir Postman
2. Click en **Import**
3. Seleccionar el archivo `DevSu.postman_collection.json`
4. Hacer clic en **Import**

### Variables de entorno

La colección usa la variable `{{baseUrl}}` con valor por defecto:
- Development: `http://localhost:8085/api`

### Endpoints incluidos

| Categoría | Métodos |
|-----------|---------|
| Clientes | GET, POST, PUT, DELETE |
| Cuentas | GET, POST, PUT, DELETE |
| Movimientos | GET, POST |
| Reportes | GET (con rango de fechas) |

### Casos de prueba

- CRUD completo de Clientes
- CRUD completo de Cuentas
- Creación de movimientos (depósitos y retiros)
- Validación de saldo insuficiente
- Validación de límite diario de retiros ($1000)
- Generación de reportes con PDF base64

## Pruebas Unitarias

### Backend (17 tests)

```bash
cd backend
./mvnw test
```

| Test | Descripción |
|------|-------------|
| ClienteServiceImplTest | 7 pruebas |
| CuentaServiceImplTest | 6 pruebas |
| MovimientoServiceImplTest | 2 pruebas |
| ReporteServiceImplTest | 2 pruebas |

### Frontend (53 tests)

```bash
cd frontend
npm test
```

| Componente | Tests |
|------------|-------|
| Services | CRUD de cliente, cuenta, movimiento, reporte |
| Components | UI de clientes, cuentas, movimientos, reportes |

## Reglas de Negocio

- **Créditos**: valores positivos (se suman al saldo)
- **Débitos**: valores negativos (se restan del saldo)
- **Límite diario**: $1,000.00 máximo en retiros por día
- **Saldo mínimo**: $0.00 (no se permiten débitos que dejarlo en negativo)

## API Endpoints

### Clientes
- `GET /api/clientes` - Listar todos
- `GET /api/clientes/{clienteId}` - Obtener por ID
- `POST /api/clientes` - Crear cliente
- `PUT /api/clientes/{clienteId}` - Actualizar cliente
- `DELETE /api/clientes/{clienteId}` - Eliminar cliente

### Cuentas
- `GET /api/cuentas` - Listar todas
- `GET /api/cuentas/{numeroCuenta}` - Obtener por número
- `POST /api/cuentas` - Crear cuenta
- `PUT /api/cuentas/{numeroCuenta}` - Actualizar cuenta
- `DELETE /api/cuentas/{numeroCuenta}` - Eliminar cuenta

### Movimientos
- `GET /api/movimientos` - Listar todos
- `POST /api/movimientos` - Registrar movimiento

### Reportes
- `GET /api/reportes?clienteId={id}&fechaInicio={fecha}&fechaFin={fecha}` - Estado de cuenta

## Licencia

Este proyecto fue desarrollado como evaluación técnica.

---

📂 Repositorio: https://github.com/macartuche/testDevsu