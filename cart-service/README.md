# Cart Service - OMS (Order Management System)

A production-ready Shopping Cart microservice with Redis caching, cart management, and checkout functionality for the Order Management System.

## Overview

The Cart Service manages customer shopping carts with features including:
- Add/remove items from cart
- Update item quantities
- Real-time inventory validation
- Cart persistence with Redis caching
- Checkout summary calculation
- Cart expiration handling

## Project Structure

```
cart-service/
├── src/main/java/com/ecom/cart/
│   ├── CartServiceApplication.java          # Main entry point
│   ├── config/
│   │   └── RedisConfig.java                 # Redis configuration
│   ├── controller/
│   │   └── CartController.java              # REST API endpoints
│   ├── service/
│   │   ├── CartService.java                 # Service interface
│   │   └── impl/
│   │       └── CartServiceImpl.java          # Service implementation
│   ├── entity/
│   │   ├── Cart.java                        # Cart entity
│   │   └── CartItem.java                    # Cart item entity
│   ├── redis/
│   │   ├── RedisCart.java                   # Redis cart model
│   │   └── RedisCartItem.java               # Redis cart item model
│   ├── mapper/
│   │   ├── CartMapper.java                  # Mapper interface
│   │   └── CartMapperImpl.java               # Mapper implementation
│   ├── repository/
│   │   ├── CartRepository.java              # JPA repository
│   │   ├── CartItemRepository.java          # Cart item repository
│   │   └── RedisCartRepository.java         # Redis operations
│   ├── dto/
│   │   ├── request/
│   │   │   ├── AddItemToCartRequest.java
│   │   │   └── UpdateCartItemQuantityRequest.java
│   │   └── response/
│   │       ├── CartResponse.java
│   │       ├── CartItemResponse.java
│   │       ├── CartValidationResponse.java
│   │       ├── CheckoutSummaryResponse.java
│   │       ├── PageResponse.java
│   │       └── ApiResponse.java
│   ├── enums/
│   │   ├── CartStatus.java
│   │   └── ErrorCode.java
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   ├── BadRequestException.java
│   │   ├── ResourceNotFoundException.java
│   │   └── ErrorResponse.java
│   ├── filter/
│   │   └── LoggingCorrelationFilter.java
│   ├── util/
│   │   └── PaginationUtils.java
│   └── resources/
│       └── application.properties
└── pom.xml
```

## Features

### 1. **Cart Management**
- Create/retrieve user cart
- Add items with product validation
- Update item quantities
- Remove items from cart
- Clear entire cart

### 2. **Inventory Validation**
- Real-time stock availability check
- Prevent over-ordering
- Validate product existence
- Check inventory before checkout

### 3. **Redis Caching**
- Fast cart retrieval
- Cart persistence in Redis
- Session-based cart management
- Reduced database queries

### 4. **Checkout Features**
- Calculate cart totals
- Apply taxes/discounts
- Generate checkout summary
- Validate cart before checkout

### 5. **Error Handling**
- Centralized exception handling
- Detailed error responses
- Validation error messages
- Service unavailability handling

## API Endpoints

### Cart Operations
```
GET    /api/cart                      - Get user's cart
POST   /api/cart/items                - Add item to cart
PUT    /api/cart/items/{itemId}       - Update item quantity
DELETE /api/cart/items/{itemId}       - Remove item from cart
DELETE /api/cart                      - Clear entire cart
```

### Checkout
```
GET    /api/cart/checkout-summary     - Get checkout summary
POST   /api/cart/validate             - Validate cart before checkout
```

### Internal APIs
```
GET    /api/cart/internal/{userId}    - Get user's cart (internal)
```

## Database Schema

### Tables
- `cart` - Shopping cart records
- `cart_item` - Individual items in cart

### Key Fields
- `id` (UUID) - Primary key
- `user_id` (UUID) - Customer identifier
- `status` (ENUM) - Cart status (ACTIVE, ABANDONED, CHECKED_OUT)
- `created_at` (TIMESTAMP) - Cart creation time
- `updated_at` (TIMESTAMP) - Last update time

## Configuration

### application.properties
```properties
server.port=8002
spring.application.name=cart-service

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/cart_db
spring.datasource.username=postgres
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=validate

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.timeout=2000
```

## Installation & Setup

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL
- Redis
- Product Service (for inventory validation)
- Inventory Service (for stock checks)

### Build
```bash
cd cart-service
mvn clean install
```

### Run
```bash
mvn spring-boot:run
```

Or with Docker:
```bash
mvn clean package
docker build -t cart-service .
docker run -p 8002:8002 cart-service
```

## Architecture

### Layers

```
┌─────────────────────────────────────────┐
│       REST Controller                   │
│    (CartController)                     │
└────────────┬────────────────────────────┘
             │
┌────────────▼────────────────────────────┐
│       Service Layer                     │
│    (CartService)                        │
└────────────┬────────────────────────────┘
             │
┌────────────▼────────────────────────────┐
│    Repository Layer                     │
│    (CartRepository,                     │
│     RedisCartRepository)                │
└────────────┬────────────────────────────┘
             │
┌────────────▼────────────────────────────┐
│    Data Layer                           │
│    (PostgreSQL + Redis)                 │
└─────────────────────────────────────────┘
```

