# Order Service - OMS (Order Management System)

A production-ready Order Management microservice for creating, tracking, and managing customer orders with workflow states and payment integration in the Order Management System.

## Overview

The Order Service handles the complete order lifecycle with features including:
- Create orders from cart
- Track order status with workflow states
- Manage order items and pricing
- Store shipping addresses
- Order history and tracking
- Advanced search and filtering
- Order status transitions with audit trail

## Project Structure

```
order-service/
├── src/main/java/com/ecom/ordersystem/orderservice/
│   ├── OrderServiceApplication.java          # Main entry point
│   ├── controller/
│   │   └── OrderController.java              # REST API endpoints
│   ├── service/
│   │   ├── OrderService.java                 # Service interface
│   │   └── impl/
│   │       └── OrderServiceImpl.java          # Service implementation
│   ├── entity/
│   │   ├── Order.java                        # Main order entity
│   │   ├── OrderItem.java                    # Order line items
│   │   ├── OrderAddress.java                 # Shipping address
│   │   └── OrderStatusHistory.java           # Status audit trail
│   ├── mapper/
│   │   ├── OrderMapper.java
│   │   └── OrderMapperImpl.java
│   ├── repository/
│   │   └── OrderRepository.java              # Order CRUD + queries
│   ├── dto/
│   │   ├── request/
│   │   │   ├── CreateOrderRequestParam.java
│   │   │   ├── OrderItemRequest.java
│   │   │   ├── OrderAddressRequest.java
│   │   │   ├── UpdateOrderStatusRequest.java
│   │   │   ├── UpdateShippingAddressRequest.java
│   │   │   ├── OrderSearchRequest.java
│   │   │   └── OrderSearchCriteria.java
│   │   └── response/
│   │       ├── OrderResponse.java
│   │       ├── OrderItemResponse.java
│   │       ├── OrderAddressResponse.java
│   │       ├── OrderStatusHistoryResponse.java
│   │       ├── PageResponse.java
│   │       └── ApiResponse.java
│   ├── enums/
│   │   ├── OrderStatus.java
│   │   └── ErrorCode.java
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   ├── BadRequestException.java
│   │   ├── ResourceNotFoundException.java
│   │   └── ErrorResponse.java
│   ├── specification/
│   │   └── OrderSpecification.java           # JPA specifications for search
│   ├── util/
│   │   └── OrderNumberGenerator.java         # Unique order ID generation
│   └── resources/
│       └── application.properties
└── pom.xml
```

## Features

### 1. **Order Creation**
- Create order from cart items
- Validate inventory before order
- Capture shipping address
- Calculate total with taxes
- Generate unique order number

### 2. **Order Tracking**
- Track order status throughout lifecycle
- View order details
- See order items and pricing
- Access shipping address
- View status history with timestamps

### 3. **Status Management**
- PENDING - Initial state
- CONFIRMED - Payment received
- PROCESSING - Being prepared
- SHIPPED - On the way
- DELIVERED - At destination
- CANCELLED - Order cancelled
- RETURNED - Items returned

### 4. **Advanced Search**
- Search by order number
- Filter by status
- Filter by date range
- Filter by amount range
- Filter by customer
- Pagination support

### 5. **Order History**
- Complete status transition history
- Timestamp for each status change
- Reason for cancellation/return
- Audit trail for compliance

## API Endpoints

### Order Management
```
POST   /api/orders                        - Create new order
GET    /api/orders/{orderId}              - Get order details
GET    /api/orders                        - List user's orders (paginated)
GET    /api/orders/number/{orderNumber}   - Get order by number
PUT    /api/orders/{orderId}/status       - Update order status
PUT    /api/orders/{orderId}/address      - Update shipping address
```

### Search & Filter
```
POST   /api/orders/search                 - Advanced search with filters
GET    /api/orders?status=SHIPPED          - Filter by status
GET    /api/orders?page=0&size=20          - Pagination
```

### Internal APIs
```
GET    /api/internal/orders/{orderId}     - Get order (for services)
POST   /api/internal/orders/validate      - Validate order items
```

## Database Schema

### Tables
- `order` - Main order records
- `order_item` - Individual items in order
- `order_address` - Shipping address
- `order_status_history` - Status audit trail

### Key Fields

