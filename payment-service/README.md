# Payment Service - OMS (Order Management System)

A production-ready Payment Processing microservice for handling payment transactions, refunds, and payment status tracking in the Order Management System.

## Overview

The Payment Service processes payments with features including:
- Multiple payment method support (Credit Card, Debit Card, Digital Wallet)
- Payment transaction processing
- Refund handling
- Payment status tracking
- Transaction history and audit trail
- Payment reconciliation
- Redis caching for performance

## Project Structure

```
payment-service/
├── src/main/java/com/ecom/payment/
│   ├── PaymentServiceApplication.java       # Main entry point
│   ├── controller/
│   │   └── PaymentController.java           # REST API endpoints
│   ├── service/
│   │   ├── PaymentService.java              # Service interface
│   │   └── impl/
│   │       └── PaymentServiceImpl.java       # Service implementation
│   ├── entity/
│   │   ├── Payment.java                     # Payment records
│   │   └── PaymentTransaction.java          # Transaction details
│   ├── mapper/
│   │   └── PaymentMapper.java               # DTO mapping
│   ├── repository/
│   │   ├── PaymentRepository.java           # Payment CRUD
│   │   └── PaymentTransactionRepository.java # Transaction queries
│   ├── dto/
│   │   ├── request/
│   │   │   ├── InitiatePaymentRequest.java
│   │   │   └── RefundPaymentRequest.java
│   │   └── response/
│   │       ├── PaymentResponse.java
│   │       ├── PaymentTransactionResponse.java
│   │       ├── RefundResponse.java
│   │       ├── PageResponse.java
│   │       └── ApiResponse.java
│   ├── enums/
│   │   ├── PaymentStatus.java
│   │   ├── PaymentMethod.java
│   │   ├── PaymentTransactionType.java
│   │   └── ErrorCode.java
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   ├── BadRequestException.java
│   │   ├── ResourceNotFoundException.java
│   │   └── ErrorResponse.java
│   ├── util/
│   │   └── PaginationUtils.java
│   └── resources/
│       └── application.properties
└── pom.xml
```

## Features

### 1. **Payment Processing**
- Initiate payment transactions
- Support multiple payment methods
- Validate payment details
- Process payment authorizations
- Handle payment confirmations

### 2. **Payment Methods**
- CREDIT_CARD - Visa, Mastercard, etc.
- DEBIT_CARD - Bank debit cards
- DIGITAL_WALLET - PayPal, Apple Pay, Google Pay
- NET_BANKING - Online banking transfers
- UPI - Unified Payments Interface (India)

### 3. **Transaction Management**
- Create transaction records
- Track transaction status
- Store payment details securely
- Maintain transaction history
- Transaction audit trail

### 4. **Refund Processing**
- Full refunds
- Partial refunds
- Refund status tracking
- Automatic refund rollback
- Refund reason tracking

### 5. **Payment Status Tracking**
- INITIATED - Payment started
- PENDING - Awaiting approval
- APPROVED - Payment approved
- COMPLETED - Payment successful
- FAILED - Payment failed
- CANCELLED - Payment cancelled
- REFUNDED - Payment refunded

## API Endpoints

### Payment Processing
```
POST   /api/payments/initiate                - Start payment
GET    /api/payments/{paymentId}             - Get payment details
GET    /api/payments                         - List payments (paginated)
GET    /api/payments?orderId={orderId}       - Get payment by order
```

### Refunds
```
POST   /api/payments/{paymentId}/refund      - Process refund
GET    /api/payments/{paymentId}/refunds     - Get refund history
```

### Transactions
```
GET    /api/payments/transactions            - Transaction history
GET    /api/payments/transactions/{txnId}    - Get transaction details
```

### Internal APIs
```
POST   /api/internal/payments/verify         - Verify payment (internal)
POST   /api/internal/payments/confirm        - Confirm payment (internal)
GET    /api/internal/payments/{paymentId}    - Get payment (internal)
```

## Database Schema

### Tables
- `payment` - Payment records
- `payment_transaction` - Transaction details

### Key Fields

