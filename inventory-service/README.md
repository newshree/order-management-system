# Inventory Service - OMS (Order Management System)

A production-ready Inventory Management microservice for tracking product stock, handling reservations, and managing inventory transactions in the Order Management System.

## Overview

The Inventory Service manages product inventory with features including:
- Real-time stock level tracking
- Inventory reservations for pending orders
- Transaction history and audit trail
- Bulk stock updates
- Stock availability validation
- Low stock alerts

## Project Structure

```
inventory-service/
├── src/main/java/com/ecom/inventory/
│   ├── InventoryServiceApplication.java     # Main entry point
│   ├── controller/
│   │   ├── InternalInventoryController.java # Internal APIs
│   │   └── AdminInventoryController.java    # Admin operations
│   ├── service/
│   │   ├── InventoryService.java            # Service interface
│   │   ├── InventoryAdminService.java       # Admin interface
│   │   └── impl/
│   │       ├── InventoryServiceImpl.java
│   │       └── InventoryAdminServiceImpl.java
│   ├── entity/
│   │   ├── Inventory.java                   # Stock levels
│   │   ├── InventoryReservation.java        # Reserved items
│   │   └── InventoryTransaction.java        # Audit trail
│   ├── mapper/
│   │   ├── InventoryMapper.java
│   │   └── InventoryMapperImpl.java
│   ├── repository/
│   │   ├── InventoryRepository.java
│   │   ├── InventoryReservationRepository.java
│   │   └── InventoryTransactionRepository.java
│   ├── dto/
│   │   ├── request/
│   │   │   ├── InventoryStockRequest.java
│   │   │   ├── StockUpdateRequest.java
│   │   │   └── BulkStockUpdateRequest.java
│   │   └── response/
│   │       ├── InventoryResponse.java
│   │       ├── ReservationResponse.java
│   │       ├── TransactionResponse.java
│   │       ├── PageResponse.java
│   │       └── ApiResponse.java
│   ├── enums/
│   │   ├── TransactionType.java
│   │   ├── ReservationStatus.java
│   │   └── ErrorCode.java
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   ├── BadRequestException.java
│   │   ├── ResourceNotFoundException.java
│   │   └── ErrorResponse.java
│   └── resources/
│       └── application.properties
└── pom.xml
```

## Features

### 1. **Stock Management**
- Create/update product inventory
- Track available quantity
- Set reorder levels
- Manage stock locations
- Real-time stock validation

### 2. **Inventory Reservations**
- Reserve stock for orders
- Track reservation status
- Auto-release expired reservations
- Prevent double-booking

### 3. **Transaction History**
- Log all stock movements
- Track transaction types:
  - PURCHASE (incoming stock)
  - SALE (outgoing stock)
  - RETURN (returned items)
  - ADJUSTMENT (inventory correction)
  - DAMAGE (damaged goods)
  - RESERVATION (reserved stock)

### 4. **Admin Operations**
- Bulk stock updates
- Inventory adjustments
- Reorder point management
- Stock availability reports

### 5. **Validation & Alerts**
- Stock availability checks
- Low stock detection
- Overstock warnings
- Expiration tracking

## API Endpoints

### Stock Information (Internal)
```
GET    /api/internal/inventory/{productId}      - Get stock level
POST   /api/internal/inventory/check-stock      - Check availability
GET    /api/internal/inventory/batch             - Batch stock check
```

### Reservations
```
POST   /api/internal/reservations               - Create reservation
GET    /api/internal/reservations/{id}          - Get reservation
PUT    /api/internal/reservations/{id}/confirm  - Confirm reservation
PUT    /api/internal/reservations/{id}/cancel   - Cancel reservation
```

