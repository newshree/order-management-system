package com.ecom.cart_service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ecom.cart.controller.CartController;
import com.ecom.cart.dto.request.AddItemToCartRequest;
import com.ecom.cart.dto.request.UpdateCartItemQuantityRequest;
import com.ecom.cart.dto.response.ApiResponse;
import com.ecom.cart.dto.response.CartItemResponse;
import com.ecom.cart.dto.response.CartResponse;
import com.ecom.cart.dto.response.CartValidationResponse;
import com.ecom.cart.dto.response.CheckoutSummaryResponse;
import com.ecom.cart.enums.CartStatus;
import com.ecom.cart.enums.ErrorCode;
import com.ecom.cart.exception.BadRequestException;
import com.ecom.cart.exception.ResourceNotFoundException;
import com.ecom.cart.service.CartService;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartController Tests")
class CartControllerTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    private UUID userId;
    private UUID cartId;
    private UUID itemId;
    private UUID productId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        cartId = UUID.randomUUID();
        itemId = UUID.randomUUID();
        productId = UUID.randomUUID();
    }

    // ===== getCart() Tests =====
    @Test
    @DisplayName("Should retrieve cart successfully")
    void testGetCartSuccess() {
        CartResponse cartResponse = buildCartResponse();
        when(cartService.getCart(userId)).thenReturn(cartResponse);

        ResponseEntity<ApiResponse<CartResponse>> response = cartController.getCart(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Cart retrieved successfully", response.getBody().getMessage());
        assertEquals(cartId, response.getBody().getData().getCartId());
        verify(cartService, times(1)).getCart(userId);
    }

    @Test
    @DisplayName("Should return empty cart for new user")
    void testGetCartEmpty() {
        CartResponse emptyCart = CartResponse.builder()
            .cartId(cartId)
            .userId(userId)
            .status(CartStatus.ACTIVE)
            .totalAmount(BigDecimal.ZERO)
            .items(new ArrayList<>())
            .build();
        when(cartService.getCart(userId)).thenReturn(emptyCart);

        ResponseEntity<ApiResponse<CartResponse>> response = cartController.getCart(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getData().getItems().isEmpty());
        verify(cartService, times(1)).getCart(userId);
    }

    // ===== addItemToCart() Tests =====
    @Test
    @DisplayName("Should add item to cart successfully")
    void testAddItemToCartSuccess() {
        AddItemToCartRequest request = AddItemToCartRequest.builder()
            .productId(productId)
            .quantity(2)
            .build();
        CartResponse cartResponse = buildCartResponse();
        when(cartService.addItemToCart(userId, request)).thenReturn(cartResponse);

        ResponseEntity<ApiResponse<CartResponse>> response = cartController.addItemToCart(request, userId);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Item added to cart successfully", response.getBody().getMessage());
        verify(cartService, times(1)).addItemToCart(userId, request);
    }

    @Test
    @DisplayName("Should throw exception when product not found")
    void testAddItemToCartProductNotFound() {
        AddItemToCartRequest request = AddItemToCartRequest.builder()
            .productId(productId)
            .quantity(2)
            .build();
        when(cartService.addItemToCart(userId, request))
            .thenThrow(new ResourceNotFoundException(ErrorCode.PRODUCT_NOT_FOUND, "Product not found"));

        assertThrows(ResourceNotFoundException.class, () -> cartController.addItemToCart(request, userId));
        verify(cartService, times(1)).addItemToCart(userId, request);
    }

    @Test
    @DisplayName("Should throw exception when quantity is invalid")
    void testAddItemToCartInvalidQuantity() {
        AddItemToCartRequest request = AddItemToCartRequest.builder()
            .productId(productId)
            .quantity(-1)
            .build();
        when(cartService.addItemToCart(userId, request))
            .thenThrow(new BadRequestException(ErrorCode.INVALID_QUANTITY, "Quantity must be greater than 0"));

        assertThrows(BadRequestException.class, () -> cartController.addItemToCart(request, userId));
        verify(cartService, times(1)).addItemToCart(userId, request);
    }

    @Test
    @DisplayName("Should increase quantity if item already in cart")
    void testAddItemToCartExistingItem() {
        AddItemToCartRequest request = AddItemToCartRequest.builder()
            .productId(productId)
            .quantity(3)
            .build();
        CartResponse cartResponse = buildCartResponse();
        cartResponse.getItems().get(0).setQuantity(5);
        when(cartService.addItemToCart(userId, request)).thenReturn(cartResponse);

        ResponseEntity<ApiResponse<CartResponse>> response = cartController.addItemToCart(request, userId);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(5, response.getBody().getData().getItems().get(0).getQuantity());
        verify(cartService, times(1)).addItemToCart(userId, request);
    }

    // ===== updateItemQuantity() Tests =====
    @Test
    @DisplayName("Should update item quantity successfully")
    void testUpdateItemQuantitySuccess() {
        UpdateCartItemQuantityRequest request = UpdateCartItemQuantityRequest.builder()
            .quantity(5)
            .build();
        CartResponse cartResponse = buildCartResponse();
        cartResponse.getItems().get(0).setQuantity(5);
        when(cartService.updateItemQuantity(userId, itemId, request)).thenReturn(cartResponse);

        ResponseEntity<ApiResponse<CartResponse>> response = cartController.updateItemQuantity(itemId, request, userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Item quantity updated successfully", response.getBody().getMessage());
        verify(cartService, times(1)).updateItemQuantity(userId, itemId, request);
    }

    @Test
    @DisplayName("Should throw exception when item not found during update")
    void testUpdateItemQuantityNotFound() {
        UpdateCartItemQuantityRequest request = UpdateCartItemQuantityRequest.builder()
            .quantity(5)
            .build();
        when(cartService.updateItemQuantity(userId, itemId, request))
            .thenThrow(new ResourceNotFoundException(ErrorCode.CART_ITEM_NOT_FOUND, "Cart item not found"));

        assertThrows(ResourceNotFoundException.class, () -> cartController.updateItemQuantity(itemId, request, userId));
        verify(cartService, times(1)).updateItemQuantity(userId, itemId, request);
    }

    @Test
    @DisplayName("Should throw exception when quantity is zero during update")
    void testUpdateItemQuantityZero() {
        UpdateCartItemQuantityRequest request = UpdateCartItemQuantityRequest.builder()
            .quantity(0)
            .build();
        when(cartService.updateItemQuantity(userId, itemId, request))
            .thenThrow(new BadRequestException(ErrorCode.INVALID_QUANTITY, "Quantity must be greater than 0"));

        assertThrows(BadRequestException.class, () -> cartController.updateItemQuantity(itemId, request, userId));
        verify(cartService, times(1)).updateItemQuantity(userId, itemId, request);
    }

    @Test
    @DisplayName("Should throw exception when quantity is negative during update")
    void testUpdateItemQuantityNegative() {
        UpdateCartItemQuantityRequest request = UpdateCartItemQuantityRequest.builder()
            .quantity(-5)
            .build();
        when(cartService.updateItemQuantity(userId, itemId, request))
            .thenThrow(new BadRequestException(ErrorCode.INVALID_QUANTITY, "Quantity must be greater than 0"));

        assertThrows(BadRequestException.class, () -> cartController.updateItemQuantity(itemId, request, userId));
    }

    // ===== removeItemFromCart() Tests =====
    @Test
    @DisplayName("Should remove item from cart successfully")
    void testRemoveItemFromCartSuccess() {
        CartResponse cartResponse = buildCartResponse();
        cartResponse.setItems(new ArrayList<>());
        when(cartService.removeItemFromCart(userId, itemId)).thenReturn(cartResponse);

        ResponseEntity<ApiResponse<CartResponse>> response = cartController.removeItemFromCart(itemId, userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Item removed from cart successfully", response.getBody().getMessage());
        verify(cartService, times(1)).removeItemFromCart(userId, itemId);
    }

    @Test
    @DisplayName("Should throw exception when item not found during removal")
    void testRemoveItemFromCartNotFound() {
        when(cartService.removeItemFromCart(userId, itemId))
            .thenThrow(new ResourceNotFoundException(ErrorCode.CART_ITEM_NOT_FOUND, "Cart item not found"));

        assertThrows(ResourceNotFoundException.class, () -> cartController.removeItemFromCart(itemId, userId));
        verify(cartService, times(1)).removeItemFromCart(userId, itemId);
    }

    @Test
    @DisplayName("Should throw exception when cart not found during item removal")
    void testRemoveItemFromCartCartNotFound() {
        when(cartService.removeItemFromCart(userId, itemId))
            .thenThrow(new ResourceNotFoundException(ErrorCode.CART_NOT_FOUND, "Cart not found"));

        assertThrows(ResourceNotFoundException.class, () -> cartController.removeItemFromCart(itemId, userId));
        verify(cartService, times(1)).removeItemFromCart(userId, itemId);
    }

    // ===== clearCart() Tests =====
    @Test
    @DisplayName("Should clear cart successfully")
    void testClearCartSuccess() {
        CartResponse emptyCart = CartResponse.builder()
            .cartId(cartId)
            .userId(userId)
            .status(CartStatus.ACTIVE)
            .totalAmount(BigDecimal.ZERO)
            .items(new ArrayList<>())
            .build();
        when(cartService.clearCart(userId)).thenReturn(emptyCart);

        ResponseEntity<ApiResponse<CartResponse>> response = cartController.clearCart(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Cart cleared successfully", response.getBody().getMessage());
        assertTrue(response.getBody().getData().getItems().isEmpty());
        verify(cartService, times(1)).clearCart(userId);
    }

    @Test
    @DisplayName("Should throw exception when cart not found during clear")
    void testClearCartNotFound() {
        when(cartService.clearCart(userId))
            .thenThrow(new ResourceNotFoundException(ErrorCode.CART_NOT_FOUND, "Cart not found"));

        assertThrows(ResourceNotFoundException.class, () -> cartController.clearCart(userId));
        verify(cartService, times(1)).clearCart(userId);
    }

    // ===== validateCart() Tests =====
    @Test
    @DisplayName("Should validate cart successfully with no errors")
    void testValidateCartSuccess() {
        CartValidationResponse validationResponse = CartValidationResponse.builder()
            .isValid(true)
            .errorCount(0)
            .warningCount(0)
            .validationErrors(new ArrayList<>())
            .validationWarnings(new ArrayList<>())
            .build();
        when(cartService.validateCart(userId)).thenReturn(validationResponse);

        ResponseEntity<ApiResponse<CartValidationResponse>> response = cartController.validateCart(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertTrue(response.getBody().getData().getIsValid());
        assertEquals(0, response.getBody().getData().getErrorCount());
        verify(cartService, times(1)).validateCart(userId);
    }

    @Test
    @DisplayName("Should validate cart with errors")
    void testValidateCartWithErrors() {
        List<String> errors = List.of("Product no longer available", "Insufficient inventory");
        CartValidationResponse validationResponse = CartValidationResponse.builder()
            .isValid(false)
            .errorCount(2)
            .warningCount(0)
            .validationErrors(errors)
            .validationWarnings(new ArrayList<>())
            .build();
        when(cartService.validateCart(userId)).thenReturn(validationResponse);

        ResponseEntity<ApiResponse<CartValidationResponse>> response = cartController.validateCart(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().getData().getIsValid());
        assertEquals(2, response.getBody().getData().getErrorCount());
        verify(cartService, times(1)).validateCart(userId);
    }

    @Test
    @DisplayName("Should validate cart with warnings")
    void testValidateCartWithWarnings() {
        List<String> warnings = List.of("Price has increased");
        CartValidationResponse validationResponse = CartValidationResponse.builder()
            .isValid(true)
            .errorCount(0)
            .warningCount(1)
            .validationErrors(new ArrayList<>())
            .validationWarnings(warnings)
            .build();
        when(cartService.validateCart(userId)).thenReturn(validationResponse);

        ResponseEntity<ApiResponse<CartValidationResponse>> response = cartController.validateCart(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getData().getIsValid());
        assertEquals(1, response.getBody().getData().getWarningCount());
        verify(cartService, times(1)).validateCart(userId);
    }

    @Test
    @DisplayName("Should throw exception when cart not found during validation")
    void testValidateCartNotFound() {
        when(cartService.validateCart(userId))
            .thenThrow(new ResourceNotFoundException(ErrorCode.CART_NOT_FOUND, "Cart not found"));

        assertThrows(ResourceNotFoundException.class, () -> cartController.validateCart(userId));
        verify(cartService, times(1)).validateCart(userId);
    }

    // ===== getCheckoutSummary() Tests =====
    @Test
    @DisplayName("Should get checkout summary successfully")
    void testGetCheckoutSummarySuccess() {
        CheckoutSummaryResponse summaryResponse = buildCheckoutSummary();
        when(cartService.getCheckoutSummary(userId)).thenReturn(summaryResponse);

        ResponseEntity<ApiResponse<CheckoutSummaryResponse>> response = cartController.getCheckoutSummary(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Checkout summary retrieved successfully", response.getBody().getMessage());
        assertNotNull(response.getBody().getData().getFinalAmount());
        verify(cartService, times(1)).getCheckoutSummary(userId);
    }

    @Test
    @DisplayName("Should throw exception when cart is empty during checkout")
    void testGetCheckoutSummaryCartEmpty() {
        when(cartService.getCheckoutSummary(userId))
            .thenThrow(new BadRequestException(ErrorCode.CART_EMPTY, "Cannot checkout with empty cart"));

        assertThrows(BadRequestException.class, () -> cartController.getCheckoutSummary(userId));
        verify(cartService, times(1)).getCheckoutSummary(userId);
    }

    @Test
    @DisplayName("Should throw exception when cart not found during checkout")
    void testGetCheckoutSummaryCartNotFound() {
        when(cartService.getCheckoutSummary(userId))
            .thenThrow(new ResourceNotFoundException(ErrorCode.CART_NOT_FOUND, "Cart not found"));

        assertThrows(ResourceNotFoundException.class, () -> cartController.getCheckoutSummary(userId));
        verify(cartService, times(1)).getCheckoutSummary(userId);
    }

    @Test
    @DisplayName("Should verify checkout summary calculations")
    void testGetCheckoutSummaryCalculations() {
        CheckoutSummaryResponse summaryResponse = buildCheckoutSummary();
        when(cartService.getCheckoutSummary(userId)).thenReturn(summaryResponse);

        ResponseEntity<ApiResponse<CheckoutSummaryResponse>> response = cartController.getCheckoutSummary(userId);

        CheckoutSummaryResponse data = response.getBody().getData();
        assertEquals(new BigDecimal("2000.00"), data.getSubtotal());
        assertEquals(new BigDecimal("200.00"), data.getTax());
        assertEquals(new BigDecimal("50.00"), data.getDeliveryCharge());
        assertEquals(new BigDecimal("2250.00"), data.getFinalAmount());
    }

    // ===== syncCart() Tests =====
    @Test
    @DisplayName("Should sync cart successfully")
    void testSyncCartSuccess() {
        CartResponse cartResponse = buildCartResponse();
        when(cartService.syncCart(userId)).thenReturn(cartResponse);

        ResponseEntity<ApiResponse<CartResponse>> response = cartController.syncCart(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Cart synced successfully", response.getBody().getMessage());
        verify(cartService, times(1)).syncCart(userId);
    }

    @Test
    @DisplayName("Should sync cart with item removal")
    void testSyncCartWithItemRemoval() {
        CartResponse cartResponse = CartResponse.builder()
            .cartId(cartId)
            .userId(userId)
            .status(CartStatus.ACTIVE)
            .totalAmount(new BigDecimal("1000.00"))
            .items(new ArrayList<>())
            .build();
        when(cartService.syncCart(userId)).thenReturn(cartResponse);

        ResponseEntity<ApiResponse<CartResponse>> response = cartController.syncCart(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getData().getItems().isEmpty());
        verify(cartService, times(1)).syncCart(userId);
    }

    @Test
    @DisplayName("Should throw exception when cart not found during sync")
    void testSyncCartNotFound() {
        when(cartService.syncCart(userId))
            .thenThrow(new ResourceNotFoundException(ErrorCode.CART_NOT_FOUND, "Cart not found"));

        assertThrows(ResourceNotFoundException.class, () -> cartController.syncCart(userId));
        verify(cartService, times(1)).syncCart(userId);
    }

    // ===== getCartItem() Tests =====
    @Test
    @DisplayName("Should get cart item successfully")
    void testGetCartItemSuccess() {
        CartItemResponse itemResponse = buildCartItemResponse();
        when(cartService.getCartItem(userId, itemId)).thenReturn(itemResponse);

        ResponseEntity<ApiResponse<CartItemResponse>> response = cartController.getCartItem(itemId, userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Cart item retrieved successfully", response.getBody().getMessage());
        assertEquals(itemId, response.getBody().getData().getItemId());
        verify(cartService, times(1)).getCartItem(userId, itemId);
    }

    @Test
    @DisplayName("Should throw exception when cart item not found")
    void testGetCartItemNotFound() {
        when(cartService.getCartItem(userId, itemId))
            .thenThrow(new ResourceNotFoundException(ErrorCode.CART_ITEM_NOT_FOUND, "Cart item not found"));

        assertThrows(ResourceNotFoundException.class, () -> cartController.getCartItem(itemId, userId));
        verify(cartService, times(1)).getCartItem(userId, itemId);
    }

    @Test
    @DisplayName("Should throw exception when cart not found during item retrieval")
    void testGetCartItemCartNotFound() {
        when(cartService.getCartItem(userId, itemId))
            .thenThrow(new ResourceNotFoundException(ErrorCode.CART_NOT_FOUND, "Cart not found"));

        assertThrows(ResourceNotFoundException.class, () -> cartController.getCartItem(itemId, userId));
        verify(cartService, times(1)).getCartItem(userId, itemId);
    }

    // ===== Helper Methods =====
    private CartResponse buildCartResponse() {
        CartItemResponse item = buildCartItemResponse();
        return CartResponse.builder()
            .cartId(cartId)
            .userId(userId)
            .status(CartStatus.ACTIVE)
            .totalAmount(new BigDecimal("2000.00"))
            .items(List.of(item))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    private CartItemResponse buildCartItemResponse() {
        return CartItemResponse.builder()
            .itemId(itemId)
            .productId(productId)
            .productName("Test Product")
            .quantity(2)
            .price(new BigDecimal("1000.00"))
            .totalPrice(new BigDecimal("2000.00"))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    private CheckoutSummaryResponse buildCheckoutSummary() {
        CartItemResponse item = buildCartItemResponse();
        return CheckoutSummaryResponse.builder()
            .items(List.of(item))
            .subtotal(new BigDecimal("2000.00"))
            .tax(new BigDecimal("200.00"))
            .deliveryCharge(new BigDecimal("50.00"))
            .totalAmount(new BigDecimal("2200.00"))
            .finalAmount(new BigDecimal("2250.00"))
            .build();
    }
}