**Payment**
- `id` (UUID) - Primary key
- `order_id` (UUID) - Associated order
- `user_id` (UUID) - Customer
- `amount` (DECIMAL) - Payment amount
- `currency` (VARCHAR) - Currency code (USD, EUR, etc.)
- `payment_method` (ENUM) - Payment method used
- `status` (ENUM) - Current status
- `gateway_transaction_id` (VARCHAR) - External gateway ID
- `created_at` (TIMESTAMP) - Payment creation
- `updated_at` (TIMESTAMP) - Last update

**PaymentTransaction**
- `id` (UUID) - Primary key
- `payment_id` (UUID) - Reference to payment
- `type` (ENUM) - CHARGE, REFUND, REVERSAL
- `amount` (DECIMAL) - Transaction amount
- `status` (ENUM) - Transaction status
- `description` (TEXT) - Transaction details
- `gateway_response` (TEXT) - Gateway response
- `created_at` (TIMESTAMP)

## Configuration

### application.properties
```properties
server.port=8006
spring.application.name=payment-service

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/payment_db
spring.datasource.username=postgres
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=validate

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379

# Payment Gateway (Example: Stripe)
payment.gateway.stripe.api_key=sk_test_xxxxx
payment.gateway.stripe.publishable_key=pk_test_xxxxx

# Payment Config
payment.max_retry_attempts=3
payment.timeout_seconds=30
```

## Installation & Setup

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL
- Redis
- Payment Gateway Account (Stripe, PayPal, etc.)

### Build
```bash
cd payment-service
mvn clean install
```

### Run
```bash
mvn spring-boot:run
```

Or with Docker:
```bash
mvn clean package
docker build -t payment-service .
docker run -p 8006:8006 payment-service
```

## Architecture

### Layers

```
┌─────────────────────────────────────────┐
│     REST Controller                     │
│   (PaymentController)                   │
└────────────┬────────────────────────────┘
             │
┌────────────▼────────────────────────────┐
│     Service Layer                       │
│   (PaymentService)                      │
└────────────┬────────────────────────────┘
             │
┌────────────▼────────────────────────────┐
│ Repository Layer + Payment Gateway      │
│ (PaymentRepository, PaymentGateway)     │
└────────────┬────────────────────────────┘
             │
┌────────────▼────────────────────────────┐
│  Database Layer (PostgreSQL + Redis)    │
└─────────────────────────────────────────┘
```

### Payment Flow

```
Order Service
    ↓
Initiate Payment
    ↓
Payment Gateway (Stripe/PayPal)
    ├─ Success → APPROVED → COMPLETED
    └─ Failure → FAILED
    ↓
Update Order Status
    ├─ CONFIRMED (if successful)
    └─ CANCELLED (if failed)
```

## SOLID Principles

- **S**ingle Responsibility: Payment vs Transaction handling
- **O**pen/Closed: Extensible payment method support
- **L**iskov Substitution: Payment method implementations
- **I**nterface Segregation: Focused service interfaces
- **D**ependency Inversion: Depends on gateway abstractions

## API Examples

### 1. Initiate Payment
```bash
curl -X POST http://localhost:8006/api/payments/initiate \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "order-uuid",
    "userId": "user-uuid",
    "amount": 215.97,
    "currency": "USD",
    "paymentMethod": "CREDIT_CARD",
    "cardDetails": {
      "cardNumber": "4111111111111111",
      "expiryMonth": 12,
      "expiryYear": 2025,
      "cvv": "123"
    }
  }'
```

Response:
```json
{
  "success": true,
  "data": {
    "id": "payment-uuid",
    "orderId": "order-uuid",
    "amount": 215.97,
    "currency": "USD",
    "status": "COMPLETED",
    "paymentMethod": "CREDIT_CARD",
    "gatewayTransactionId": "txn_1234567890",
    "createdAt": "2024-01-15T10:30:00"
  }
}
```

### 2. Get Payment Details
```bash
curl -X GET http://localhost:8006/api/payments/payment-uuid \
  -H "X-User-Id: user-uuid"
```

### 3. Process Refund
```bash
curl -X POST http://localhost:8006/api/payments/payment-uuid/refund \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 100.00,
    "reason": "Product defective"
  }'
```

Response:
```json
{
  "success": true,
  "data": {
    "id": "refund-uuid",
    "paymentId": "payment-uuid",
    "amount": 100.00,
    "status": "COMPLETED",
    "reason": "Product defective",
    "createdAt": "2024-01-15T11:00:00"
  }
}
```

