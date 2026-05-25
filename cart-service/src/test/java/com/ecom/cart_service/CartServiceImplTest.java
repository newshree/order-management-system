package com.ecom.cart_service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ecom.cart.dto.request.AddItemToCartRequest;
import com.ecom.cart.dto.request.UpdateCartItemQuantityRequest;
import com.ecom.cart.dto.response.CartItemResponse;
import com.ecom.cart.dto.response.CartResponse;
import com.ecom.cart.dto.response.CartValidationResponse;
import com.ecom.cart.dto.response.CheckoutSummaryResponse;
import com.ecom.cart.entity.Cart;
import com.ecom.cart.entity.CartItem;
import com.ecom.cart.enums.CartStatus;
import com.ecom.cart.enums.ErrorCode;
import com.ecom.cart.exception.BadRequestException;
import com.ecom.cart.exception.ResourceNotFoundException;
import com.ecom.cart.mapper.CartMapper;
import com.ecom.cart.redis.RedisCart;
import com.ecom.cart.redis.RedisCartItem;
import com.ecom.cart.repository.CartItemRepository;
import com.ecom.cart.repository.CartRepository;
import com.ecom.cart.repository.RedisCartRepository;
import com.ecom.cart.service.impl.CartServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartServiceImpl Tests")
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private RedisCartRepository redisCartRepository;

    @Mock
    private CartMapper cartMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    private UUID userId;
    private UUID cartId;
    private UUID itemId;
    private UUID productId;
    private Cart cart;
    private CartItem cartItem;
    private CartResponse cartResponse;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        cartId = UUID.randomUUID();
        itemId = UUID.randomUUID();
        productId = UUID.randomUUID();

        cart = buildCart();
        cartItem = buildCartItem();
        cartResponse = buildCartResponse();
    }

    // ===== getCart() Tests =====
    @Test
    @DisplayName("Should retrieve cart from Redis successfully")
    void testGetCartFromRedisSuccess() {
        RedisCart redisCart = buildRedisCart();
        when(redisCartRepository.findById(userId.toString())).thenReturn(Optional.of(redisCart));

        CartResponse result = cartService.getCart(userId);

        assertNotNull(result);
        assertEquals(cartId, result.getCartId());
        verify(redisCartRepository, times(1)).findById(userId.toString());
        verify(cartRepository, never()).findByUserId(userId);
    }

    @Test
    @DisplayName("Should retrieve cart from database when not in Redis")
    void testGetCartFromDatabaseWhenNotInRedis() {
        when(redisCartRepository.findById(userId.toString())).thenReturn(Optional.empty());
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartMapper.toCartResponse(cart)).thenReturn(cartResponse);

        CartResponse result = cartService.getCart(userId);

        assertNotNull(result);
        assertEquals(cartId, result.getCartId());
        verify(redisCartRepository, times(1)).findById(userId.toString());
        verify(cartRepository, times(1)).findByUserId(userId);
        verify(redisCartRepository, times(1)).save(any(RedisCart.class));
    }

    @Test
    @DisplayName("Should create new cart if not found in Redis or database")
    void testGetCartCreateNewCartIfNotFound() {
        when(redisCartRepository.findById(userId.toString())).thenReturn(Optional.empty());
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartMapper.toCartResponse(cart)).thenReturn(cartResponse);

        CartResponse result = cartService.getCart(userId);

        assertNotNull(result);
        assertEquals(cartId, result.getCartId());
        verify(cartRepository, times(1)).save(any(Cart.class));
        verify(redisCartRepository, times(1)).save(any(RedisCart.class));
    }

    // ===== addItemToCart() Tests =====
    @Test
    @DisplayName("Should add new item to cart successfully")
    void testAddItemToCartSuccess() {
        AddItemToCartRequest request = AddItemToCartRequest.builder()
            .productId(productId)
            .quantity(2)
            .build();

        CartItem savedItem = CartItem.builder()
            .id(itemId)
            .cart(cart)
            .productId(productId)
            .productName("Product " + productId)
            .quantity(2)
            .price(new BigDecimal("100.00"))
            .totalPrice(new BigDecimal("200.00"))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        Cart cartAfterSave = Cart.builder()
            .id(cartId)
            .userId(userId)
            .status(CartStatus.ACTIVE)
            .totalAmount(new BigDecimal("200.00"))
            .items(new ArrayList<>(List.of(savedItem)))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        when(cartRepository.findByUserId(userId))
            .thenReturn(Optional.of(cart))
            .thenReturn(Optional.of(cartAfterSave));
        when(cartItemRepository.findByCartIdAndProductId(cartId, productId)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(savedItem);
        when(cartRepository.save(any(Cart.class))).thenReturn(cartAfterSave);
        when(cartMapper.toCartResponse(cartAfterSave)).thenReturn(cartResponse);

        CartResponse result = cartService.addItemToCart(userId, request);

        assertNotNull(result);
        assertEquals(cartId, result.getCartId());
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    @DisplayName("Should increase quantity when item already exists in cart")
    void testAddItemToCartExistingItem() {
        AddItemToCartRequest request = AddItemToCartRequest.builder()
            .productId(productId)
            .quantity(3)
            .build();

        CartItem existingItem = CartItem.builder()
            .id(itemId)
            .cart(cart)
            .productId(productId)
            .productName("Test Product")
            .quantity(2)
            .price(new BigDecimal("100.00"))
            .totalPrice(new BigDecimal("200.00"))
            .build();

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndProductId(cartId, productId)).thenReturn(Optional.of(existingItem));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(existingItem);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartMapper.toCartResponse(cart)).thenReturn(cartResponse);

        CartResponse result = cartService.addItemToCart(userId, request);

        assertNotNull(result);
        assertEquals(5, existingItem.getQuantity());
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Should throw exception when quantity is invalid")
    void testAddItemToCartInvalidQuantity() {
        AddItemToCartRequest request = AddItemToCartRequest.builder()
            .productId(productId)
            .quantity(-1)
            .build();

        BadRequestException exception = assertThrows(BadRequestException.class,
            () -> cartService.addItemToCart(userId, request));

        assertEquals("Quantity must be greater than 0", exception.getMessage());
        assertEquals(ErrorCode.INVALID_QUANTITY, exception.getErrorCode());
    }

    @Test
    @DisplayName("Should throw exception when product ID is null")
    void testAddItemToCartNullProductId() {
        AddItemToCartRequest request = AddItemToCartRequest.builder()
            .productId(null)
            .quantity(2)
            .build();

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> cartService.addItemToCart(userId, request));

        assertEquals("Product not found with id: null", exception.getMessage());
        assertEquals(ErrorCode.PRODUCT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("Should create new cart if it doesn't exist when adding item")
    void testAddItemToCartCreateNewCart() {
        AddItemToCartRequest request = AddItemToCartRequest.builder()
            .productId(productId)
            .quantity(2)
            .build();

        CartItem savedItem = CartItem.builder()
            .id(itemId)
            .cart(cart)
            .productId(productId)
            .productName("Product " + productId)
            .quantity(2)
            .price(new BigDecimal("100.00"))
            .totalPrice(new BigDecimal("200.00"))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        Cart newCart = Cart.builder()
            .id(cartId)
            .userId(userId)
            .status(CartStatus.ACTIVE)
            .totalAmount(BigDecimal.ZERO)
            .items(new ArrayList<>())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        Cart cartAfterSave = Cart.builder()
            .id(cartId)
            .userId(userId)
            .status(CartStatus.ACTIVE)
            .totalAmount(new BigDecimal("200.00"))
            .items(new ArrayList<>(List.of(savedItem)))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        when(cartRepository.findByUserId(userId))
            .thenReturn(Optional.empty())
            .thenReturn(Optional.of(cartAfterSave));
        when(cartRepository.save(any(Cart.class)))
            .thenReturn(newCart)
            .thenReturn(cartAfterSave);
        when(cartItemRepository.findByCartIdAndProductId(cartId, productId)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(savedItem);
        when(cartMapper.toCartResponse(cartAfterSave)).thenReturn(cartResponse);

        CartResponse result = cartService.addItemToCart(userId, request);

        assertNotNull(result);
        verify(cartRepository, atLeast(1)).save(any(Cart.class));
    }

    // ===== updateItemQuantity() Tests =====
    @Test
    @DisplayName("Should update item quantity successfully")
    void testUpdateItemQuantitySuccess() {
        UpdateCartItemQuantityRequest request = UpdateCartItemQuantityRequest.builder()
            .quantity(5)
            .build();

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(itemId)).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartMapper.toCartResponse(cart)).thenReturn(cartResponse);

        CartResponse result = cartService.updateItemQuantity(userId, itemId, request);

        assertNotNull(result);
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Should throw exception when cart not found during update")
    void testUpdateItemQuantityCartNotFound() {
        UpdateCartItemQuantityRequest request = UpdateCartItemQuantityRequest.builder()
            .quantity(5)
            .build();

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> cartService.updateItemQuantity(userId, itemId, request));

        assertEquals("Cart not found for user: " + userId, exception.getMessage());
        assertEquals(ErrorCode.CART_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("Should throw exception when item not found during update")
    void testUpdateItemQuantityItemNotFound() {
        UpdateCartItemQuantityRequest request = UpdateCartItemQuantityRequest.builder()
            .quantity(5)
            .build();

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(itemId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> cartService.updateItemQuantity(userId, itemId, request));

        assertEquals("Cart item not found with id: " + itemId, exception.getMessage());
        assertEquals(ErrorCode.CART_ITEM_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("Should throw exception when quantity is invalid during update")
    void testUpdateItemQuantityInvalidQuantity() {
        UpdateCartItemQuantityRequest request = UpdateCartItemQuantityRequest.builder()
            .quantity(0)
            .build();

        BadRequestException exception = assertThrows(BadRequestException.class,
            () -> cartService.updateItemQuantity(userId, itemId, request));

        assertEquals("Quantity must be greater than 0", exception.getMessage());
        assertEquals(ErrorCode.INVALID_QUANTITY, exception.getErrorCode());
    }

    @Test
    @DisplayName("Should throw exception when item doesn't belong to cart")
    void testUpdateItemQuantityItemNotInCart() {
        UpdateCartItemQuantityRequest request = UpdateCartItemQuantityRequest.builder()
            .quantity(5)
            .build();

        Cart differentCart = Cart.builder()
            .id(UUID.randomUUID())
            .userId(userId)
            .status(CartStatus.ACTIVE)
            .totalAmount(BigDecimal.ZERO)
            .items(new ArrayList<>())
            .build();

        CartItem itemFromDifferentCart = CartItem.builder()
            .id(itemId)
            .cart(differentCart)
            .productId(productId)
            .productName("Test Product")
            .quantity(2)
            .price(new BigDecimal("100.00"))
            .totalPrice(new BigDecimal("200.00"))
            .build();

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(itemId)).thenReturn(Optional.of(itemFromDifferentCart));

        BadRequestException exception = assertThrows(BadRequestException.class,
            () -> cartService.updateItemQuantity(userId, itemId, request));

        assertEquals("Cart item does not belong to this cart", exception.getMessage());
        assertEquals(ErrorCode.INVALID_PARAMETER, exception.getErrorCode());
    }

    // ===== removeItemFromCart() Tests =====
    @Test
    @DisplayName("Should remove item from cart successfully")
    void testRemoveItemFromCartSuccess() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(itemId)).thenReturn(Optional.of(cartItem));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartMapper.toCartResponse(cart)).thenReturn(cartResponse);

        CartResponse result = cartService.removeItemFromCart(userId, itemId);

        assertNotNull(result);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    @DisplayName("Should throw exception when cart not found during removal")
    void testRemoveItemFromCartCartNotFound() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> cartService.removeItemFromCart(userId, itemId));

        assertEquals("Cart not found for user: " + userId, exception.getMessage());
        assertEquals(ErrorCode.CART_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("Should throw exception when item not found during removal")
    void testRemoveItemFromCartItemNotFound() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(itemId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> cartService.removeItemFromCart(userId, itemId));

        assertEquals("Cart item not found with id: " + itemId, exception.getMessage());
        assertEquals(ErrorCode.CART_ITEM_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("Should throw exception when item doesn't belong to cart during removal")
    void testRemoveItemFromCartItemNotInCart() {
        Cart differentCart = Cart.builder()
            .id(UUID.randomUUID())
            .userId(userId)
            .status(CartStatus.ACTIVE)
            .totalAmount(BigDecimal.ZERO)
            .items(new ArrayList<>())
            .build();

        CartItem itemFromDifferentCart = CartItem.builder()
            .id(itemId)
            .cart(differentCart)
            .productId(productId)
            .productName("Test Product")
            .quantity(2)
            .price(new BigDecimal("100.00"))
            .totalPrice(new BigDecimal("200.00"))
            .build();

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(itemId)).thenReturn(Optional.of(itemFromDifferentCart));

        BadRequestException exception = assertThrows(BadRequestException.class,
            () -> cartService.removeItemFromCart(userId, itemId));

        assertEquals("Cart item does not belong to this cart", exception.getMessage());
        assertEquals(ErrorCode.INVALID_PARAMETER, exception.getErrorCode());
    }

    // ===== clearCart() Tests =====
    @Test
    @DisplayName("Should clear cart successfully")
    void testClearCartSuccess() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartMapper.toCartResponse(cart)).thenReturn(cartResponse);

        CartResponse result = cartService.clearCart(userId);

        assertNotNull(result);
        assertTrue(cart.getItems().isEmpty());
        assertEquals(BigDecimal.ZERO, cart.getTotalAmount());
        verify(cartRepository, times(1)).save(any(Cart.class));
        verify(redisCartRepository, times(1)).deleteById(userId.toString());
    }

    @Test
    @DisplayName("Should throw exception when cart not found during clear")
    void testClearCartNotFound() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> cartService.clearCart(userId));

        assertEquals("Cart not found for user: " + userId, exception.getMessage());
        assertEquals(ErrorCode.CART_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("Should clear empty cart successfully")
    void testClearEmptyCart() {
        Cart emptyCart = Cart.builder()
            .id(cartId)
            .userId(userId)
            .status(CartStatus.ACTIVE)
            .totalAmount(BigDecimal.ZERO)
            .items(new ArrayList<>())
            .build();

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(emptyCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(emptyCart);
        CartResponse emptyResponse = CartResponse.builder()
            .cartId(cartId)
            .userId(userId)
            .status(CartStatus.ACTIVE)
            .totalAmount(BigDecimal.ZERO)
            .items(new ArrayList<>())
            .build();
        when(cartMapper.toCartResponse(emptyCart)).thenReturn(emptyResponse);

        CartResponse result = cartService.clearCart(userId);

        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
    }

    // ===== validateCart() Tests =====
    @Test
    @DisplayName("Should validate cart successfully with no errors")
    void testValidateCartSuccess() {
        CartItem validItem = CartItem.builder()
            .id(itemId)
            .cart(cart)
            .productId(productId)
            .productName("Valid Product")
            .quantity(2)
            .price(new BigDecimal("100.00"))
            .totalPrice(new BigDecimal("200.00"))
            .build();

        Cart validCart = Cart.builder()
            .id(cartId)
            .userId(userId)
            .status(CartStatus.ACTIVE)
            .totalAmount(new BigDecimal("200.00"))
            .items(List.of(validItem))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(validCart));

        CartValidationResponse result = cartService.validateCart(userId);

        assertTrue(result.getIsValid());
        assertEquals(0, result.getErrorCount());
        assertEquals(0, result.getWarningCount());
    }

    @Test
    @DisplayName("Should throw exception when cart not found during validation")
    void testValidateCartNotFound() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> cartService.validateCart(userId));

        assertEquals("Cart not found for user: " + userId, exception.getMessage());
        assertEquals(ErrorCode.CART_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("Should validate empty cart as invalid")
    void testValidateEmptyCart() {
        Cart emptyCart = Cart.builder()
            .id(cartId)
            .userId(userId)
            .status(CartStatus.ACTIVE)
            .totalAmount(BigDecimal.ZERO)
            .items(new ArrayList<>())
            .build();

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(emptyCart));

        CartValidationResponse result = cartService.validateCart(userId);

        assertFalse(result.getIsValid());
        assertEquals(1, result.getErrorCount());
        assertEquals("Cart is empty", result.getValidationErrors().get(0));
    }

    @Test
    @DisplayName("Should detect product not found during validation")
    void testValidateCartProductNotFound() {
        CartItem itemWithoutProduct = CartItem.builder()
            .id(itemId)
            .cart(cart)
            .productId(null)
            .productName("Test Product")
            .quantity(2)
            .price(new BigDecimal("100.00"))
            .totalPrice(new BigDecimal("200.00"))
            .build();

        cart.setItems(List.of(itemWithoutProduct));

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        CartValidationResponse result = cartService.validateCart(userId);

        assertFalse(result.getIsValid());
        assertEquals(1, result.getErrorCount());
    }

    @Test
    @DisplayName("Should detect price issues during validation")
    void testValidateCartPriceIssues() {
        CartItem itemWithInvalidPrice = CartItem.builder()
            .id(itemId)
            .cart(cart)
            .productId(productId)
            .productName("Test Product")
            .quantity(2)
            .price(BigDecimal.ZERO)
            .totalPrice(BigDecimal.ZERO)
            .build();

        cart.setItems(List.of(itemWithInvalidPrice));

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        CartValidationResponse result = cartService.validateCart(userId);

        assertEquals(1, result.getWarningCount());
    }

    // ===== getCheckoutSummary() Tests =====
    @Test
    @DisplayName("Should get checkout summary successfully")
    void testGetCheckoutSummarySuccess() {
        Cart cartWithItems = Cart.builder()
            .id(cartId)
            .userId(userId)
            .status(CartStatus.ACTIVE)
            .totalAmount(new BigDecimal("1000.00"))
            .items(List.of(cartItem))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cartWithItems));
        when(cartMapper.toCartItemResponseList(cartWithItems.getItems())).thenReturn(new ArrayList<>());

        CheckoutSummaryResponse result = cartService.getCheckoutSummary(userId);

        assertNotNull(result);
        assertEquals(new BigDecimal("1000.00"), result.getSubtotal());
        assertEquals(new BigDecimal("100.00"), result.getTax());
        assertEquals(new BigDecimal("50.00"), result.getDeliveryCharge());
        verify(cartMapper, times(1)).toCartItemResponseList(any());
    }

    @Test
    @DisplayName("Should throw exception when cart not found during checkout summary")
    void testGetCheckoutSummaryCartNotFound() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> cartService.getCheckoutSummary(userId));

        assertEquals("Cart not found for user: " + userId, exception.getMessage());
        assertEquals(ErrorCode.CART_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("Should throw exception when cart is empty during checkout summary")
    void testGetCheckoutSummaryEmptyCart() {
        Cart emptyCart = Cart.builder()
            .id(cartId)
            .userId(userId)
            .status(CartStatus.ACTIVE)
            .totalAmount(BigDecimal.ZERO)
            .items(new ArrayList<>())
            .build();

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(emptyCart));

        BadRequestException exception = assertThrows(BadRequestException.class,
            () -> cartService.getCheckoutSummary(userId));

        assertEquals("Cannot checkout with empty cart", exception.getMessage());
        assertEquals(ErrorCode.CART_EMPTY, exception.getErrorCode());
    }

    @Test
    @DisplayName("Should calculate tax correctly")
    void testGetCheckoutSummaryTaxCalculation() {
        Cart cartWithSpecificAmount = Cart.builder()
            .id(cartId)
            .userId(userId)
            .status(CartStatus.ACTIVE)
            .totalAmount(new BigDecimal("1000.00"))
            .items(List.of(cartItem))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cartWithSpecificAmount));
        when(cartMapper.toCartItemResponseList(cartWithSpecificAmount.getItems())).thenReturn(new ArrayList<>());

        CheckoutSummaryResponse result = cartService.getCheckoutSummary(userId);

        assertEquals(new BigDecimal("1000.00"), result.getSubtotal());
        assertEquals(new BigDecimal("100.00"), result.getTax());
    }

    // ===== syncCart() Tests =====
    @Test
    @DisplayName("Should sync cart successfully")
    void testSyncCartSuccess() {
        Cart cartWithItems = Cart.builder()
            .id(cartId)
            .userId(userId)
            .status(CartStatus.ACTIVE)
            .totalAmount(new BigDecimal("200.00"))
            .items(new ArrayList<>(List.of(cartItem)))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cartWithItems));
        when(cartRepository.save(any(Cart.class))).thenReturn(cartWithItems);
        when(cartMapper.toCartResponse(cartWithItems)).thenReturn(cartResponse);

        CartResponse result = cartService.syncCart(userId);

        assertNotNull(result);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    @DisplayName("Should throw exception when cart not found during sync")
    void testSyncCartNotFound() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> cartService.syncCart(userId));

        assertEquals("Cart not found for user: " + userId, exception.getMessage());
        assertEquals(ErrorCode.CART_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("Should sync empty cart successfully")
    void testSyncEmptyCart() {
        Cart emptyCart = Cart.builder()
            .id(cartId)
            .userId(userId)
            .status(CartStatus.ACTIVE)
            .totalAmount(BigDecimal.ZERO)
            .items(new ArrayList<>())
            .build();

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(emptyCart));
        CartResponse emptyResponse = CartResponse.builder()
            .cartId(cartId)
            .userId(userId)
            .status(CartStatus.ACTIVE)
            .totalAmount(BigDecimal.ZERO)
            .items(new ArrayList<>())
            .build();
        when(cartMapper.toCartResponse(emptyCart)).thenReturn(emptyResponse);

        CartResponse result = cartService.syncCart(userId);

        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
    }

    // ===== getCartItem() Tests =====
    @Test
    @DisplayName("Should get cart item successfully")
    void testGetCartItemSuccess() {
        CartItemResponse itemResponse = CartItemResponse.builder()
            .itemId(itemId)
            .productId(productId)
            .productName("Test Product")
            .quantity(2)
            .price(new BigDecimal("100.00"))
            .totalPrice(new BigDecimal("200.00"))
            .build();

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(itemId)).thenReturn(Optional.of(cartItem));
        when(cartMapper.toCartItemResponse(cartItem)).thenReturn(itemResponse);

        CartItemResponse result = cartService.getCartItem(userId, itemId);

        assertNotNull(result);
        assertEquals(itemId, result.getItemId());
        verify(cartMapper, times(1)).toCartItemResponse(cartItem);
    }

    @Test
    @DisplayName("Should throw exception when cart not found during get item")
    void testGetCartItemCartNotFound() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> cartService.getCartItem(userId, itemId));

        assertEquals("Cart not found for user: " + userId, exception.getMessage());
        assertEquals(ErrorCode.CART_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("Should throw exception when item not found during get item")
    void testGetCartItemNotFound() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(itemId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> cartService.getCartItem(userId, itemId));

        assertEquals("Cart item not found with id: " + itemId, exception.getMessage());
        assertEquals(ErrorCode.CART_ITEM_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("Should throw exception when item doesn't belong to cart during get item")
    void testGetCartItemNotInCart() {
        Cart differentCart = Cart.builder()
            .id(UUID.randomUUID())
            .userId(userId)
            .status(CartStatus.ACTIVE)
            .totalAmount(BigDecimal.ZERO)
            .items(new ArrayList<>())
            .build();

        CartItem itemFromDifferentCart = CartItem.builder()
            .id(itemId)
            .cart(differentCart)
            .productId(productId)
            .productName("Test Product")
            .quantity(2)
            .price(new BigDecimal("100.00"))
            .totalPrice(new BigDecimal("200.00"))
            .build();

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findById(itemId)).thenReturn(Optional.of(itemFromDifferentCart));

        BadRequestException exception = assertThrows(BadRequestException.class,
            () -> cartService.getCartItem(userId, itemId));

        assertEquals("Cart item does not belong to this cart", exception.getMessage());
        assertEquals(ErrorCode.INVALID_PARAMETER, exception.getErrorCode());
    }

    // ===== Helper Methods =====
    private Cart buildCart() {
        return Cart.builder()
            .id(cartId)
            .userId(userId)
            .status(CartStatus.ACTIVE)
            .totalAmount(new BigDecimal("200.00"))
            .items(new ArrayList<>())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    private CartItem buildCartItem() {
        return CartItem.builder()
            .id(itemId)
            .cart(cart)
            .productId(productId)
            .productName("Test Product")
            .quantity(2)
            .price(new BigDecimal("100.00"))
            .totalPrice(new BigDecimal("200.00"))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    private CartResponse buildCartResponse() {
        CartItemResponse itemResponse = CartItemResponse.builder()
            .itemId(itemId)
            .productId(productId)
            .productName("Test Product")
            .quantity(2)
            .price(new BigDecimal("100.00"))
            .totalPrice(new BigDecimal("200.00"))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        return CartResponse.builder()
            .cartId(cartId)
            .userId(userId)
            .status(CartStatus.ACTIVE)
            .totalAmount(new BigDecimal("200.00"))
            .items(List.of(itemResponse))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    private RedisCart buildRedisCart() {
        RedisCartItem redisItem = RedisCartItem.builder()
            .itemId(itemId.toString())
            .productId(productId.toString())
            .productName("Test Product")
            .quantity(2)
            .price(new BigDecimal("100.00"))
            .totalPrice(new BigDecimal("200.00"))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        return RedisCart.builder()
            .cartId(cartId.toString())
            .userId(userId.toString())
            .status(CartStatus.ACTIVE.name())
            .totalAmount(new BigDecimal("200.00"))
            .items(List.of(redisItem))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }
}
