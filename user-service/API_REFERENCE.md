# User Service - API Testing Guide

## Quick Start Testing

### 1. Create a Tenant (if not exists)
Assuming default tenant: `2f34e34a-a524-4aff-9702-35e6f8c6f9d1`

### 2. Test Admin - Create User
```bash
curl -X POST http://localhost:8080/api/admin/users \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 550e8400-e29b-41d4-a716-446655440000" \
  -H "X-User-Role: ADMIN" \
  -H "X-Tenant-ID: 2f34e34a-a524-4aff-9702-35e6f8c6f9d1" \
  -d '{
    "identityUserId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phoneNumber": "+1-234-567-8900",
    "dateOfBirth": "1990-01-15"
  }'
```

**Expected Response (201 Created):**
```json
{
  "success": true,
  "data": {
    "id": "a1b2c3d4-e5f6-47g8-h9i0-j1k2l3m4n5o6",
    "identityUserId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phoneNumber": "+1-234-567-8900",
    "dateOfBirth": "1990-01-15",
    "isActive": true,
    "createdAt": "2024-01-15T10:30:45",
    "updatedAt": "2024-01-15T10:30:45"
  },
  "message": "User created successfully"
}
```

### 3. Get Current User Profile
Use the UUID from previous response as X-User-Id
```bash
curl -X GET http://localhost:8080/api/users/me \
  -H "X-User-Id: f47ac10b-58cc-4372-a567-0e02b2c3d479" \
  -H "X-User-Role: USER"
```

### 4. Update User Profile
```bash
curl -X PUT http://localhost:8080/api/users/me \
  -H "Content-Type: application/json" \
  -H "X-User-Id: f47ac10b-58cc-4372-a567-0e02b2c3d479" \
  -H "X-User-Role: USER" \
  -d '{
    "firstName": "Johnny",
    "phoneNumber": "+1-234-567-8901"
  }'
```

### 5. Add Address
```bash
curl -X POST http://localhost:8080/api/users/me/addresses \
  -H "Content-Type: application/json" \
  -H "X-User-Id: f47ac10b-58cc-4372-a567-0e02b2c3d479" \
  -H "X-User-Role: USER" \
  -d '{
    "fullName": "John Doe",
    "phoneNumber": "+1-234-567-8900",
    "addressLine1": "123 Main Street",
    "addressLine2": "Apt 4B",
    "landmark": "Near Central Park",
    "city": "New York",
    "state": "NY",
    "country": "USA",
    "postalCode": "10001",
    "isDefault": true
  }'
```

### 6. Get All Addresses
```bash
curl -X GET http://localhost:8080/api/users/me/addresses \
  -H "X-User-Id: f47ac10b-58cc-4372-a567-0e02b2c3d479" \
  -H "X-User-Role: USER"
```

### 7. Get Default Address
```bash
curl -X GET http://localhost:8080/api/users/me/addresses/default \
  -H "X-User-Id: f47ac10b-58cc-4372-a567-0e02b2c3d479" \
  -H "X-User-Role: USER"
```

### 8. Set Address as Default
Use the address ID from previous responses
```bash
curl -X PUT http://localhost:8080/api/users/me/addresses/{addressId}/default \
  -H "X-User-Id: f47ac10b-58cc-4372-a567-0e02b2c3d479" \
  -H "X-User-Role: USER"
```

### 9. Get Preferences
```bash
curl -X GET http://localhost:8080/api/users/me/preferences \
  -H "X-User-Id: f47ac10b-58cc-4372-a567-0e02b2c3d479" \
  -H "X-User-Role: USER"
```

### 10. Update Preferences
```bash
curl -X PUT http://localhost:8080/api/users/me/preferences \
  -H "Content-Type: application/json" \
  -H "X-User-Id: f47ac10b-58cc-4372-a567-0e02b2c3d479" \
  -H "X-User-Role: USER" \
  -d '{
    "language": "es",
    "currency": "EUR",
    "emailNotificationsEnabled": true,
    "smsNotificationsEnabled": true
  }'
```

### 11. Search Users (Admin Only)
```bash
curl -X GET \"http://localhost:8080/api/admin/users/search?email=john\" \
  -H "X-User-Id: 550e8400-e29b-41d4-a716-446655440000" \
  -H "X-User-Role: ADMIN" \
  -H "X-Tenant-ID: 2f34e34a-a524-4aff-9702-35e6f8c6f9d1"
```

### 12. List All Users (Admin Only, Paginated)
```bash
curl -X GET \"http://localhost:8080/api/admin/users?page=0&size=10\" \
  -H "X-User-Id: 550e8400-e29b-41d4-a716-446655440000" \
  -H "X-User-Role: ADMIN" \
  -H "X-Tenant-ID: 2f34e34a-a524-4aff-9702-35e6f8c6f9d1"
```

