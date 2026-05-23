# API Gateway Service - OMS (Order Management System)

A production-ready API Gateway implementation for routing, filtering, and managing requests across all microservices in the Order Management System.

## Project Structure

```
api-gateway/
├── src/main/java/com/ecom/api/gateway/
│   ├── ApiGatewayApplication.java          # Main entry point
│   ├── config/
│   │   ├── GatewayConfig.java              # Route configuration
│   │   └── RestTemplateConfig.java         # HTTP client configuration
│   ├── constant/
│   │   └── GatewayConstants.java           # Gateway constants
│   ├── controller/
│   │   ├── GatewayController.java          # Gateway API endpoints
│   │   └── HealthCheckController.java      # Health check endpoints
│   ├── dto/response/
│   │   ├── ApiResponse.java                # Generic API response wrapper
│   │   └── ErrorResponse.java              # Error response structure
│   ├── enums/
│   │   └── ErrorCode.java                  # Error code enumeration
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java     # Centralized exception handling
│   │   ├── GatewayException.java           # Base gateway exception
│   │   ├── ServiceUnavailableException.java
│   │   └── GatewayTimeoutException.java
│   ├── filter/
│   │   ├── AuthenticationFilter.java       # Authentication filter
│   │   ├── RequestLoggingFilter.java       # Request/response logging
│   │   └── ErrorHandlingFilter.java        # Error handling filter
│   ├── service/
│   │   ├── HealthCheckService.java         # Service health interface
│   │   ├── RouteService.java               # Route management interface
│   │   └── impl/
│   │       ├── HealthCheckServiceImpl.java
│   │       └── RouteServiceImpl.java
│   └── util/
│       └── RouteUtils.java                 # Utility functions
├── src/main/resources/
│   └── application.properties               # Configuration
└── pom.xml                                  # Maven build configuration
```

## Features

### 1. **Request Routing**
- Routes requests to 7 microservices:
  - User Service (8001)
  - Cart Service (8002)
  - Product Service (8003)
  - Inventory Service (8004)
  - Order Service (8005)
  - Payment Service (8006)
  - Auth Service (8007)

### 2. **Filters**
- **Authentication Filter**: Validates authorization headers
- **Request Logging Filter**: Logs all requests and responses
- **Error Handling Filter**: Catches service errors and provides meaningful responses

### 3. **Health Checks**
- Individual service health status
- Aggregate health check for all services

### 4. **Exception Handling**
- Centralized global exception handler
- Standardized error responses
- Support for timeouts and service unavailability

### 5. **API Documentation**
- Swagger/OpenAPI integration via springdoc-openapi

## Design Patterns & SOLID Principles

### Single Responsibility Principle (SRP)
- Each class has one reason to change
- Separate concerns: routing, filtering, exception handling, health checks

### Open/Closed Principle (OCP)
- Open for extension via filter abstraction
- Closed for modification

### Liskov Substitution Principle (LSP)
- Service interfaces properly implemented
- All filters extend AbstractGatewayFilterFactory

### Interface Segregation Principle (ISP)
- Small, focused interfaces (HealthCheckService, RouteService)
- Clients depend only on required methods

### Dependency Inversion Principle (DIP)
- High-level modules depend on abstractions (interfaces)
- Low-level modules implement abstractions
- Dependency injection via Spring

## Installation & Setup

### Prerequisites
- Java 17 or higher
- Maven 3.8+
- All 7 microservices running on their respective ports

### Build
```bash
cd api-gateway
mvn clean install
```

### Run
```bash
mvn spring-boot:run
```

Or package and run:
```bash
mvn clean package
java -jar target/api-gateway-0.0.1-SNAPSHOT.jar
```

## Configuration

### application.properties
- **Server Port**: 8000
- **Gateway Routes**: Configured via Spring Cloud Gateway
- **Timeout Settings**:
  - Connect Timeout: 5000ms
  - Response Timeout: 30000ms

## API Endpoints

