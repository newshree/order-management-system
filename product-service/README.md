# Product Service - OMS (Order Management System)

A production-ready Product Catalog microservice for managing products, categories, brands, and units of measure in the Order Management System.

## Overview

The Product Service provides comprehensive product management with features including:
- Product CRUD operations
- Category management with hierarchy
- Brand management
- Unit of measure definitions
- Product search and filtering
- Product validation
- Catalog management

## Project Structure

```
product-service/
├── src/main/java/com/ecom/product/
│   ├── ProductServiceApplication.java       # Main entry point
│   ├── controller/
│   │   ├── ProductController.java           # Product REST endpoints
│   │   ├── CategoryController.java          # Category endpoints
│   │   ├── BrandController.java             # Brand endpoints
│   │   └── UnitOfMeasureController.java     # UOM endpoints
│   ├── service/
│   │   ├── ProductService.java              # Product service interface
│   │   ├── CategoryService.java             # Category service interface
│   │   ├── BrandService.java                # Brand service interface
│   │   ├── UnitOfMeasureService.java        # UOM service interface
│   │   └── impl/
│   │       ├── ProductServiceImpl.java
│   │       ├── CategoryServiceImpl.java
│   │       ├── BrandServiceImpl.java
│   │       └── UnitOfMeasureServiceImpl.java
│   ├── entity/
│   │   ├── Product.java                     # Product entity
│   │   ├── Category.java                    # Category entity
│   │   ├── Brand.java                       # Brand entity
│   │   └── UnitOfMeasure.java               # Unit of measure entity
│   ├── mapper/
│   │   ├── ProductMapper.java               # Product mapper
│   │   ├── CategoryMapper.java              # Category mapper
│   │   ├── BrandMapper.java                 # Brand mapper
│   │   └── UnitOfMeasureMapper.java         # UOM mapper
│   ├── repository/
│   │   ├── ProductRepository.java           # Product repository
│   │   ├── CategoryRepository.java          # Category repository
│   │   ├── BrandRepository.java             # Brand repository
│   │   └── UnitOfMeasureRepository.java     # UOM repository
│   ├── dto/
│   │   ├── request/
│   │   │   ├── ProductCreateRequest.java
│   │   │   ├── ProductUpdateRequest.java
│   │   │   ├── CategoryCreateRequest.java
│   │   │   ├── CategoryUpdateRequest.java
│   │   │   ├── BrandCreateRequest.java
│   │   │   ├── BrandUpdateRequest.java
│   │   │   ├── UnitOfMeasureCreateRequest.java
│   │   │   └── UnitOfMeasureUpdateRequest.java
│   │   └── response/
│   │       ├── ProductResponse.java
│   │       ├── CategoryResponse.java
│   │       ├── BrandResponse.java
│   │       ├── UnitOfMeasureResponse.java
│   │       └── ApiResponse.java
│   ├── enums/
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

### 1. **Product Management**
- Create, read, update, delete products
- Product details: name, description, SKU
- Pricing information
- Product status (active/inactive)
- Product images/media
- Product attributes
- Bulk import/export

### 2. **Category Management**
- Hierarchical category structure
- Parent-child category relationships
- Category descriptions
- SEO-friendly URLs
- Category images
- Display order management

### 3. **Brand Management**
- Brand creation and management
- Brand descriptions
- Brand logos
- Brand status control
- Featured brands

### 4. **Unit of Measure**
- Standard units (kg, liters, pieces, etc.)
- Custom unit definitions
- Unit conversion factors
- Unit symbol management
- Unit description

### 5. **Search & Filtering**
- Full-text search
- Filter by category
- Filter by brand
- Filter by price range
- Filter by attributes
- Pagination support

### 6. **Catalog Management**
- Product visibility control
- Featured products
- Trending products
- Best sellers
- New arrivals

## API Endpoints

### Products
```
GET    /api/products                         - List products (paginated)
POST   /api/products                         - Create product
GET    /api/products/{productId}             - Get product details
PUT    /api/products/{productId}             - Update product
DELETE /api/products/{productId}             - Delete product
GET    /api/products/search?q={query}        - Search products
```

### Categories
```
GET    /api/categories                       - List categories
POST   /api/categories                       - Create category
GET    /api/categories/{categoryId}          - Get category
PUT    /api/categories/{categoryId}          - Update category
DELETE /api/categories/{categoryId}          - Delete category
GET    /api/categories/{categoryId}/products - Category products
```

### Brands
```
GET    /api/brands                           - List brands
POST   /api/brands                           - Create brand
GET    /api/brands/{brandId}                 - Get brand
PUT    /api/brands/{brandId}                 - Update brand
DELETE /api/brands/{brandId}                 - Delete brand
GET    /api/brands/{brandId}/products        - Brand products
```

### Units of Measure
```
GET    /api/units                            - List units
POST   /api/units                            - Create unit
GET    /api/units/{unitId}                   - Get unit
PUT    /api/units/{unitId}                   - Update unit
DELETE /api/units/{unitId}                   - Delete unit
```

### Internal APIs
```
GET    /api/internal/products/{productId}    - Get product (internal)
POST   /api/internal/products/batch          - Batch product info
```

## Database Schema

### Tables
- `product` - Product records
- `category` - Product categories
- `brand` - Product brands
- `unit_of_measure` - Units of measure

### Key Fields

**Product**
- `id` (UUID) - Primary key
- `sku` (VARCHAR) - Stock keeping unit (unique)
- `name` (VARCHAR) - Product name
- `description` (TEXT) - Product description
- `category_id` (UUID) - Category reference
- `brand_id` (UUID) - Brand reference
- `unit_of_measure_id` (UUID) - UOM reference
- `price` (DECIMAL) - Current price
- `cost` (DECIMAL) - Cost price
- `status` (ENUM) - ACTIVE, INACTIVE, DISCONTINUED
- `is_featured` (BOOLEAN) - Featured flag
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

**Category**
- `id` (UUID) - Primary key
- `name` (VARCHAR) - Category name
- `description` (TEXT) - Category description
- `parent_id` (UUID) - Parent category (nullable)
- `display_order` (INT) - Sort order
- `status` (ENUM) - ACTIVE, INACTIVE
- `created_at` (TIMESTAMP)

**Brand**
- `id` (UUID) - Primary key
- `name` (VARCHAR) - Brand name (unique)
- `description` (TEXT)
- `logo_url` (VARCHAR)
- `status` (ENUM) - ACTIVE, INACTIVE
- `created_at` (TIMESTAMP)

**UnitOfMeasure**
- `id` (UUID) - Primary key
- `name` (VARCHAR) - Unit name (unique)
- `symbol` (VARCHAR) - Unit symbol
- `description` (TEXT)
- `conversion_factor` (DECIMAL)
- `created_at` (TIMESTAMP)

## Configuration

### application.properties
```properties
server.port=8003
spring.application.name=product-service

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/product_db
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