**Order**
- `id` (UUID) - Primary key
- `order_number` (VARCHAR) - Unique order identifier
- `user_id` (UUID) - Customer
- `status` (ENUM) - Current order status
- `total_amount` (DECIMAL) - Order total
- `subtotal` (DECIMAL) - Before tax
- `tax_amount` (DECIMAL) - Tax calculated
- `discount_amount` (DECIMAL) - Applied discounts
- `created_at` (TIMESTAMP) - Order creation
- `updated_at` (TIMESTAMP) - Last update

**OrderItem**
- `id` (UUID) - Primary key
- `order_id` (UUID) - Reference to order
- `product_id` (UUID) - Product ordered
- `quantity` (INT) - Items ordered
- `unit_price` (DECIMAL) - Price per unit
- `total_price` (DECIMAL) - Quantity × price

**OrderAddress**
- `id` (UUID) - Primary key
- `order_id` (UUID) - Reference to order
- `full_name` (VARCHAR) - Recipient name
- `phone_number` (VARCHAR)
- `address_line_1` (VARCHAR)
- `address_line_2` (VARCHAR)
- `city` (VARCHAR)
- `state` (VARCHAR)
- `country` (VARCHAR)
- `postal_code` (VARCHAR)

**OrderStatusHistory**
- `id` (UUID) - Primary key
- `order_id` (UUID) - Reference to order
- `from_status` (ENUM) - Previous status
- `to_status` (ENUM) - New status
- `reason` (TEXT) - Reason for change
- `created_at` (TIMESTAMP)

## Configuration

### application.properties
```properties
server.port=8005
spring.application.name=order-service

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/order_db
spring.datasource.username=postgres
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=validate

# JPA
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true
```

## Installation & Setup

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL
- Cart Service (for order creation)
- Inventory Service (for stock validation)
- Payment Service (for payment confirmation)

### Build
```bash
cd order-service
mvn clean install
```

### Run
```bash
mvn spring-boot:run
```

Or with Docker:
```bash
mvn clean package
docker build -t order-service .
docker run -p 8005:8005 order-service
```

## Architecture

### Layers

```
┌─────────────────────────────────────────┐
│     REST Controller                     │
│    (OrderController)                    │
└────────────┬────────────────────────────┘
             │
┌────────────▼────────────────────────────┐
│     Service Layer                       │
│    (OrderService)                       │
└────────────┬────────────────────────────┘
             │
┌────────────▼────────────────────────────┐
│    Repository Layer                     │
│  (OrderRepository with Specifications)  │
└────────────┬────────────────────────────┘
             │
┌────────────▼────────────────────────────┐
│  Database Layer (PostgreSQL)            │
└─────────────────────────────────────────┘
```

### Order Workflow

```
Cart Checkout
    ↓
Create Order (PENDING)
    ↓
Payment Processing
    ├─ Success → CONFIRMED
    └─ Failure → CANCELLED
    ↓
Order Processing (PROCESSING)
    ↓
Shipment (SHIPPED)
    ↓
Delivery (DELIVERED)
```

## SOLID Principles

- **S**ingle Responsibility: Clear separation of concerns
- **O**pen/Closed: Extensible via interfaces
- **L**iskov Substitution: Proper implementation contracts
- **I**nterface Segregation: Focused service contracts
- **D**ependency Inversion: Depends on abstractions

## API Examples

### 1. Create Order
```bash
curl -X POST http://localhost:8005/api/orders \
  -H "Content-Type: application/json" \
  -H "X-User-Id: user-uuid" \
  -d '{
    "items": [
      {
        "productId": "product-uuid",
        "quantity": 2
      }
    ],
    "shippingAddress": {
      "fullName": "John Doe",
      "phoneNumber": "+1-234-567-8900",
      "addressLine1": "123 Main St",
      "city": "New York",
      "state": "NY",
      "country": "USA",
      "postalCode": "10001"
    }
  }'
```

Response:
```json
{
  "success": true,
  "data": {
    "id": "order-uuid",
    "orderNumber": "ORD-2024-001234",
    "userId": "user-uuid",
    "status": "PENDING",
    "items": [
      {
        "productId": "product-uuid",
        "quantity": 2,
        "unitPrice": 99.99,
        "totalPrice": 199.98
      }
    ],
    "subtotal": 199.98,
    "taxAmount": 15.99,
    "total": 215.97,
    "createdAt": "2024-01-15T10:30:00"
  }
}
```

### 2. Get Order Details
```bash
curl -X GET http://localhost:8005/api/orders/order-uuid \
  -H "X-User-Id: user-uuid"
```