### 13. Deactivate User (Admin Only)
```bash
curl -X PUT \"http://localhost:8080/api/admin/users/f47ac10b-58cc-4372-a567-0e02b2c3d479/status?isActive=false\" \
  -H "X-User-Id: 550e8400-e29b-41d4-a716-446655440000" \
  -H "X-User-Role: ADMIN"
```

### 14. Delete User (Admin Only)
```bash
curl -X DELETE http://localhost:8080/api/admin/users/f47ac10b-58cc-4372-a567-0e02b2c3d479 \
  -H "X-User-Id: 550e8400-e29b-41d4-a716-446655440000" \
  -H "X-User-Role: ADMIN"
```

### 15. Internal API - Get User by ID (For other services)
```bash
curl -X GET http://localhost:8080/api/internal/users/f47ac10b-58cc-4372-a567-0e02b2c3d479
```

### 16. Internal API - Get Default Address (For Order Service)
```bash
curl -X GET http://localhost:8080/api/internal/users/f47ac10b-58cc-4372-a567-0e02b2c3d479/default-address
```

## Error Response Examples

### Missing Required Header
```bash
curl -X GET http://localhost:8080/api/users/me
```

**Response (400 Bad Request):**
```json
{
  "timestamp": "2024-01-15T10:35:22.123",
  "status": 400,
  "error": "BAD_REQUEST",
  "code": "UNAUTHORIZED",
  "message": "Missing required header: X-User-Id",
  "path": "/api/users/me"
}
```

### User Not Found
```bash
curl -X GET http://localhost:8080/api/users/me \
  -H "X-User-Id: 00000000-0000-0000-0000-000000000000" \
  -H "X-User-Role: USER"
```

**Response (404 Not Found):**
```json
{
  "timestamp": "2024-01-15T10:36:45.123",
  "status": 404,
  "error": "NOT_FOUND",
  "code": "USER_NOT_FOUND",
  "message": "User not found with identity ID: 00000000-0000-0000-0000-000000000000",
  "path": "/api/users/me"
}
```

### Unauthorized Admin Access
```bash
curl -X GET http://localhost:8080/api/admin/users \
  -H "X-User-Id: f47ac10b-58cc-4372-a567-0e02b2c3d479" \
  -H "X-User-Role: USER"
```

**Response (403 Forbidden):**
```json
{
  "timestamp": "2024-01-15T10:37:15.123",
  "status": 403,
  "error": "FORBIDDEN",
  "code": "FORBIDDEN",
  "message": "Admin role required for this operation",
  "path": "/api/admin/users"
}
```

### Validation Error
```bash
curl -X POST http://localhost:8080/api/users/me/addresses \
  -H "Content-Type: application/json" \
  -H "X-User-Id: f47ac10b-58cc-4372-a567-0e02b2c3d479" \
  -H "X-User-Role: USER" \
  -d '{
    "fullName": "John Doe",
    "phoneNumber": "invalid"
  }'
```

**Response (400 Bad Request):**
```json
{
  "timestamp": "2024-01-15T10:38:30.123",
  "status": 400,
  "error": "VALIDATION_ERROR",
  "code": "INVALID_REQUEST",
  "message": "Validation failed: phoneNumber: Phone number should be valid, addressLine1: Address line 1 is required",
  "path": "/api/users/me/addresses"
}
```

## Postman Collection

You can import this collection into Postman for easy testing:

1. Create a new Postman collection
2. Add the following environment variables:
   - `base_url`: http://localhost:8080
   - `user_id`: Your test user UUID
   - `admin_id`: Admin UUID
   - `tenant_id`: 2f34e34a-a524-4aff-9702-35e6f8c6f9d1

3. Create requests using the {{variable}} syntax

## Integration Testing Example (Java)

```java
@SpringBootTest
@AutoConfigureMockMvc
class UserServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCreateAndRetrieveUser() throws Exception {
        // Create user
        MvcResult result = mockMvc.perform(post("/api/admin/users")
            .header("X-User-Id", "admin-uuid")
            .header("X-User-Role", "ADMIN")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "identityUserId": "user-uuid",
                  "firstName": "John",
                  "lastName": "Doe",
                  "email": "john@example.com"
                }
                """))
            .andExpect(status().isCreated())
            .andReturn();

        // Extract user ID from response
        String response = result.getResponse().getContentAsString();
        // Parse and verify
    }
}
```

## Performance Testing

### Load Testing with Apache JMeter
1. Create a test plan with 100 threads
2. Ramp-up period: 60 seconds
3. Number of loops: 10
4. Point API endpoints: /api/users/me, /api/users/me/addresses

### Expected Response Times
- GET endpoints: < 100ms
- POST endpoints: < 200ms
- Admin list endpoints: < 500ms (with pagination)

## Notes
- Always include required headers (X-User-Id, X-User-Role)
- Use valid UUIDs (v4 recommended)
- Tenant ID should be consistent (default provided)
- Pagination parameters: page (0-based), size