### Build
```bash
cd product-service
mvn clean install
```

### Run
```bash
mvn spring-boot:run
```

Or with Docker:
```bash
mvn clean package
docker build -t product-service .
docker run -p 8003:8003 product-service
```

## Architecture

### Layers

```
┌─────────────────────────────────────────┐
│    REST Controllers                     │
│ (Product, Category, Brand, UOM)         │
└────────────┬────────────────────────────┘
             │
┌────────────▼────────────────────────────┐
│     Service Layer                       │
│ (Product, Category, Brand, UOM Services)│
└────────────┬────────────────────────────┘
             │
┌────────────▼────────────────────────────┐
│    Repository Layer                     │
│ (Product, Category, Brand, UOM Repos)   │
└────────────┬────────────────────────────┘
             │
┌────────────▼────────────────────────────┐
│  Database Layer (PostgreSQL)            │
└─────────────────────────────────────────┘
```

## SOLID Principles

- **S**ingle Responsibility: Separate services for each entity
- **O**pen/Closed: Extensible via service interfaces
- **L**iskov Substitution: Service implementations
- **I**nterface Segregation: Focused service contracts
- **D**ependency Inversion: Depends on service abstractions

## API Examples

### 1. Create Product
```bash
curl -X POST http://localhost:8003/api/products \
  -H "Content-Type: application/json" \
  -H "X-User-Id: admin-uuid" \
  -d '{
    "sku": "PROD-001",
    "name": "Laptop Computer",
    "description": "High-performance laptop",
    "categoryId": "category-uuid",
    "brandId": "brand-uuid",
    "unitOfMeasureId": "unit-uuid",
    "price": 999.99,
    "cost": 600.00
  }'
```

Response:
```json
{
  "success": true,
  "data": {
    "id": "product-uuid",
    "sku": "PROD-001",
    "name": "Laptop Computer",
    "description": "High-performance laptop",
    "categoryId": "category-uuid",
    "brandId": "brand-uuid",
    "price": 999.99,
    "status": "ACTIVE",
    "createdAt": "2024-01-15T10:30:00"
  }
}
```

