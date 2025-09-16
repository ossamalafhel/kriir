# System Architecture

This document describes the architecture of the Reactive Transactional Mobility Platform, including design decisions, patterns used, and system components.

## Overview

The system follows a modern microservices architecture with reactive programming principles, featuring:

- **Frontend**: React 18 with Redux Toolkit
- **Backend**: Spring Boot 3 with WebFlux
- **Database**: PostgreSQL with PostGIS extension
- **Infrastructure**: Docker containers with Nginx reverse proxy

## Architecture Patterns

### 1. Reactive Architecture

The backend is built using Spring WebFlux, providing:

- **Non-blocking I/O**: Better resource utilization and scalability
- **Event-driven**: Reactive streams for handling data flow
- **Backpressure handling**: Automatic flow control
- **Server-Sent Events**: Real-time data streaming to clients

```java
@RestController
public class CarController {
    
    @GetMapping(value = "/cars/flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Car> getCarsFlux() {
        return carService.findAllAsFlux()
            .delayElements(Duration.ofSeconds(1));
    }
}
```

### 2. CQRS (Command Query Responsibility Segregation)

The system separates read and write operations:

- **Commands**: Modify state (POST, PUT, DELETE operations)
- **Queries**: Read state (GET operations with Server-Sent Events)
- **Event Sourcing**: Database triggers and LISTEN/NOTIFY for state changes

### 3. Event-Driven Architecture

Real-time updates are achieved through:

- **PostgreSQL LISTEN/NOTIFY**: Database-level event notifications
- **Server-Sent Events**: Push updates to connected clients
- **Redux Store**: Client-side state synchronization

## System Components

### Frontend Architecture

```
┌─────────────────────────────────────┐
│              Browser                │
├─────────────────────────────────────┤
│         React Application          │
│  ┌─────────────────────────────┐   │
│  │      Redux Store            │   │
│  │  ┌─────────────────────┐    │   │
│  │  │   Mobility Slice    │    │   │
│  │  │  • carsData        │    │   │
│  │  │  • usersData       │    │   │
│  │  │  • loading         │    │   │
│  │  │  • error           │    │   │
│  │  └─────────────────────┘    │   │
│  └─────────────────────────────┘   │
│  ┌─────────────────────────────┐   │
│  │      Components             │   │
│  │  • App                     │   │
│  │  • AppContent              │   │
│  │  • InteractiveMap          │   │
│  │  • Marker                  │   │
│  └─────────────────────────────┘   │
└─────────────────────────────────────┘
```

### Backend Architecture

```
┌─────────────────────────────────────┐
│           Spring Boot 3             │
├─────────────────────────────────────┤
│  ┌─────────────────────────────┐   │
│  │       Controllers           │   │
│  │  • CarController           │   │
│  │  • UserController          │   │
│  └─────────────────────────────┘   │
│  ┌─────────────────────────────┐   │
│  │        Services             │   │
│  │  • CarService              │   │
│  │  • UserService             │   │
│  └─────────────────────────────┘   │
│  ┌─────────────────────────────┐   │
│  │      Repositories           │   │
│  │  • CarRepository           │   │
│  │  • UserRepository          │   │
│  └─────────────────────────────┘   │
│  ┌─────────────────────────────┐   │
│  │      Configuration          │   │
│  │  • OpenApiConfig           │   │
│  │  • TransactionConfig       │   │
│  │  • NotificationConfig      │   │
│  └─────────────────────────────┘   │
└─────────────────────────────────────┘
```

### Database Architecture

```
┌─────────────────────────────────────┐
│          PostgreSQL 15              │
├─────────────────────────────────────┤
│  ┌─────────────────────────────┐   │
│  │         Tables              │   │
│  │  ┌─────────────────────┐    │   │
│  │  │        car          │    │   │
│  │  │  • id (VARCHAR)     │    │   │
│  │  │  • x (DOUBLE)       │    │   │
│  │  │  • y (DOUBLE)       │    │   │
│  │  │  • coordinate       │    │   │
│  │  │    (GEOMETRY)       │    │   │
│  │  └─────────────────────┘    │   │
│  │  ┌─────────────────────┐    │   │
│  │  │        users        │    │   │
│  │  │  • id (VARCHAR)     │    │   │
│  │  │  • x (DOUBLE)       │    │   │
│  │  │  • y (DOUBLE)       │    │   │
│  │  │  • coordinate       │    │   │
│  │  │    (GEOMETRY)       │    │   │
│  │  └─────────────────────┘    │   │
│  └─────────────────────────────┘   │
│  ┌─────────────────────────────┐   │
│  │       Extensions            │   │
│  │  • PostGIS (Spatial)       │   │
│  │  • Triggers (Notifications)│   │
│  └─────────────────────────────┘   │
└─────────────────────────────────────┘
```

