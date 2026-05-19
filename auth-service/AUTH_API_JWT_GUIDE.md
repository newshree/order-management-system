# Auth Service API Flow & JWT Authentication Guide

## Table of Contents
1. [JWT Basics](#jwt-basics)
2. [Spring Security Fundamentals](#spring-security-fundamentals)
3. [Auth Service Architecture](#auth-service-architecture)
4. [Complete API Flow](#complete-api-flow)
5. [Step-by-Step Examples](#step-by-step-examples)
6. [Security Features](#security-features)

---

## JWT Basics

### What is JWT?

JWT stands for **JSON Web Token**. It's a secure way to transmit information between parties (client and server) as a JSON object. The token is digitally signed, meaning the server can verify its authenticity.

### JWT Structure

A JWT token consists of three parts separated by dots (`.`):

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIn0.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ

[Header].[Payload].[Signature]
```

#### 1. Header
Contains metadata about the token type and the signing algorithm.

```json
{
  "alg": "HS256",     // Algorithm used (HMAC SHA-256)
  "typ": "JWT"        // Token type
}
```

#### 2. Payload (Claims)
Contains the actual data (claims) about the user.

```json
{
  "sub": "user@example.com",              // Subject (user identifier)
  "iat": 1516239022,                      // Issued at (timestamp)
  "exp": 1516242622                       // Expiration (timestamp)
}
```

Common claims:
- `sub` (subject): User identifier (in your case, email)
- `iat` (issued at): When token was created
- `exp` (expiration): When token expires
- Custom claims: Any additional data you want to include

#### 3. Signature
A cryptographic signature that verifies the token hasn't been tampered with.

```
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret_key
)
```

### Why JWT is Secure

1. **Base64 Encoded**: Not encrypted, but encoded (human unreadable)
2. **Digitally Signed**: Using a secret key (only server knows)
3. **Tamper-Proof**: If payload is modified, signature becomes invalid
4. **Stateless**: No need to store sessions on the server

### JWT Workflow

```
Client                          Server
  |                               |
  |-------- 1. Login ----------->|
  |      (email + password)      |
  |                               |
  |<--- 2. JWT Token Generated ---|
  |    (Access + Refresh Token)   |
  |                               |
  |---- 3. API Request with JWT ->|
  |    Authorization: Bearer JWT  |
  |                               |
  |<--- 4. Validate Token --------|
  |    Check signature & expiry   |
  |                               |
  |<--- 5. Send Response ---------|
  |       (if token valid)        |
```

---

## Spring Security Fundamentals

### What is Spring Security?

Spring Security is a framework that helps protect your application by:
- Authenticating users (verifying who they are)
- Authorizing users (determining what they can access)
- Protecting against common security threats (CSRF, XSS, etc.)

### Key Concepts

#### 1. Authentication
**Definition**: Verifying that a user is who they claim to be.

**In Your System**:
```
User submits email + password
    ↓
AuthService checks if user exists
    ↓
PasswordEncoder verifies password
    ↓
If valid, generate JWT tokens
    ↓
Return tokens to user
```

#### 2. Authorization
**Definition**: Determining what an authenticated user can do.

**In Your System**:
```
User sends API request with JWT
    ↓
JwtAuthenticationFilter validates token
    ↓
Extract user email from token
    ↓
Set user in SecurityContext
    ↓
User can now access protected resources
```

#### 3. SecurityContext
A container that holds the currently authenticated user information during a request.

```java
// In JwtAuthenticationFilter
SecurityContextHolder.getContext().setAuthentication(authentication);
// Now the user is available throughout the request
```

#### 4. Authentication Object
Represents the authenticated user with their credentials.

```java
UsernamePasswordAuthenticationToken authentication = 
    new UsernamePasswordAuthenticationToken(email, null, null);
// First param: user identifier (email)
// Second param: credentials (null - we already verified)
// Third param: authorities/roles (null - no specific roles in basic setup)
```

#### 5. Filter Chain
Security filters that intercept requests sequentially.

```
Request 
  ↓
Filter 1 (CORS)
  ↓
Filter 2 (CSRF)
  ↓
Filter 3 (Session Management)
  ↓
Filter 4 (JWT Authentication Filter) ← Your custom filter
  ↓
Filter 5 (Authorization)
  ↓
Your Controller
```

### Spring Security Configuration in Your App

#### CSRF Disabled
```java
.csrf(csrf -> csrf.disable())
```
- CSRF (Cross-Site Request Forgery) protection is for session-based auth
- Not needed for stateless JWT-based APIs

#### Stateless Session Management
```java
.sessionManagement(session -> session
    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
)
```
- No session cookies are created
- Each request is independent
- JWT token carries all necessary information

#### Permit Public Endpoints
```java
.requestMatchers(
    "/api/auth/register",
    "/api/auth/login",
    "/api/auth/refresh",
    "/api/auth/validate"
).permitAll()
```
- These endpoints are accessible without authentication
- No JWT token required

#### Require Authentication for Others
```java
.anyRequest().authenticated()
```
- All other endpoints require valid JWT token

---

## Auth Service Architecture

### Component Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    Client Application                        │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                   REST API Controller                         │
│  - /api/auth/register   - /api/auth/login                   │
│  - /api/auth/refresh    - /api/auth/validate                │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                   Authentication Service                      │
│  - register(RegisterRequest)                                │
│  - login(LoginRequest)                                      │
│  - refreshToken(String)                                    │
│  - validateToken(String)                                   │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│  ┌──────────────┐  ┌─────────────┐  ┌──────────────┐      │
│  │  JwtUtil     │  │PasswordEnc  │  │AuthMapper    │      │
│  │  (Token Ops) │  │  (Hash Pwd) │  │(Data Mapping)│      │
│  └──────────────┘  └─────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│            JwtAuthenticationFilter (Spring Filter)          │
│  - Intercepts incoming requests                            │
│  - Extracts JWT from Authorization header                  │
│  - Validates token signature and expiration                │
│  - Sets user in SecurityContext                            │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                      Database (User Table)                  │
│  - Stores user credentials (hashed password)               │
│  - Stores user roles and status                            │
└─────────────────────────────────────────────────────────────┘
```

### Key Components

#### 1. **AuthController** (REST Endpoints)
- Receives HTTP requests
- Delegates to AuthService
- Returns standardized responses

#### 2. **AuthService** (Business Logic)
- Implements authentication logic
- Validates user credentials
- Generates and refreshes tokens
- Coordinates between layers

#### 3. **JwtUtil** (Token Operations)
- Generates new tokens
- Validates existing tokens
- Extracts claims from tokens
- Manages token expiration

#### 4. **JwtAuthenticationFilter** (Security Filter)
- Intercepts every request
- Validates JWT tokens
- Sets user in Spring Security context
- Allows request to proceed if valid

#### 5. **PasswordEncoder** (Security)
- Hashes passwords using BCrypt
- Compares plain password with hashed password
- Ensures passwords are never stored in plain text

#### 6. **UserRepository** (Data Access)
- Queries database for users
- Stores new user registrations
- Finds users by email

---

## Complete API Flow

### Flow 1: User Registration

```
Client sends POST /api/auth/register
{
  "email": "user@example.com",
  "password": "password123"
}
    ↓
AuthController.register() receives request
    ↓
Input validation (email format, password length)
    ↓
AuthService.register() called
    ↓
Check if email already exists in database
    ├─ YES → Throw BadRequestException (USER_ALREADY_EXISTS)
    └─ NO → Continue
    ↓
Hash password using BCryptPasswordEncoder
    └─ Plain: "password123" 
    └─ Hashed: "$2a$10$N9qo8uLO..."
    ↓
Create new User entity with:
  - UUID id (auto-generated)
  - email
  - passwordHash
  - role: "CUSTOMER"
  - isActive: true
    ↓
Save to database via UserRepository
    ↓
Return 201 Created response
{
  "success": true,
  "message": "User registered successfully",
  "data": "User account created. You can now login."
}
```

### Flow 2: User Login

```
Client sends POST /api/auth/login
{
  "email": "user@example.com",
  "password": "password123"
}
    ↓
AuthController.login() receives request
    ↓
Input validation
    ↓
AuthService.login() called
    ↓
Query database: findByEmail("user@example.com")
    ├─ User not found → Throw ResourceNotFoundException
    └─ User found → Continue
    ↓
Verify password
    ├─ passwordEncoder.matches(plain, hash)
    ├─ Compare "password123" with stored hash
    ├─ Wrong password → Throw BadRequestException (INVALID_CREDENTIALS)
    └─ Correct password → Continue
    ↓
Check if account is active
    ├─ isActive == false → Throw BadRequestException (UNAUTHORIZED)
    └─ isActive == true → Continue
    ↓
Generate JWT Access Token (expires in 15 minutes)
    └─ JwtUtil.generateAccessToken(email)
    └─ Token subject: "user@example.com"
    └─ Signed with secret key using HS256
    └─ Example: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    ↓
Generate JWT Refresh Token (expires in 7 days)
    └─ JwtUtil.generateRefreshToken(email)
    └─ Longer expiration for requesting new access tokens
    ↓
Map User + tokens to AuthResponse
    └─ userId, email, role, accessToken, refreshToken, expiresIn
    ↓
Return 200 OK response
{
  "success": true,
  "message": "Login successful",
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "role": "CUSTOMER",
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 900000  // 15 minutes in milliseconds
  }
}
```

### Flow 3: Access Protected API with JWT

```
Client calls GET /api/protected-endpoint
Header: Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
    ↓
Request enters Spring Security filter chain
    ↓
JwtAuthenticationFilter.doFilterInternal() intercepts
    ↓
Extract Authorization header
    ├─ Header not found → Skip authentication, continue
    ├─ Header doesn't start with "Bearer " → Skip
    └─ Header valid → Continue
    ↓
Extract token by removing "Bearer " prefix
    └─ Token: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    ↓
Validate token
    ├─ JwtUtil.validateToken(token)
    ├─ Parse token with signing key
    ├─ Verify signature is valid
    ├─ Check if token is expired
    ├─ Token invalid/expired → Skip authentication
    └─ Token valid → Continue
    ↓
Extract email from token
    └─ JwtUtil.extractEmail(token)
    └─ Get value from "sub" claim
    └─ Extract: "user@example.com"
    ↓
Create Authentication object
    ├─ UsernamePasswordAuthenticationToken
    ├─ Principal: "user@example.com"
    └─ Set in SecurityContext
    ↓
SecurityContextHolder.getContext().setAuthentication(auth)
    └─ User is now authenticated for this request
    ↓
Pass request to next filter
    ↓
Request reaches your controller
    ↓
Controller can access current user:
    └─ SecurityContextHolder.getContext().getAuthentication()
    └─ Returns: "user@example.com"
    ↓
Return protected resource response
```

### Flow 4: Refresh Expired Access Token

```
Client's access token is about to expire
Client sends POST /api/auth/refresh
Header: Authorization: Bearer [REFRESH_TOKEN]
    ↓
AuthController.refresh() receives request
    ↓
Extract refresh token from Authorization header
    ↓
Input validation
    ↓
AuthService.refreshToken(refreshToken) called
    ↓
Validate refresh token
    ├─ JwtUtil.validateToken(refreshToken)
    ├─ Check signature and expiration
    ├─ Token invalid → Throw BadRequestException (TOKEN_INVALID)
    └─ Token valid → Continue
    ↓
Extract email from refresh token
    └─ JwtUtil.extractEmail(refreshToken)
    └─ Get: "user@example.com"
    ↓
Query database: findByEmail("user@example.com")
    ├─ User not found → Throw ResourceNotFoundException
    └─ User found → Continue
    ↓
Generate NEW Access Token (same email, new expiration)
    ├─ Expiration: Current time + 15 minutes
    └─ Refresh token remains the same
    ↓
Map and return new tokens
    └─ New access token, same refresh token
    ↓
Return 200 OK with new tokens
{
  "success": true,
  "message": "Token refreshed successfully",
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "role": "CUSTOMER",
    "accessToken": "[NEW_ACCESS_TOKEN]",
    "refreshToken": "[SAME_REFRESH_TOKEN]",
    "expiresIn": 900000
  }
}
```

### Flow 5: Validate Token

```
Client sends GET /api/auth/validate
Header: Authorization: Bearer [JWT_TOKEN]
    ↓
AuthController.validateToken() receives request
    ↓
Extract token from Authorization header
    ↓
Input validation
    ↓
AuthService.validateToken(token) called
    ↓
JwtUtil.validateToken(token)
    ├─ Try to parse token with signing key
    ├─ If parsing fails → return false
    ├─ Check signature validity
    ├─ Check expiration time
    ├─ If any check fails → return false
    └─ All checks pass → return true
    ↓
Return validation result
{
  "success": true,
  "message": "Token is valid",
  "data": true
}
OR
{
  "success": false,
  "message": "Token is invalid",
  "data": false
}
```

---

## Step-by-Step Examples

### Example 1: Complete User Journey

#### Step 1: Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "SecurePass123"
  }'
```

Response:
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": "User account created. You can now login.",
  "generatedAt": "2024-01-15T10:30:45"
}
```

**Behind the scenes:**
- Password "SecurePass123" is hashed with BCrypt
- User stored in database with hashed password
- No plain text password is ever stored

#### Step 2: Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "SecurePass123"
  }'
```

Response:
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "userId": "123e4567-e89b-12d3-a456-426614174000",
    "email": "john@example.com",
    "role": "CUSTOMER",
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwiaWF0IjoxNjczNzYzNDQ1LCJleHAiOjE2NzM3NjQzNDV9.x3K9Z2L8...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwiaWF0IjoxNjczNzYzNDQ1LCJleHAiOjE2NzQzNjgzNDV9.a1B2C3D4...",
    "expiresIn": 900000
  },
  "generatedAt": "2024-01-15T10:31:00"
}
```

**Token breakdown:**
- Access token expires in 900,000 ms (15 minutes)
- Refresh token expires in 7 days
- Both are signed and cannot be forged

#### Step 3: Use Access Token to Call Protected API
```bash
curl -X GET http://localhost:8080/api/protected-endpoint \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwiaWF0IjoxNjczNzYzNDQ1LCJleHAiOjE2NzM3NjQzNDV9.x3K9Z2L8..."
```

**Behind the scenes:**
1. Request intercepted by JwtAuthenticationFilter
2. Token extracted from Authorization header
3. Signature verified using server's secret key
4. Expiration checked (not expired yet)
5. Email extracted from token: "john@example.com"
6. User set in SecurityContext
7. Request allowed to proceed

#### Step 4: Access Token Expires After 15 Minutes

```bash
curl -X GET http://localhost:8080/api/protected-endpoint \
  -H "Authorization: Bearer [SAME_ACCESS_TOKEN]"
```

Response:
```
401 Unauthorized
(Token rejected - expired)
```

**What happened:**
- Current time > token expiration time
- Token validation fails
- JwtAuthenticationFilter doesn't set user in SecurityContext
- Controller either returns 401 or request is rejected

#### Step 5: Refresh Token to Get New Access Token
```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwiaWF0IjoxNjczNzYzNDQ1LCJleHAiOjE2NzQzNjgzNDV9.a1B2C3D4..."
```

Response:
```json
{
  "success": true,
  "message": "Token refreshed successfully",
  "data": {
    "userId": "123e4567-e89b-12d3-a456-426614174000",
    "email": "john@example.com",
    "role": "CUSTOMER",
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwiaWF0IjoxNjczNzYzNDQ1LCJleHAiOjE2NzM3NjQzNDV9.y4L9M3K2...",
    "refreshToken": "[SAME_REFRESH_TOKEN]",
    "expiresIn": 900000
  }
}
```

**What happened:**
- Refresh token validated (not expired - valid for 7 days)
- New access token generated (valid for 15 minutes)
- Refresh token remains the same
- User can continue using the app

---

## Security Features

### 1. Password Security

**Bcrypt Hashing**
```
Plain password: "MyPassword123"
       ↓
   BCrypt Hash Function
       ↓
Stored hash: "$2a$10$N9qo8uLO...eIyc1uEEzU"
```

Why Bcrypt is secure:
- **One-way function**: Cannot reverse the hash
- **Salt**: Random value added before hashing (prevents rainbow tables)
- **Slow**: Takes significant time to hash (prevents brute force)
- **Adaptive**: Can increase iterations as computing power grows

**Verification Process**
```
User enters: "MyPassword123"
       ↓
BCrypt compares with stored hash
       ↓
Hash("MyPassword123" + salt) == stored hash?
       ↓
If YES → Password correct
If NO  → Password incorrect
```

### 2. Token Security

**Token Signature Verification**
```
Server creates token:
  Payload: { "sub": "user@example.com", "iat": 1234567890, "exp": 1234568790 }
  Secret: "my-secret-key-only-server-knows"
  Signature: HMACSHA256(payload, secret)

Attacker tries to modify payload:
  Original sub: "user@example.com"
  Modified sub: "hacker@example.com"
  
Server receives and verifies:
  Recalculate: HMACSHA256(modified_payload, secret)
  Compare with received signature
  MISMATCH! ✓ Token rejected
  
Why? Without the secret key, attacker cannot recalculate the correct signature
```

**Token Expiration**
```
Token created at: 2024-01-15 10:00:00 AM
Expiration set to: 2024-01-15 10:15:00 AM (15 minutes)

At 10:14:00 AM:
  Current time < expiration → ✓ Valid

At 10:15:01 AM:
  Current time > expiration → ✗ Invalid

This forces users to refresh tokens periodically
```

### 3. CSRF Protection (Disabled for JWT)

Why CSRF protection is disabled:
- CSRF attacks work with session-based authentication
- Attacker tricks user to click malicious link
- Browser automatically sends session cookie
- Action happens without user consent

JWT is safe because:
- Token is stored in memory or local storage
- Not sent automatically like cookies
- Must be explicitly included in headers
- Attacker cannot access token from different domain

### 4. Stateless Authentication

**Traditional Session-Based (Vulnerable to some attacks)**
```
Server stores all active sessions:
  Session1: {userId: 123, email: "user1@example.com", expiresAt: 2024-01-15 11:00}
  Session2: {userId: 456, email: "user2@example.com", expiresAt: 2024-01-15 12:00}
  Session3: {userId: 789, email: "user3@example.com", expiresAt: 2024-01-15 13:00}

Problems:
- Server must maintain all sessions in memory
- Scales poorly with many users
- Server down = all sessions lost
- Sessions need to be shared across multiple servers
```

**JWT Stateless (Scalable)**
```
Server doesn't store anything for the user

User's request contains all information:
  Authorization: Bearer [JWT_WITH_USER_INFO]

Server only verifies:
  - Token signature valid?
  - Token not expired?
  
Benefits:
- No session storage needed
- Works across multiple servers
- Each server can verify independently
- Scales to millions of users
```

### 5. Authorization

In your system:
```java
// All authenticated users can access protected endpoints
// User role is stored in JWT token

@GetMapping("/api/protected")
public ResponseEntity<String> protectedEndpoint() {
    String userEmail = SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
    return ResponseEntity.ok("Hello, " + userEmail);
}
```

Future enhancement with roles:
```java
@GetMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")  // Only ADMIN role can access
public ResponseEntity<String> adminEndpoint() {
    return ResponseEntity.ok("Admin area");
}
```

### 6. Common Security Threats & Protection

| Threat | How it happens | Protection in your system |
|--------|---------------|----|
| Brute Force | Attacker tries many password combinations | BCrypt is slow, multiple wrong attempts could lock account |
| Man-in-the-Middle | Attacker intercepts communication | Use HTTPS/TLS encryption (not HTTP) |
| Replay Attack | Attacker reuses old valid token | Token expiration (15 min) makes old tokens invalid |
| Token Tampering | Attacker modifies token payload | Digital signature verification fails |
| Session Hijacking | Attacker steals session cookie | JWT stored in memory, not in cookies |
| CSRF | Attacker tricks user to perform action | Stateless JWT not vulnerable to CSRF |
| SQL Injection | Attacker injects SQL code | Use JpaRepository (parameterized queries) |

---

## API Endpoints Reference

### 1. Register New User
```
POST /api/auth/register
Content-Type: application/json

Request:
{
  "email": "user@example.com",
  "password": "Password123"
}

Response: 201 Created
{
  "success": true,
  "message": "User registered successfully",
  "data": "User account created. You can now login."
}

Errors:
- 400 Bad Request: Email already exists
- 400 Bad Request: Invalid email format
- 400 Bad Request: Password too short
```

### 2. Login User
```
POST /api/auth/login
Content-Type: application/json

Request:
{
  "email": "user@example.com",
  "password": "Password123"
}

Response: 200 OK
{
  "success": true,
  "message": "Login successful",
  "data": {
    "userId": "...",
    "email": "user@example.com",
    "role": "CUSTOMER",
    "accessToken": "eyJh...",
    "refreshToken": "eyJh...",
    "expiresIn": 900000
  }
}

Errors:
- 404 Not Found: User doesn't exist
- 400 Bad Request: Wrong password
- 400 Bad Request: User account inactive
```

### 3. Refresh Access Token
```
POST /api/auth/refresh
Authorization: Bearer [REFRESH_TOKEN]

Response: 200 OK
{
  "success": true,
  "message": "Token refreshed successfully",
  "data": {
    "userId": "...",
    "email": "user@example.com",
    "role": "CUSTOMER",
    "accessToken": "[NEW_ACCESS_TOKEN]",
    "refreshToken": "[SAME_REFRESH_TOKEN]",
    "expiresIn": 900000
  }
}

Errors:
- 400 Bad Request: Missing Authorization header
- 400 Bad Request: Invalid refresh token
- 404 Not Found: User not found
```

### 4. Validate Token
```
GET /api/auth/validate
Authorization: Bearer [ACCESS_TOKEN]

Response: 200 OK
{
  "success": true,
  "message": "Token is valid",
  "data": true
}

OR

{
  "success": false,
  "message": "Token is invalid",
  "data": false
}

Errors:
- 400 Bad Request: Missing Authorization header
```

---

## Client Implementation Example

### JavaScript (Frontend)

```javascript
// 1. Register
async function register(email, password) {
  const response = await fetch('http://localhost:8080/api/auth/register', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password })
  });
  return response.json();
}

// 2. Login
async function login(email, password) {
  const response = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password })
  });
  
  const data = await response.json();
  
  // Store tokens
  localStorage.setItem('accessToken', data.data.accessToken);
  localStorage.setItem('refreshToken', data.data.refreshToken);
  localStorage.setItem('tokenExpiry', Date.now() + data.data.expiresIn);
  
  return data;
}

// 3. Call Protected API
async function callProtectedAPI() {
  const accessToken = localStorage.getItem('accessToken');
  
  const response = await fetch('http://localhost:8080/api/protected-endpoint', {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${accessToken}`
    }
  });
  
  if (response.status === 401) {
    // Token expired, refresh it
    await refreshAccessToken();
    // Retry the request
    return callProtectedAPI();
  }
  
  return response.json();
}

