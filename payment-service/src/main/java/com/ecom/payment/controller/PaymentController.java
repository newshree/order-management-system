package com.ecom.payment.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.payment.dto.request.InitiatePaymentRequest;
import com.ecom.payment.dto.request.RefundPaymentRequest;
import com.ecom.payment.dto.response.ApiResponse;
import com.ecom.payment.dto.response.PaymentResponse;
import com.ecom.payment.dto.response.PaymentTransactionResponse;
import com.ecom.payment.dto.response.RefundResponse;
import com.ecom.payment.service.PaymentService;

import jakarta.validation.Valid;

/**
 * PaymentController - REST API endpoints for payment operations.
 *
 * Base Path: /payments
 *
 * Endpoints:
 * - POST /payments : Initiate payment
 * - GET /payments/{paymentId} : Get payment by ID
 * - GET /payments/order/{orderId} : Get payments by order
 * - GET /payments/user/{userId} : Get payments by user
 * - POST /payments/refund : Refund payment
 * - GET /payments/{paymentId}/transactions : Get transaction history
 * - GET /payments/health : Health check
 *
 * Design Pattern:
 * - REST Controller Pattern: Exposes services via HTTP
 * - Layered Architecture: Controllers delegate to services
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    
    @Autowired
    private PaymentService paymentService;


    /**
     * API 1: Initiate Payment
     * Endpoint: POST /payments
     * Purpose: Creates and processes a payment for an order.
     *
     * Request Body:
     * {
     *   "orderId": "uuid",
     *   "userId": "uuid",
     *   "amount": 1500,
     *   "paymentMethod": "UPI"
     * }
     *
     * Response: 201 Created
     * {
     *   "success": true,
     *   "data": {
     *     "paymentId": "uuid",
     *     "orderId": "uuid",
     *     "userId": "uuid",
     *     "amount": 1500,
     *     "paymentMethod": "UPI",
     *     "status": "SUCCESS",
     *     "transactionId": "TXN12345",
     *     "createdAt": "2026-05-16T10:30:00",
     *     "updatedAt": "2026-05-16T10:30:00"
     *   },
     *   "message": "Payment processed successfully"
     * }
     *
     * @param request InitiatePaymentRequest with order and payment details
     * @return ResponseEntity with ApiResponse containing PaymentResponse
     */
    @PostMapping("/initiatePayment")
    public ResponseEntity<ApiResponse<PaymentResponse>> initiatePayment(
            @Valid @RequestBody InitiatePaymentRequest request) {
        log.info("Payment initiation request received for orderId: {}", request.getOrderId());

        PaymentResponse response = paymentService.initiatePayment(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<PaymentResponse>builder()
                        .success(true)
                        .data(response)
                        .message("Payment processed successfully")
                        .build());
    }

    /**
     * API 2: Get Payment By ID
     * Endpoint: GET /payments/{paymentId}
     * Purpose: Fetches payment details by payment ID.
     *
     * Response: 200 OK
     * {
     *   "success": true,
     *   "data": { ... PaymentResponse ... }
     * }
     *
     * @param paymentId the payment identifier (UUID)
     * @return ResponseEntity with ApiResponse containing PaymentResponse
     */
    @GetMapping("/getPaymentById/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(
            @PathVariable UUID paymentId) {
        log.info("Fetching payment: {}", paymentId);

        PaymentResponse response = paymentService.getPaymentById(paymentId);

        return ResponseEntity.ok(ApiResponse.<PaymentResponse>builder()
                .success(true)
                .data(response)
                .message("Payment fetched successfully")
                .build());
    }

    /**
     * API 3: Get Payments By Order ID
     * Endpoint: GET /payments/order/{orderId}
     * Purpose: Retrieves payment history for an order.
     *
     * Response: 200 OK
     * {
     *   "success": true,
     *   "data": [ ... list of PaymentResponse ... ]
     * }
     *
     * @param orderId the order identifier (UUID)
     * @return ResponseEntity with ApiResponse containing list of PaymentResponse
     */
    @GetMapping("/getPaymentsByOrderId/{orderId}")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByOrderId(
            @PathVariable UUID orderId) {
        log.info("Fetching payments for orderId: {}", orderId);

        List<PaymentResponse> responses = paymentService.getPaymentsByOrderId(orderId);

        return ResponseEntity.ok(ApiResponse.<List<PaymentResponse>>builder()
                .success(true)
                .data(responses)
                .message("Payments fetched successfully")
                .build());
    }

    /**
     * API 3.1: Get Payments By User ID
     * Endpoint: GET /payments/user/{userId}
     * Purpose: Retrieves all payments for a user.
     *
     * Response: 200 OK
     * {
     *   "success": true,
     *   "data": [ ... list of PaymentResponse ... ]
     * }
     *
     * @param userId the user identifier (UUID)
     * @return ResponseEntity with ApiResponse containing list of PaymentResponse
     */
    @GetMapping("/getPaymentsByUserId/{userId}")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByUserId(
            @PathVariable UUID userId) {
        log.info("Fetching payments for userId: {}", userId);

        List<PaymentResponse> responses = paymentService.getPaymentsByUserId(userId);

        return ResponseEntity.ok(ApiResponse.<List<PaymentResponse>>builder()
                .success(true)
                .data(responses)
                .message("Payments fetched successfully")
                .build());
    }

    /**
     * API 5: Refund Payment
     * Endpoint: POST /payments/refund
     * Purpose: Initiates refund for a successful payment.
     *
     * Request Body:
     * {
     *   "paymentId": "uuid",
     *   "reason": "ORDER_CANCELLED"
     * }
     *
     * Response: 200 OK
     * {
     *   "success": true,
     *   "data": {
     *     "refundId": "uuid",
     *     "paymentId": "uuid",
     *     "refundAmount": 1500,
     *     "status": "REFUNDED",
     *     "reason": "ORDER_CANCELLED",
     *     "createdAt": "2026-05-16T10:30:00"
     *   },
     *   "message": "Refund processed successfully"
     * }
     *
     * @param request RefundPaymentRequest with payment ID and reason
     * @return ResponseEntity with ApiResponse containing RefundResponse
     */
    @PostMapping("/refundPayment")
    public ResponseEntity<ApiResponse<RefundResponse>> refundPayment(
            @Valid @RequestBody RefundPaymentRequest request) {
        log.info("Refund request received for paymentId: {}", request.getPaymentId());

        RefundResponse response = paymentService.refundPayment(request);

        return ResponseEntity.ok(ApiResponse.<RefundResponse>builder()
                .success(true)
                .data(response)
                .message("Refund processed successfully")
                .build());
    }

    /**
     * Get Payment Transaction History
     * Endpoint: GET /payments/{paymentId}/transactions
     * Purpose: Retrieves audit trail of all transactions for a payment.
     *
     * Response: 200 OK
     * {
     *   "success": true,
     *   "data": [ ... list of PaymentTransactionResponse ... ]
     * }
     *
     * @param paymentId the payment identifier (UUID)
     * @return ResponseEntity with ApiResponse containing list of PaymentTransactionResponse
     */
    @GetMapping("/getPaymentTransactionHistory/{paymentId}")
    public ResponseEntity<ApiResponse<List<PaymentTransactionResponse>>> getPaymentTransactionHistory(
            @PathVariable UUID paymentId) {
        log.info("Fetching transaction history for paymentId: {}", paymentId);

        List<PaymentTransactionResponse> responses = paymentService.getPaymentTransactionHistory(paymentId);

        return ResponseEntity.ok(ApiResponse.<List<PaymentTransactionResponse>>builder()
                .success(true)
                .data(responses)
                .message("Transaction history fetched successfully")
                .build());
    }

    /**
     * API 6: Payment Health Check
     * Endpoint: GET /payments/health
     * Purpose: Kubernetes/Docker health checks and monitoring.
     *
     * Response: 200 OK
     * {
     *   "success": true,
     *   "data": true
     * }
     *
     * @return ResponseEntity with ApiResponse containing health status
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Boolean>> healthCheck() {
        log.debug("Health check called");

        boolean isHealthy = paymentService.healthCheck();

        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .success(true)
                .data(isHealthy)
                .message("Payment service is healthy")
                .build());
    }
}