### 3. Update Order Status
```bash
curl -X PUT http://localhost:8005/api/orders/order-uuid/status \
  -H "Content-Type: application/json" \
  -H "X-User-Id: admin-uuid" \
  -d '{
    "status": "CONFIRMED"
  }'
```

### 4. Search Orders
```bash
curl -X POST http://localhost:8005/api/orders/search \
  -H "Content-Type: application/json" \
  -H "X-User-Id: user-uuid" \
  -d '{
    "status": "SHIPPED",
    "startDate": "2024-01-01",
    "endDate": "2024-01-31",
    "page": 0,
    "size": 10
  }'
```

### 5. Update Shipping Address
```bash
curl -X PUT http://localhost:8005/api/orders/order-uuid/address \
  -H "Content-Type: application/json" \
  -H "X-User-Id: user-uuid" \
  -d '{
    "fullName": "Jane Doe",
    "phoneNumber": "+1-234-567-8901",
    "addressLine1": "456 Oak Ave",
    "city": "Boston",
    "state": "MA",
    "country": "USA",
    "postalCode": "02101"
  }'
```

## Order Status Transitions

```
PENDING
  ├─→ CONFIRMED (on payment success)
  └─→ CANCELLED (on payment failure or user request)

CONFIRMED
  ├─→ PROCESSING (admin action)
  └─→ CANCELLED (before shipping)

PROCESSING
  ├─→ SHIPPED (items dispatched)
  └─→ CANCELLED (before shipment)

SHIPPED
  ├─→ DELIVERED (items received)
  └─→ RETURNED (customer return request)

DELIVERED
  ├─→ RETURNED (within return window)
  └─→ (final state)

CANCELLED / RETURNED
  └─→ (final state - no transitions)
```

## Error Codes

| Code | HTTP | Description |
|------|------|-------------|
| ORDER_NOT_FOUND | 404 | Order doesn't exist |
| INVALID_ORDER_ITEMS | 400 | Invalid items in order |
| INSUFFICIENT_INVENTORY | 400 | Not enough stock |
| INVALID_ADDRESS | 400 | Invalid shipping address |
| INVALID_STATUS_TRANSITION | 400 | Invalid status change |
| ORDER_ALREADY_SHIPPED | 400 | Order already shipped |
| INVALID_REQUEST | 400 | Invalid request data |
| UNAUTHORIZED | 401 | Authentication failed |
| FORBIDDEN | 403 | Insufficient permissions |
| INTERNAL_SERVER_ERROR | 500 | Server error |

## Integration Points

### With Cart Service
- Create order from cart
- Clear cart after order
- Validate cart items

### With Inventory Service
- Check stock availability
- Create reservations
- Confirm reservations on payment

### With Payment Service
- Validate payment
- Confirm order on payment success
- Cancel on payment failure

### With User Service
- Get shipping address
- Get user information
- Validate user exists

## Performance Considerations

- **Indexes**: On order_number, user_id, status, created_at
- **Pagination**: Efficient list retrieval
- **Specifications**: JPA Criteria for complex queries
- **Caching**: Consider cache for recent orders

## Monitoring & Logging

### Actuator Endpoints
```
GET /actuator/health           - Service health
GET /actuator/metrics          - Metrics
```

### Key Metrics
- Orders created per hour
- Average order value
- Order processing time
- Status transition rates

## Testing

### Unit Tests
- Order creation logic
- Status transition validation
- Search/filter functionality

### Integration Tests
- Complete order workflow
- Database operations
- API endpoint tests

## Future Enhancements

1. Order notifications
2. Tracking number integration
3. Return/exchange management
4. Order notes and comments
5. Multiple shipping addresses
6. Bulk order operations
7. Subscription orders
8. Order analytics/insights
9. Custom order fields
10. Integration with shipping providers

## Troubleshooting

### Order Creation Failed
- Check inventory availability
- Verify user information
- Check address validation

### Invalid Status Transition
- Review current order status
- Check allowed transitions
- Verify user permissions

### Search Not Finding Orders
- Check date range
- Verify search criteria
- Check pagination parameters

## Technologies

- **Spring Boot**: 3.5.9
- **Spring Data JPA**: ORM with Specifications
- **PostgreSQL**: Primary database
- **Lombok**: Boilerplate reduction
- **OpenAPI/Swagger**: API documentation

## Version & Maintenance

**Version**: 1.0.0  
**Last Updated**: May 2025  
**Maintained By**: Development Team

## Contributing

Follow existing code style and patterns. Ensure all new code follows SOLID principles.

## Support

For issues or questions, refer to project documentation or contact the development team.