### Design Patterns
- **Repository Pattern**: Data access abstraction
- **Service Layer Pattern**: Business logic encapsulation
- **DTO Pattern**: API contract separation
- **Mapper Pattern**: Entity-DTO transformations
- **Caching Strategy**: Redis for performance

## SOLID Principles

- **S**ingle Responsibility: Each class handles one concern
- **O**pen/Closed: Extensible via interfaces
- **L**iskov Substitution: Implementations substitute interfaces
- **I**nterface Segregation: Focused interfaces
- **D**ependency Inversion: Depends on abstractions

## API Examples

### 1. Get User Cart
```bash
curl -X GET http://localhost:8002/api/cart \
  -H "X-User-Id: user-uuid"
```

Response:
```json
{
  "success": true,
  "data": {
    "id": "cart-uuid",
    "userId": "user-uuid",
    "status": "ACTIVE",
    "items": [
      {
        "id": "item-uuid",
        "productId": "product-uuid",
        "productName": "Product Name",
        "quantity": 2,
        "price": 99.99,
        "total": 199.98
      }
    ],
    "subtotal": 199.98,
    "tax": 15.99,
    "total": 215.97,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T11:00:00"
  }
}
```

### 2. Add Item to Cart
```bash
curl -X POST http://localhost:8002/api/cart/items \
  -H "Content-Type: application/json" \
  -H "X-User-Id: user-uuid" \
  -d '{
    "productId": "product-uuid",
    "quantity": 2
  }'
```

### 3. Update Item Quantity
```bash
curl -X PUT http://localhost:8002/api/cart/items/item-uuid \
  -H "Content-Type: application/json" \
  -H "X-User-Id: user-uuid" \
  -d '{
    "quantity": 5
  }'
```

### 4. Get Checkout Summary
```bash
curl -X GET http://localhost:8002/api/cart/checkout-summary \
  -H "X-User-Id: user-uuid"
```

## Error Codes

| Code | HTTP | Description |
|------|------|-------------|
| CART_NOT_FOUND | 404 | Cart does not exist |
| ITEM_NOT_FOUND | 404 | Item not in cart |
| PRODUCT_NOT_FOUND | 404 | Product doesn't exist |
| INVALID_QUANTITY | 400 | Invalid quantity provided |
| INSUFFICIENT_STOCK | 400 | Not enough inventory |
| INVALID_REQUEST | 400 | Invalid request data |
| UNAUTHORIZED | 401 | Authentication failed |
| SERVICE_UNAVAILABLE | 503 | Service temporarily down |
| INTERNAL_SERVER_ERROR | 500 | Server error |

## Integration Points

### With Product Service
- Fetch product details
- Validate product existence
- Get current pricing

### With Inventory Service
- Check stock availability
- Reserve inventory
- Update stock on checkout

### With Order Service
- Create order from cart
- Clear cart after checkout
- Validate items before order

## Performance Considerations

- **Redis Caching**: Reduces database queries
- **Batch Operations**: Efficient bulk updates
- **Connection Pooling**: Optimized database connections
- **Index Strategy**: Fast lookups on frequently queried fields

## Monitoring & Logging

- **Correlation IDs**: Track requests across services
- **Request/Response Logging**: Via LoggingCorrelationFilter
- **Health Checks**: Spring Boot Actuator endpoints
- **Metrics**: Micrometer integration

### Actuator Endpoints
```
GET /actuator/health           - Service health
GET /actuator/metrics          - Metrics
GET /actuator/prometheus       - Prometheus metrics
```

## Testing

### Unit Tests
- Service layer tests
- Mapper tests
- Validation tests

### Integration Tests
- Controller tests
- Repository tests
- Redis integration tests

### Test Data
```bash
# Insert test data into PostgreSQL
psql -U postgres -d cart_db -f test-data.sql
```

## Deployment

### Docker
```dockerfile
FROM openjdk:17-slim
COPY target/cart-service-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Compose
```yaml
services:
  cart-service:
    build: ./cart-service
    ports:
      - "8002:8002"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/cart_db
      - SPRING_REDIS_HOST=redis
    depends_on:
      - postgres
      - redis
```

## Future Enhancements

1. Wishlist management
2. Saved carts for later
3. Cart sharing
4. Promotional code handling
5. Abandoned cart emails
6. Advanced analytics
7. Cart recommendations
8. Multi-currency support
9. Cart versioning
10. A/B testing support

## Troubleshooting

### Cart Not Found
- Verify user is authenticated
- Check X-User-Id header is provided
- Ensure user exists in system

### Redis Connection Failed
- Check Redis server is running
- Verify Redis host/port configuration
- Check network connectivity

### Inventory Validation Failed
- Ensure Inventory Service is running
- Verify product IDs are correct
- Check stock levels

## Technologies

- **Spring Boot**: 3.5.14
- **Spring Data JPA**: Database ORM
- **Spring Data Redis**: Redis integration
- **PostgreSQL**: Primary database
- **Redis**: Caching layer
- **Lombok**: Boilerplate reduction
- **OpenAPI/Swagger**: API documentation

## Version & Maintenance

**Version**: 1.0.0  
**Last Updated**: May 2025  
**Maintained By**: Development Team

## Contributing

Follow existing code style and patterns. Ensure all new code follows SOLID principles and maintains backward compatibility.

## Support

For issues or questions, refer to project documentation or contact the development team.