### Admin Operations
```
POST   /api/admin/inventory                      - Create inventory
PUT    /api/admin/inventory/{productId}          - Update stock
POST   /api/admin/inventory/bulk-update          - Bulk update
GET    /api/admin/inventory                      - List inventory (paginated)
GET    /api/admin/inventory/{productId}          - Get details
GET    /api/admin/inventory/low-stock            - Low stock items
GET    /api/admin/transactions/{productId}       - Transaction history
```

## Database Schema

### Tables
- `inventory` - Product stock levels
- `inventory_reservation` - Reserved items
- `inventory_transaction` - Transaction audit trail

### Key Fields

**Inventory**
- `id` (UUID) - Primary key
- `product_id` (UUID) - Product reference
- `available_quantity` (INT) - Available stock
- `reserved_quantity` (INT) - Reserved stock
- `total_quantity` (INT) - Total stock
- `reorder_level` (INT) - Minimum stock level
- `updated_at` (TIMESTAMP) - Last update

**InventoryReservation**
- `id` (UUID) - Primary key
- `product_id` (UUID) - Product reference
- `order_id` (UUID) - Associated order
- `quantity` (INT) - Reserved quantity
- `status` (ENUM) - PENDING, CONFIRMED, CANCELLED, RELEASED
- `created_at` (TIMESTAMP)

**InventoryTransaction**
- `id` (UUID) - Primary key
- `product_id` (UUID) - Product reference
- `type` (ENUM) - Transaction type
- `quantity` (INT) - Quantity changed
- `reason` (TEXT) - Reason for change
- `created_at` (TIMESTAMP)

## Configuration

### application.properties
```properties
server.port=8004
spring.application.name=inventory-service

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/inventory_db
spring.datasource.username=postgres
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=validate

# Logging
logging.level.com.ecom.inventory=DEBUG
```

## Installation & Setup

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL

### Build
```bash
cd inventory-service
mvn clean install
```

### Run
```bash
mvn spring-boot:run
```

Or with Docker:
```bash
mvn clean package
docker build -t inventory-service .
docker run -p 8004:8004 inventory-service
```

## Architecture

### Layers

```
┌─────────────────────────────────────────┐
│   REST Controllers                      │
│  (Internal, Admin)                      │
└────────────┬────────────────────────────┘
             │
┌────────────▼────────────────────────────┐
│    Service Layer                        │
│ (InventoryService,                      │
│  InventoryAdminService)                 │
└────────────┬────────────────────────────┘
             │
┌────────────▼────────────────────────────┐
│   Repository Layer                      │
│ (InventoryRepository,                   │
│  ReservationRepository,                 │
│  TransactionRepository)                 │
└────────────┬────────────────────────────┘
             │
┌────────────▼────────────────────────────┐
│   Database Layer (PostgreSQL)           │
└─────────────────────────────────────────┘
```

## SOLID Principles

- **S**ingle Responsibility: Each service handles one concern
- **O**pen/Closed: Extensible via interfaces
- **L**iskov Substitution: Proper implementation substitution
- **I**nterface Segregation: Focused service interfaces
- **D**ependency Inversion: Depends on abstractions

## API Examples

### 1. Get Stock Level
```bash
curl -X GET http://localhost:8004/api/internal/inventory/product-uuid
```

Response:
```json
{
  "success": true,
  "data": {
    "productId": "product-uuid",
    "productName": "Product Name",
    "availableQuantity": 150,
    "reservedQuantity": 25,
    "totalQuantity": 175,
    "reorderLevel": 50,
    "status": "IN_STOCK",
    "lastUpdated": "2024-01-15T10:30:00"
  }
}
```

### 2. Check Stock Availability
```bash
curl -X POST http://localhost:8004/api/internal/inventory/check-stock \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "product-uuid",
    "requestedQuantity": 10
  }'
```

### 3. Create Reservation
```bash
curl -X POST http://localhost:8004/api/internal/reservations \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "product-uuid",
    "orderId": "order-uuid",
    "quantity": 5
  }'
```