### 4. List Payments (Paginated)
```bash
curl -X GET "http://localhost:8006/api/payments?page=0&size=20" \
  -H "X-User-Id: user-uuid"
```

### 5. Get Transaction History
```bash
curl -X GET "http://localhost:8006/api/payments/transactions?page=0&size=10" \
  -H "X-User-Id: user-uuid"
```

## Payment Status Transitions

```
INITIATED
  └─→ PENDING (payment processing)

PENDING
  ├─→ APPROVED (authorization approved)
  └─→ FAILED (declined/error)

APPROVED
  ├─→ COMPLETED (capture successful)
  └─→ FAILED (capture failed)

COMPLETED
  ├─→ REFUNDED (full/partial refund)
  └─→ (final state)

FAILED / CANCELLED
  └─→ (final state - no transitions)

REFUNDED
  └─→ (final state)
```

## Error Codes

| Code | HTTP | Description |
|------|------|-------------|
| PAYMENT_NOT_FOUND | 404 | Payment record not found |
| ORDER_NOT_FOUND | 404 | Associated order not found |
| INVALID_AMOUNT | 400 | Invalid payment amount |
| INVALID_CARD | 400 | Invalid card details |
| PAYMENT_DECLINED | 400 | Card declined by bank |
| INSUFFICIENT_FUNDS | 400 | Insufficient balance |
| INVALID_STATUS_TRANSITION | 400 | Invalid status change |
| REFUND_EXCEEDS_AMOUNT | 400 | Refund > payment amount |
| GATEWAY_ERROR | 502 | Payment gateway error |
| INVALID_REQUEST | 400 | Invalid request data |
| UNAUTHORIZED | 401 | Authentication failed |
| INTERNAL_SERVER_ERROR | 500 | Server error |

## Security Considerations

### PCI Compliance
- Never store full card numbers
- Use tokenization for card storage
- Encrypt sensitive data
- Comply with PCI DSS standards

### Data Protection
- Encrypt card details in transit (HTTPS)
- Secure payment gateway communication
- Mask card numbers in logs
- Regular security audits

### Fraud Prevention
- Validate amount ranges
- Check for duplicate payments
- Monitor transaction patterns
- Rate limiting on endpoints

## Integration Points

### With Order Service
- Create payment for order
- Update order status on payment
- Get order details
- Confirm/cancel order

### With Inventory Service
- Confirm inventory on payment
- Release reservations if payment fails
- Validate order items

### With User Service
- Get user information
- Store billing address
- Get user preferences

## Performance Considerations

- **Redis Caching**: Recent payment lookups
- **Pagination**: Efficient list retrieval
- **Indexes**: On order_id, user_id, status
- **Batch Processing**: Bulk refund operations
- **Connection Pooling**: Database connections

## Monitoring & Logging

### Actuator Endpoints
```
GET /actuator/health           - Service health
GET /actuator/metrics          - Metrics
```

### Key Metrics
- Payments processed per hour
- Success/failure rates
- Average processing time
- Refund rates
- Payment method distribution

## Testing

### Unit Tests
- Payment processing logic
- Status transitions
- Refund calculations

### Integration Tests
- Payment gateway integration
- Database operations
- Complete payment workflow

### Test Cards
- Visa: 4111 1111 1111 1111
- Mastercard: 5555 5555 5555 4444
- Amex: 3714 496353 98431

## Future Enhancements

1. Recurring/subscription payments
2. Payment splitting
3. Multi-currency support
4. Payment analytics dashboard
5. Webhook notifications
6. 3D Secure integration
7. Payment plan support
8. Bill payment integration
9. Escrow payment support
10. Settlement reconciliation

## Troubleshooting

### Payment Declined
- Verify card details
- Check card limits
- Contact payment gateway
- Check fraud filters

### Gateway Timeout
- Check gateway status
- Retry payment
- Check network connectivity
- Verify API credentials

### Refund Failed
- Check refund amount
- Verify payment status
- Check gateway limits
- Contact gateway support

## Technologies

- **Spring Boot**: 3.5.14
- **Spring Data JPA**: ORM
- **Spring Data Redis**: Caching
- **PostgreSQL**: Primary database
- **Redis**: Cache layer
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
