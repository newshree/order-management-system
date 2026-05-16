package com.ecom.payment.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.payment.dto.request.InitiatePaymentRequest;
import com.ecom.payment.dto.request.RefundPaymentRequest;
import com.ecom.payment.dto.response.PaymentResponse;
import com.ecom.payment.dto.response.PaymentTransactionResponse;
import com.ecom.payment.dto.response.RefundResponse;
import com.ecom.payment.entity.Payment;
import com.ecom.payment.entity.PaymentTransaction;
import com.ecom.payment.enums.ErrorCode;
import com.ecom.payment.enums.PaymentStatus;
import com.ecom.payment.enums.PaymentTransactionType;
import com.ecom.payment.exception.BadRequestException;
import com.ecom.payment.exception.ResourceNotFoundException;
import com.ecom.payment.mapper.PaymentMapper;
import com.ecom.payment.repository.PaymentRepository;
import com.ecom.payment.repository.PaymentTransactionRepository;
import com.ecom.payment.service.PaymentService;

/**
 * PaymentServiceImpl - Implementation of PaymentService interface.
 *
 * Handles all payment processing logic including:
 * - Payment initiation with simulation
 * - Status updates
 * - Refund processing
 * - Transaction audit trail
 *
 * Design Pattern:
 * - Service Pattern: Business logic isolation
 * - Transactional Pattern: ACID compliance for critical operations
 * - Mapper Pattern: DTO conversion
 */
@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private static final String TRANSACTION_ID_PREFIX = "TXN";
    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentMapper paymentMapper;
    private final Random random;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            PaymentTransactionRepository paymentTransactionRepository,
            PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.paymentMapper = paymentMapper;
        this.random = new Random();
    }

    @Override
    @Transactional
    public PaymentResponse initiatePayment(InitiatePaymentRequest request) {
        log.info("Initiating payment for orderId: {}, userId: {}, amount: {}",
                request.getOrderId(), request.getUserId(), request.getAmount());

        validatePaymentRequest(request);

        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .status(PaymentStatus.PENDING)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        String transactionId = generateTransactionId();
        savedPayment.setTransactionId(transactionId);

        boolean paymentSuccess = simulatePaymentProcessing();
        PaymentStatus finalStatus = paymentSuccess ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
        savedPayment.setStatus(finalStatus);

        savedPayment = paymentRepository.save(savedPayment);

        createPaymentTransaction(savedPayment, PaymentTransactionType.PAYMENT, finalStatus);

        log.info("Payment {} processed with status: {}", savedPayment.getId(), finalStatus);

        return paymentMapper.paymentToPaymentResponse(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(UUID paymentId) {
        log.info("Fetching payment: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> {
                    log.warn("Payment not found: {}", paymentId);
                    return new ResourceNotFoundException(
                            "Payment not found with ID: " + paymentId,
                            ErrorCode.PAYMENT_NOT_FOUND);
                });

        return paymentMapper.paymentToPaymentResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByOrderId(UUID orderId) {
        log.info("Fetching payments for orderId: {}", orderId);

        List<Payment> payments = paymentRepository.findByOrderId(orderId);

        if (payments.isEmpty()) {
            log.warn("No payments found for orderId: {}", orderId);
            throw new ResourceNotFoundException(
                    "No payments found for order: " + orderId,
                    ErrorCode.PAYMENT_NOT_FOUND);
        }

        return payments.stream()
                .map(paymentMapper::paymentToPaymentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByUserId(UUID userId) {
        log.info("Fetching payments for userId: {}", userId);

        List<Payment> payments = paymentRepository.findByUserId(userId);

        if (payments.isEmpty()) {
            log.warn("No payments found for userId: {}", userId);
            throw new ResourceNotFoundException(
                    "No payments found for user: " + userId,
                    ErrorCode.PAYMENT_NOT_FOUND);
        }

        return payments.stream()
                .map(paymentMapper::paymentToPaymentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RefundResponse refundPayment(RefundPaymentRequest request) {
        log.info("Processing refund for paymentId: {}", request.getPaymentId());

        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> {
                    log.warn("Payment not found: {}", request.getPaymentId());
                    return new ResourceNotFoundException(
                            "Payment not found with ID: " + request.getPaymentId(),
                            ErrorCode.PAYMENT_NOT_FOUND);
                });

        validateRefundEligibility(payment);

        payment.setStatus(PaymentStatus.REFUNDED);
        Payment refundedPayment = paymentRepository.save(payment);

        createPaymentTransaction(refundedPayment, PaymentTransactionType.REFUND, PaymentStatus.SUCCESS);

        log.info("Refund processed successfully for payment: {}", payment.getId());

        return RefundResponse.builder()
                .refundId(UUID.randomUUID())
                .paymentId(payment.getId())
                .refundAmount(payment.getAmount())
                .status(PaymentStatus.REFUNDED)
                .reason(request.getReason())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentTransactionResponse> getPaymentTransactionHistory(UUID paymentId) {
        log.info("Fetching transaction history for paymentId: {}", paymentId);

        if (!paymentRepository.existsById(paymentId)) {
            log.warn("Payment not found: {}", paymentId);
            throw new ResourceNotFoundException(
                    "Payment not found with ID: " + paymentId,
                    ErrorCode.PAYMENT_NOT_FOUND);
        }

        List<PaymentTransaction> transactions = paymentTransactionRepository.findByPaymentId(paymentId);

        return transactions.stream()
                .map(paymentMapper::paymentTransactionToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean healthCheck() {
        log.debug("Health check called");
        return true;
    }

    private void validatePaymentRequest(InitiatePaymentRequest request) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Invalid payment amount: {}", request.getAmount());
            throw new BadRequestException(
                    "Payment amount must be greater than 0",
                    ErrorCode.PAYMENT_INVALID_AMOUNT);
        }

        if (request.getPaymentMethod() == null) {
            log.warn("Invalid payment method");
            throw new BadRequestException(
                    "Payment method is required",
                    ErrorCode.INVALID_PAYMENT_METHOD);
        }

        if (request.getOrderId() == null || request.getUserId() == null) {
            log.warn("Missing required payment details");
            throw new BadRequestException(
                    "Order ID and User ID are required",
                    ErrorCode.INVALID_REQUEST);
        }
    }

    private void validateRefundEligibility(Payment payment) {
        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            log.warn("Cannot refund payment with status: {}", payment.getStatus());
            throw new BadRequestException(
                    "Only successful payments can be refunded",
                    ErrorCode.REFUND_NOT_ELIGIBLE);
        }
    }

    private String generateTransactionId() {
        long timestamp = System.currentTimeMillis();
        return TRANSACTION_ID_PREFIX + timestamp;
    }

    private boolean simulatePaymentProcessing() {
        return random.nextBoolean();
    }

    private void createPaymentTransaction(
            Payment payment,
            PaymentTransactionType transactionType,
            PaymentStatus status) {
        PaymentTransaction transaction = PaymentTransaction.builder()
                .payment(payment)
                .transactionType(transactionType)
                .status(status)
                .amount(payment.getAmount())
                .build();

        paymentTransactionRepository.save(transaction);
        log.debug("Payment transaction created: type={}, status={}", transactionType, status);
    }
}