// 4. Refresh Token
async function refreshAccessToken() {
  const refreshToken = localStorage.getItem('refreshToken');
  
  const response = await fetch('http://localhost:8080/api/auth/refresh', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${refreshToken}`
    }
  });
  
  const data = await response.json();
  
  // Update tokens
  localStorage.setItem('accessToken', data.data.accessToken);
  localStorage.setItem('tokenExpiry', Date.now() + data.data.expiresIn);
  
  return data;
}

// 5. Logout
function logout() {
  localStorage.removeItem('accessToken');
  localStorage.removeItem('refreshToken');
  localStorage.removeItem('tokenExpiry');
  window.location.href = '/login';
}
```

---

## Troubleshooting Guide

### Problem: "Token is invalid"
**Causes:**
- Token signature doesn't match (secret key mismatch)
- Token has been tampered with
- Token format is incorrect

**Solution:**
- Ensure Authorization header format is: `Bearer [TOKEN]`
- Check that secret key is same on server
- Re-login to get a new token

### Problem: "Token is expired"
**Causes:**
- Access token older than 15 minutes
- Refresh token older than 7 days

**Solution:**
- Use refresh token to get new access token
- If refresh token expired, user must login again

### Problem: "User not found"
**Causes:**
- User never registered
- User was deleted from database
- Email typo in request

**Solution:**
- Register the user first
- Check email spelling

### Problem: "User already exists"
**Causes:**
- Email already registered
- Trying to register same email twice

**Solution:**
- Use different email for new account
- If forgot password, implement "forgot password" feature

### Problem: "401 Unauthorized" on protected endpoint
**Causes:**
- No Authorization header sent
- Authorization header format wrong
- Token is invalid or expired
- Bearer token not included

**Solution:**
- Include Authorization header: `Authorization: Bearer [TOKEN]`
- Get new token by logging in or refreshing

---

## Key Takeaways

1. **JWT is three-part token**: Header (algo), Payload (claims), Signature (verification)
2. **Signature cannot be forged**: Only server with secret key can create valid signatures
3. **Stateless authentication**: Server doesn't store sessions, verifies using signature
4. **Passwords are hashed**: Using BCrypt, cannot be reversed
5. **Tokens expire**: Access tokens are short-lived (15 min), refresh tokens are long-lived (7 days)
6. **Spring Security intercepts requests**: Uses filter chain to validate tokens before reaching controller
7. **Authorization vs Authentication**: Authentication = who you are, Authorization = what you can access
8. **HTTPS is essential**: Always use HTTPS in production to encrypt tokens in transit

---

## Additional Resources

- **JWT.io**: https://jwt.io - Visual JWT decoder and introduction
- **Spring Security Docs**: https://spring.io/projects/spring-security
- **OWASP Authentication**: https://owasp.org/www-community/attacks/Session_fixation
- **Bcrypt**: https://en.wikipedia.org/wiki/Bcrypt