### Gateway Management
```
GET  /api/gateway/health           - Gateway health status
GET  /api/gateway/info             - Gateway information
```

### Health Checks
```
GET  /api/gateway/health-check/service/{serviceName}  - Check specific service
GET  /api/gateway/health-check/all                    - Check all services
```

### Microservice Routes
```
/api/users/**        - User Service
/api/cart/**         - Cart Service
/api/products/**     - Product Service
/api/inventory/**    - Inventory Service
/api/orders/**       - Order Service
/api/payments/**     - Payment Service
/api/auth/**         - Auth Service
```

## Request/Response Flow

```
Client Request
     ↓
Gateway (Port 8000)
     ↓
Authentication Filter (validates token)
     ↓
Request Logging Filter (logs request)
     ↓
Route Matcher (determines target service)
     ↓
Target Microservice
     ↓
Response Logging Filter
     ↓
Response to Client
```

## Exception Handling

### Error Response Format
```json
{
  "timestamp": "2025-05-19T10:30:00",
  "status": 503,
  "error": "SERVICE_UNAVAILABLE",
  "code": "SERVICE_UNAVAILABLE",
  "message": "Service temporarily unavailable",
  "path": "/api/cart/getCart"
}
```

### Error Codes
- `INVALID_REQUEST` (400)
- `UNAUTHORIZED` (401)
- `FORBIDDEN` (403)
- `GATEWAY_TIMEOUT` (504)
- `SERVICE_UNAVAILABLE` (503)
- `BAD_GATEWAY` (502)
- `RATE_LIMIT_EXCEEDED` (429)
- `INTERNAL_SERVER_ERROR` (500)

## Logging

- **Root Level**: INFO
- **Gateway Level**: DEBUG
- **Spring Cloud Gateway**: DEBUG

## Best Practices

1. **Service Discovery**: Configure actual service discovery (Eureka, Consul)
2. **Load Balancing**: Implement load balancing for each service
3. **Circuit Breaker**: Add Resilience4j for fault tolerance
4. **Rate Limiting**: Implement rate limiting on gateway
5. **Caching**: Consider caching frequently accessed data
6. **Monitoring**: Integrate with Prometheus/Grafana
7. **Security**: Implement JWT token validation in AuthenticationFilter

## Extension Points

### Add New Microservice
1. Update `GatewayConstants.java` with service URL
2. Add route in `GatewayConfig.java`
3. Add service health check in `HealthCheckServiceImpl.java`
4. Update `RouteUtils.java` service name mapping

### Add Custom Filter
1. Extend `AbstractGatewayFilterFactory`
2. Register in `GatewayConfig.java`
3. Configure in `application.properties`

## Technologies

- **Spring Boot**: 3.5.14
- **Spring Cloud**: 2025.0.2
- **Spring Cloud Gateway**: WebMVC Edition
- **Java**: 17
- **Maven**: Build tool
- **Lombok**: Boilerplate reduction

## Monitoring & Actuator Endpoints

```
GET /actuator/health     - Application health
GET /actuator/info       - Application info
GET /actuator/gateway    - Gateway routes information
```

## Performance Considerations

- **Connection Timeout**: 5 seconds (configurable)
- **Response Timeout**: 30 seconds (configurable)
- **Connection Pool**: Configurable via HttpClient settings
- **Buffering**: Request/response buffering enabled

## Future Enhancements

1. OAuth2/JWT Authentication
2. Rate limiting with Redis
3. Service discovery integration
4. Circuit breaker pattern
5. Request transformation
6. Request tracing (Sleuth/Jaeger)
7. Metrics collection (Micrometer)
8. Caching layer
9. Load balancing strategies
10. GraphQL gateway support

## Contributing

Follow the existing code style and patterns. Ensure all new code follows SOLID principles and maintains backward compatibility.

## Support

For issues or questions, refer to the Cart Service documentation for similar implementation patterns.

---

**Version**: 1.0.0  
**Last Updated**: May 2025  
**Maintained By**: Development Team