### 2. List Products with Filters
```bash
curl -X GET "http://localhost:8003/api/products?categoryId=cat-uuid&page=0&size=20"
```

### 3. Search Products
```bash
curl -X GET "http://localhost:8003/api/products/search?q=laptop&page=0&size=20"
```

### 4. Create Category
```bash
curl -X POST http://localhost:8003/api/categories \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Electronics",
    "description": "Electronic products",
    "displayOrder": 1
  }'
```

### 5. Get Category Products
```bash
curl -X GET "http://localhost:8003/api/categories/category-uuid/products?page=0&size=20"
```

### 6. Create Brand
```bash
curl -X POST http://localhost:8003/api/brands \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Dell",
    "description": "Dell Technologies",
    "logoUrl": "https://example.com/logo.png"
  }'
```

### 7. Get Unit of Measure
```bash
curl -X GET http://localhost:8003/api/units/unit-uuid
```

## Category Hierarchy Example

```
Electronics (parent)
├── Computers
│   ├── Laptops
│   ├── Desktops
│   └── Tablets
├── Mobile Devices
│   ├── Smartphones
│   └── Accessories
└── Wearables
    ├── Smartwatches
    └── Fitness Trackers
```

## Error Codes

| Code | HTTP | Description |
|------|------|-------------|
| PRODUCT_NOT_FOUND | 404 | Product doesn't exist |
| CATEGORY_NOT_FOUND | 404 | Category doesn't exist |
| BRAND_NOT_FOUND | 404 | Brand doesn't exist |
| UNIT_NOT_FOUND | 404 | Unit doesn't exist |
| SKU_ALREADY_EXISTS | 400 | SKU already in use |
| BRAND_NAME_EXISTS | 400 | Brand name taken |
| UNIT_NAME_EXISTS | 400 | Unit name taken |
| INVALID_PARENT_CATEGORY | 400 | Invalid parent category |
| INVALID_REQUEST | 400 | Invalid request data |
| UNAUTHORIZED | 401 | Authentication failed |
| FORBIDDEN | 403 | Insufficient permissions |
| INTERNAL_SERVER_ERROR | 500 | Server error |

## Integration Points

### With Cart Service
- Fetch product details
- Validate products in cart
- Get current pricing

### With Order Service
- Get product info for orders
- Validate order items
- Calculate order totals

### With Inventory Service
- Link products to inventory
- Stock level information
- Product availability

## Performance Considerations

- **Indexes**: On SKU, name, category_id, brand_id
- **Pagination**: Efficient listing
- **Lazy Loading**: Category and brand relationships
- **Caching**: Consider cache for frequently accessed products

## Monitoring & Logging

### Actuator Endpoints
```
GET /actuator/health           - Service health
GET /actuator/metrics          - Metrics
```

## Testing

### Unit Tests
- Service logic tests
- Mapper tests
- Validation tests

### Integration Tests
- CRUD operations
- Search/filter functionality
- Database operations

## Sample Data Setup

```sql
-- Categories
INSERT INTO category (id, name, description, display_order) 
VALUES ('cat-1', 'Electronics', 'Electronic products', 1);

-- Brands
INSERT INTO brand (id, name, description)
VALUES ('brand-1', 'Dell', 'Dell Technologies');

-- Units of Measure
INSERT INTO unit_of_measure (id, name, symbol)
VALUES ('unit-1', 'Piece', 'pcs');

-- Products
INSERT INTO product (id, sku, name, price, category_id, brand_id, unit_of_measure_id)
VALUES ('prod-1', 'DELL-001', 'XPS 13', 999.99, 'cat-1', 'brand-1', 'unit-1');
```

## Future Enhancements

1. Product variants (color, size, etc.)
2. Product recommendations
3. Customer reviews and ratings
4. Product images/media management
5. SEO optimization
6. Product analytics
7. Pricing rules/discounts
8. Product bundles
9. Subscription products
10. Digital products support

## Troubleshooting

### Product Not Found
- Verify product ID/SKU
- Check product status (active)
- Confirm product exists

### Duplicate SKU Error
- Use unique SKU values
- Check existing products
- Update if needed

### Category Hierarchy Issues
- Check parent-child relationships
- Avoid circular references
- Verify parent exists

## Technologies

- **Spring Boot**: 3.5.13
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