### 4. Update Stock (Admin)
```bash
curl -X PUT http://localhost:8004/api/admin/inventory/product-uuid \
  -H "Content-Type: application/json" \
  -H "X-User-Id: admin-uuid" \
  -H "X-User-Role: ADMIN" \
  -d '{
    "quantity": 200,
    "reason": "New stock received"
  }'
```

### 5. Get Low Stock Items (Admin)
```bash
curl -X GET "http://localhost:8004/api/admin/inventory/low-stock?page=0&size=20" \
  -H "X-User-Id: admin-uuid" \
  -H "X-User-Role: ADMIN"
```

## Error Codes

| Code | HTTP | Description |
|------|------|-------------|
| INVENTORY_NOT_FOUND | 404 | Inventory record not found |
| INSUFFICIENT_STOCK | 400 | Not enough stock available |
| RESERVATION_NOT_FOUND | 404 | Reservation not found |
| INVALID_QUANTITY | 400 | Invalid quantity provided |
| RESERVATION_EXPIRED | 400 | Reservation has expired |
| CONCURRENT_UPDATE | 409 | Concurrent modification detected |
| INVALID_REQUEST | 400 | Invalid request data |
| UNAUTHORIZED | 401 | Authentication failed |
| FORBIDDEN | 403 | Insufficient permissions |
| INTERNAL_SERVER_ERROR | 500 | Server error |

## Key Workflows

### 1. Stock Check Before Order
```
Order Service
    ↓
Check Stock Availability
    ├─ Sufficient Stock → OK
    └─ Insufficient Stock → ERROR
```

### 2. Create Reservation
```
Order Service
    ↓
Create Inventory Reservation
    ├─ Reserve Quantity
    └─ Set Expiration Time
```

### 3. Confirm Reservation (on Payment)
```
Payment Service
    ↓
Confirm Reservation
    ├─ Update Inventory
    └─ Log Transaction
```

### 4. Stock Update
```
Admin
    ↓
Update Stock
    ├─ Adjust Quantity
    ├─ Create Transaction
    └─ Check Reorder Level
```

## Integration Points

### With Product Service
- Fetch product details
- Validate product existence
- Get product categories

### With Order Service
- Check stock before order creation
- Create reservations for orders
- Confirm reservations on payment

### With Payment Service
- Confirm reservations on successful payment
- Release reservations on failed payment

## Performance Considerations

- **Indexes**: On product_id, status, created_at
- **Batch Operations**: Efficient bulk updates
- **Transaction Logging**: Asynchronous logging option
- **Caching**: Consider cache for high-traffic products

## Monitoring & Logging

### Actuator Endpoints
```
GET /actuator/health           - Service health
GET /actuator/metrics          - Metrics
```

### Key Metrics
- Current stock levels
- Reservation count
- Transaction volume
- Low stock items

## Testing

### Unit Tests
- Stock calculation tests
- Reservation logic tests
- Transaction logging tests

### Integration Tests
- Inventory CRUD operations
- Reservation workflow tests
- Transaction history tests

## Transactions & Concurrency

- **Optimistic Locking**: Version-based updates
- **Row Locks**: For critical inventory updates
- **Transaction Isolation**: READ_COMMITTED level
- **Deadlock Prevention**: Consistent ordering

## Future Enhancements

1. Warehouse management
2. Multiple location support
3. SKU management
4. Barcode/QR code support
5. Inventory forecasting
6. Automated reorder
7. Batch expiration tracking
8. Real-time stock alerts
9. Advanced analytics
10. Integration with 3PL systems

## Troubleshooting

### Insufficient Stock Error
- Check available quantity
- Verify reservations
- Review transaction history

### Reservation Expired
- Check expiration time settings
- Auto-release old reservations
- Verify confirmation timing

### Database Lock Issues
- Check concurrent operations
- Review transaction times
- Monitor database connections

## Technologies

- **Spring Boot**: 3.5.11
- **Spring Data JPA**: ORM
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
