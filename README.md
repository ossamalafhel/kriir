# Reactive Transactional Mobility Platform

A modern, real-time mobility tracking platform built with Spring Boot 3 and React 18, featuring reactive programming, geospatial data handling, and live updates through Server-Sent Events.

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.3.1-blue.svg)](https://reactjs.org/)
[![Redux Toolkit](https://img.shields.io/badge/Redux%20Toolkit-2.2.7-purple.svg)](https://redux-toolkit.js.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![PostGIS](https://img.shields.io/badge/PostGIS-3.4-green.svg)](https://postgis.net/)

## 🚀 Features

### Backend
- **Reactive Architecture**: Built with Spring WebFlux for high-performance, non-blocking I/O
- **Real-time Updates**: Server-Sent Events (SSE) for live data streaming
- **Geospatial Support**: PostGIS integration for advanced location-based queries
- **Modern Spring Boot 3**: Jakarta EE, native compilation ready
- **Database Notifications**: PostgreSQL LISTEN/NOTIFY for real-time data synchronization
- **OpenAPI 3**: Comprehensive API documentation with SpringDoc
- **Comprehensive Testing**: Unit, integration, and performance tests

### Frontend
- **Modern React 18**: Hooks, Concurrent Features, and StrictMode
- **Redux Toolkit**: Simplified state management with RTK Query
- **Interactive Maps**: Mapbox GL JS integration for real-time vehicle tracking
- **Responsive Design**: Mobile-first, adaptive UI
- **TypeScript Ready**: Type-safe development environment
- **Performance Optimized**: Code splitting, lazy loading, and memoization

### Infrastructure
- **Docker & Docker Compose**: Full containerization with multi-stage builds
- **Security**: Non-root containers, security headers, health checks
- **Development Environment**: Hot reloading, debugging support
- **Production Ready**: Nginx reverse proxy, gzip compression, caching

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   React 18      │    │  Spring Boot 3  │    │  PostgreSQL +  │
│   Frontend      │◄──►│  Backend        │◄──►│  PostGIS       │
│                 │    │                 │    │                 │
│ • Redux Toolkit │    │ • WebFlux       │    │ • LISTEN/NOTIFY │
│ • Mapbox GL     │    │ • Server-Sent   │    │ • Spatial Data  │
│ • Real-time UI  │    │   Events        │    │ • Transactions  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🛠️ Technology Stack

| Component | Technology | Version |
|-----------|------------|---------|
| **Backend Framework** | Spring Boot | 3.3.5 |
| **Java Version** | OpenJDK | 17 |
| **Build Tool** | Maven | 3.9+ |
| **Database** | PostgreSQL | 15 |
| **Spatial Extension** | PostGIS | 3.4 |
| **Frontend Framework** | React | 18.3.1 |
| **State Management** | Redux Toolkit | 2.2.7 |
| **Build Tool** | Create React App | 5.0.1 |
| **Maps** | Mapbox GL JS | 3.6.0 |
| **Testing (Backend)** | JUnit | 5.9+ |
| **Testing (Frontend)** | Jest + Testing Library | Latest |
| **Containerization** | Docker | 20.10+ |
| **Container Orchestration** | Docker Compose | 2.0+ |

## 🚀 Quick Start

### Prerequisites

- Java 17+
- Node.js 18+
- Docker & Docker Compose
- PostgreSQL 15+ (if running locally)

### 🐳 Docker (Recommended)

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-org/reactive-transactional.git
   cd reactive-transactional
   ```

2. **Start the application**
   ```bash
   # Production environment
   docker-compose up --build

   # Development environment with hot reload
   docker-compose -f docker-compose.dev.yml up --build
   ```

3. **Access the application**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080
   - API Documentation: http://localhost:8080/swagger-ui.html

### 🖥️ Local Development

#### Backend Setup
```bash
cd server
./mvnw clean install
./mvnw spring-boot:run
```

#### Frontend Setup
```bash
cd front
npm install
npm start
```

#### Database Setup
```bash
# Using Docker
docker run --name mobility-postgres -e POSTGRES_PASSWORD=rci -e POSTGRES_USER=rci -e POSTGRES_DB=mobility_db -p 5432:5432 -d postgis/postgis:15-3.4-alpine

# Run schema initialization
psql -h localhost -U rci -d mobility_db -f server/src/main/resources/schema.sql
psql -h localhost -U rci -d mobility_db -f server/src/main/resources/function_notify_event.sql
```

## 📖 API Documentation

The API documentation is automatically generated using SpringDoc OpenAPI 3 and is available at:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### Key Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/cars` | Get all cars |
| POST | `/cars` | Create a new car |
| GET | `/cars/flux` | Real-time car updates (SSE) |
| GET | `/cars/inside` | Get cars within area |
| GET | `/users` | Get all users |
| POST | `/users` | Create a new user |
| GET | `/users/flux` | Real-time user updates (SSE) |

## 🧪 Testing

### Backend Tests
```bash
cd server
./mvnw test                    # Unit tests
./mvnw test -Dtest=**IT        # Integration tests
./mvnw verify                  # All tests + quality checks
```

### Frontend Tests
```bash
cd front
npm test                       # Interactive test runner
npm test -- --coverage        # With coverage report
npm test -- --watchAll=false  # Single run
```

## 🚀 Deployment

### Production Deployment
```bash
# Build and deploy
docker-compose up --build -d

# Health checks
curl http://localhost:8080/actuator/health
curl http://localhost:3000/
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Spring profile | `dev` |
| `POSTGRES_URL` | Database URL | `jdbc:postgresql://localhost:5432/mobility_db` |
| `POSTGRES_USER` | Database user | `rci` |
| `POSTGRES_PASSWORD` | Database password | `rci` |
| `REACT_APP_API_URL` | Backend API URL | `http://localhost:8080` |
| `MAPBOX_ACCESS_TOKEN` | Mapbox API token | Required |

## 🏗️ Development

### Project Structure
```
reactive-transactional/
├── server/                 # Spring Boot backend
│   ├── src/main/java/     # Java source code
│   ├── src/test/java/     # Test code
│   ├── src/main/resources/ # Configuration & SQL
│   └── Dockerfile         # Production container
├── front/                 # React frontend
│   ├── src/               # React source code
│   ├── public/            # Static assets
│   └── Dockerfile         # Production container
├── docs/                  # Documentation
├── docker-compose.yml     # Production containers
└── docker-compose.dev.yml # Development containers
```

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🤝 Support

- 📧 Email: contact@rcimobility.com
- 📖 Documentation: [docs/](docs/)
- 🐛 Bug Reports: GitHub Issues
- 💬 Discussions: GitHub Discussions

---

**Built with ❤️ by the RCI Mobility Team**