## Data Flow

### 1. Real-time Updates

```
┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│   User   │───▶│  React   │───▶│  Redux   │───▶│    UI    │
│  Action  │    │Component │    │  Action  │    │ Update   │
└──────────┘    └──────────┘    └──────────┘    └──────────┘
                       │                              │
                       ▼                              │
┌──────────┐    ┌──────────┐    ┌──────────┐         │
│   SSE    │◄───│ Backend  │◄───│ Database │         │
│ Stream   │    │Controller│    │ Trigger  │         │
└──────────┘    └──────────┘    └──────────┘         │
      │                                               │
      └───────────────────────────────────────────────┘
```

### 2. Database Operations

```
┌──────────┐    ┌──────────┐    ┌──────────┐
│  Client  │───▶│ Backend  │───▶│ Database │
│ Request  │    │Service   │    │   CRUD   │
└──────────┘    └──────────┘    └──────────┘
                                      │
                                      ▼
┌──────────┐    ┌──────────┐    ┌──────────┐
│Connected │◄───│   SSE    │◄───│ Database │
│ Clients  │    │ Stream   │    │ Trigger  │
└──────────┘    └──────────┘    └──────────┘
```

## Security Considerations

### 1. Container Security

- **Non-root users**: All containers run as non-root users
- **Security headers**: Comprehensive HTTP security headers
- **Health checks**: Container health monitoring
- **Resource limits**: CPU and memory constraints

### 2. Network Security

- **Internal networks**: Docker bridge networks for service isolation
- **Reverse proxy**: Nginx as a reverse proxy and load balancer
- **HTTPS ready**: SSL/TLS termination support

### 3. Application Security

- **Input validation**: Comprehensive request validation
- **SQL injection prevention**: JPA/Hibernate parameterized queries
- **CORS configuration**: Cross-origin resource sharing controls
- **Authentication ready**: Framework for user authentication

## Performance Characteristics

### 1. Scalability

- **Reactive backend**: Non-blocking I/O for high concurrency
- **Connection pooling**: Efficient database connection management
- **Caching strategies**: Application-level and HTTP caching
- **Horizontal scaling**: Container-based deployment

### 2. Real-time Performance

- **Server-Sent Events**: Low-latency push notifications
- **Database triggers**: Immediate change detection
- **WebFlux streams**: Efficient data streaming
- **Redux optimization**: Minimal re-renders

### 3. Resource Efficiency

- **Multi-stage builds**: Optimized container images
- **JVM tuning**: Container-aware JVM settings
- **Nginx optimization**: Gzip compression and static file serving
- **Database indexes**: Spatial and standard indexes for performance

## Deployment Architecture

### Production Environment

```
┌─────────────────────────────────────┐
│              Load Balancer           │
├─────────────────────────────────────┤
│  ┌─────────────────────────────┐   │
│  │         Nginx               │   │
│  │  • Static files serving    │   │
│  │  • Reverse proxy           │   │
│  │  • Gzip compression        │   │
│  │  • Security headers        │   │
│  └─────────────────────────────┘   │
├─────────────────────────────────────┤
│  ┌─────────────────────────────┐   │
│  │     Application Tier        │   │
│  │  ┌─────────────────────┐    │   │
│  │  │   React Frontend    │    │   │
│  │  └─────────────────────┘    │   │
│  │  ┌─────────────────────┐    │   │
│  │  │  Spring Backend     │    │   │
│  │  └─────────────────────┘    │   │
│  └─────────────────────────────┘   │
├─────────────────────────────────────┤
│  ┌─────────────────────────────┐   │
│  │       Database Tier         │   │
│  │  ┌─────────────────────┐    │   │
│  │  │  PostgreSQL +       │    │   │
│  │  │  PostGIS           │    │   │
│  │  └─────────────────────┘    │   │
│  └─────────────────────────────┘   │
└─────────────────────────────────────┘
```

## Monitoring and Observability

### 1. Health Checks

- **Application**: Spring Boot Actuator endpoints
- **Database**: PostgreSQL connection checks
- **Frontend**: HTTP availability checks
- **Containers**: Docker health check commands

### 2. Metrics Collection

- **JVM metrics**: Memory, CPU, garbage collection
- **HTTP metrics**: Request rates, response times, error rates
- **Database metrics**: Connection pool, query performance
- **Custom metrics**: Business logic metrics

### 3. Logging Strategy

- **Structured logging**: JSON format for log aggregation
- **Log levels**: Appropriate log levels for different environments
- **Log rotation**: Automated log rotation and archival
- **Centralized logging**: Container log aggregation

This architecture provides a solid foundation for a scalable, maintainable, and high-performance mobility tracking platform.