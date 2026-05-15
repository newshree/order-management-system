package com.ecom.cart.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

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
import com.ecom.cart.service.CartService;

/**
 * CartServiceImpl - Implementation of CartService business logic.
 * 
 * Core responsibilities:
 * - Managing cart operations (add, update, remove, clear)
 * - Validating cart items and availability
 * - Calculating pricing information
 * - Synchronizing with external services (Product Service)
 * - Managing both Redis (fast) and database (persistent) storage
 * 
 * Architecture:
 * - Redis: Primary storage for active carts (TTL: 7 days)
 * - PostgreSQL: Backup/persistent storage
 * - Both are kept in sync
 * 
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private RedisCartRepository redisCartRepository;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private RestTemplate restTemplate;

    private static final BigDecimal TAX_RATE = new BigDecimal("0.10");  // 10% tax
    private static final BigDecimal DELIVERY_CHARGE = new BigDecimal("50.00");  // Fixed delivery

    /**
     * Retrieves the cart for a specific user.
     * 
     * Priority:
     * 1. Check Redis (fast cache)
     * 2. Check database (persistent storage)
     * 3. Create new cart if doesn't exist
     */
    @Override
    public CartResponse getCart(UUID userId) {
        
        // Try to get from Redis first (fast)
        return redisCartRepository.findById(userId.toString())
            .map(this::convertRedisCartToResponse)
            .orElseGet(() -> {

                // Get from database or create new
                Cart cart = cartRepository.findByUserId(userId)
                        .orElseGet(() -> createNewCart(userId));

                // Cache in Redis
                saveToRedis(cart);

                return cartMapper.toCartResponse(cart);
            });
    }

    /**
     * Adds an item to the user's cart.
     * 
     * Business Logic:
     * 1. Get or create cart
     * 2. Fetch product from Product Service
     * 3. Check if item already in cart
     *    - If yes: increase quantity
     *    - If no: add new item
     * 4. Store product price snapshot
     * 5. Recalculate cart total
     * 6. Save to both Redis and database
     */
    @Override
    @Transactional
    public CartResponse addItemToCart(UUID userId, AddItemToCartRequest request) {
        // Validate input
        if (request.getQuantity() <= 0) {
            throw new BadRequestException(
                ErrorCode.INVALID_QUANTITY,
                "Quantity must be greater than 0"
            );
        }

        // Get or create cart
        Cart cart = cartRepository.findByUserId(userId)
            .orElseGet(() -> createNewCart(userId));

        // Fetch product details from Product Service
        String productServiceUrl = "http://localhost:8003/api/products/getProductById/" + request.getProductId();
        try {
            // Note: In production, use a service client, not RestTemplate directly
            // For now, we'll just validate that the product ID is not null
            if (request.getProductId() == null) {
                throw new ResourceNotFoundException(
                    ErrorCode.PRODUCT_NOT_FOUND,
                    "Product not found with id: " + request.getProductId()
                );
            }
        } catch (Exception e) {
            throw new ResourceNotFoundException(
                ErrorCode.PRODUCT_NOT_FOUND,
                "Product not found with id: " + request.getProductId()
            );
        }

        // Check if item already in cart
        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(
            cart.getId(),
            request.getProductId()
        );

        CartItem cartItem;
        if (existingItem.isPresent()) {
            // Item exists: increase quantity
            cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
            cartItem.recalculateTotalPrice();
        } else {
            // Item doesn't exist: add new item
            cartItem = CartItem.builder()
                .cart(cart)
                .productId(request.getProductId())
                .productName("Product " + request.getProductId())  // Default name
                .quantity(request.getQuantity())
                .price(new BigDecimal("100.00"))  // Default price - would come from Product Service
                .build();
            cartItem.recalculateTotalPrice();
        }

        cartItemRepository.save(cartItem);

        // Add item to cart's items list (in-memory)
        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }
        if (existingItem.isEmpty()) {
            cart.getItems().add(cartItem);
        }

        // Refresh items and recalculate total (reload cart to avoid orphanRemoval issues)
        cart = cartRepository.findByUserId(userId).get();
        recalculateCartTotal(cart);
        cart = cartRepository.save(cart);

        // Save to Redis
        saveToRedis(cart);

        return cartMapper.toCartResponse(cart);
    }

    /**
     * Updates the quantity of an item in the cart.
     * 
     * Business Logic:
     * 1. Get cart
     * 2. Find and validate item exists
     * 3. Validate new quantity > 0
     * 4. Update quantity
     * 5. Recalculate prices
     * 6. Persist changes
     */
    @Override
    @Transactional
    public CartResponse updateItemQuantity(UUID userId, UUID itemId, UpdateCartItemQuantityRequest request) {
        // Validate input
        if (request.getQuantity() <= 0) {
            throw new BadRequestException(
                ErrorCode.INVALID_QUANTITY,
                "Quantity must be greater than 0"
            );
        }

        // Get cart
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                ErrorCode.CART_NOT_FOUND,
                "Cart not found for user: " + userId
            ));

        // Find cart item
        CartItem cartItem = cartItemRepository.findById(itemId)
            .orElseThrow(() -> new ResourceNotFoundException(
                ErrorCode.CART_ITEM_NOT_FOUND,
                "Cart item not found with id: " + itemId
            ));

        // Verify item belongs to this cart
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException(
                ErrorCode.INVALID_PARAMETER,
                "Cart item does not belong to this cart"
            );
        }

        // Update quantity and recalculate
        cartItem.setQuantity(request.getQuantity());
        cartItem.recalculateTotalPrice();
        cartItemRepository.save(cartItem);

        // Recalculate cart total (reload cart to avoid orphanRemoval issues)
        cart = cartRepository.findByUserId(userId).get();
        recalculateCartTotal(cart);
        cart = cartRepository.save(cart);

        // Save to Redis
        saveToRedis(cart);

        return cartMapper.toCartResponse(cart);
    }

    /**
     * Removes an item from the cart.
     * 
     * Business Logic:
     * 1. Get cart
     * 2. Find and delete item
     * 3. Recalculate cart total
     * 4. Persist changes
     */
    @Override
    @Transactional
    public CartResponse removeItemFromCart(UUID userId, UUID itemId) {
        // Get cart
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                ErrorCode.CART_NOT_FOUND,
                "Cart not found for user: " + userId
            ));

        // Find and delete cart item
        CartItem cartItem = cartItemRepository.findById(itemId)
            .orElseThrow(() -> new ResourceNotFoundException(
                ErrorCode.CART_ITEM_NOT_FOUND,
                "Cart item not found with id: " + itemId
            ));

        // Verify item belongs to this cart
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException(
                ErrorCode.INVALID_PARAMETER,
                "Cart item does not belong to this cart"
            );
        }

        // Remove from cart's items collection (orphanRemoval will delete from DB)
        cart.getItems().remove(cartItem);

        // Recalculate cart total
        recalculateCartTotal(cart);
        cart = cartRepository.save(cart);

        // Save to Redis
        saveToRedis(cart);

        return cartMapper.toCartResponse(cart);
    }

    /**
     * Clears all items from the user's cart.
     * 
     * Business Logic:
     * 1. Get cart
     * 2. Delete all items
     * 3. Reset total to 0.00
     * 4. Persist changes
     */
    @Override
    @Transactional
    public CartResponse clearCart(UUID userId) {
        // Get cart
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                ErrorCode.CART_NOT_FOUND,
                "Cart not found for user: " + userId
            ));

        // Clear all items from collection (orphanRemoval will delete from DB)
        cart.getItems().clear();
        cart.setTotalAmount(BigDecimal.ZERO);
        cart = cartRepository.save(cart);

        // Update Redis
        redisCartRepository.deleteById(userId.toString());

        return cartMapper.toCartResponse(cart);
    }

    /**
     * Validates the cart for checkout.
     * 
     * Validation Logic:
     * For each item:
     * 1. Check if product exists
     * 2. Check if price changed
     * 3. Check inventory availability
     */
    @Override
    public CartValidationResponse validateCart(UUID userId) {
        // Get cart
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                ErrorCode.CART_NOT_FOUND,
                "Cart not found for user: " + userId
            ));

        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            errors.add("Cart is empty");
            return CartValidationResponse.builder()
                .isValid(false)
                .errorCount(1)
                .warningCount(0)
                .validationErrors(errors)
                .validationWarnings(warnings)
                .build();
        }

        // Validate each item
        for (CartItem item : cart.getItems()) {
            // Check if product exists
            // In production, call Product Service
            if (item.getProductId() == null) {
                errors.add("Product " + item.getProductName() + " is no longer available");
            }

            // Check if price changed (warning, not error)
            // In production, fetch from Product Service and compare
            if (item.getPrice() == null || item.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                warnings.add("Product " + item.getProductName() + " price information is outdated");
            }

            // Check inventory (in production, call Inventory Service)
            // For now, just a placeholder
        }

        boolean isValid = errors.isEmpty();

        return CartValidationResponse.builder()
            .isValid(isValid)
            .errorCount(errors.size())
            .warningCount(warnings.size())
            .validationErrors(errors)
            .validationWarnings(warnings)
            .build();
    }

    /**
     * Gets checkout summary with pricing details.
     * 
     * Checkout Logic:
     * 1. Get cart items
     * 2. Calculate subtotal
     * 3. Calculate tax
     * 4. Add delivery charge
     * 5. Return complete summary
     */
    @Override
    public CheckoutSummaryResponse getCheckoutSummary(UUID userId) {
        // Get cart
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                ErrorCode.CART_NOT_FOUND,
                "Cart not found for user: " + userId
            ));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BadRequestException(
                ErrorCode.CART_EMPTY,
                "Cannot checkout with empty cart"
            );
        }

        // Calculate prices
        BigDecimal subtotal = cart.getTotalAmount();
        BigDecimal tax = subtotal.multiply(TAX_RATE).setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal deliveryCharge = DELIVERY_CHARGE;
        BigDecimal finalAmount = subtotal.add(tax).add(deliveryCharge).setScale(2, java.math.RoundingMode.HALF_UP);

        return CheckoutSummaryResponse.builder()
            .items(cartMapper.toCartItemResponseList(cart.getItems()))
            .subtotal(subtotal)
            .tax(tax)
            .deliveryCharge(deliveryCharge)
            .totalAmount(subtotal.add(tax))
            .finalAmount(finalAmount)
            .build();
    }

    /**
     * Syncs cart with latest product information.
     * 
     * Sync Logic:
     * 1. Get cart
     * 2. For each item, fetch latest product info
     * 3. Update prices
     * 4. Remove unavailable items
     * 5. Recalculate totals
     */
    @Override
    @Transactional
    public CartResponse syncCart(UUID userId) {
        // Get cart
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                ErrorCode.CART_NOT_FOUND,
                "Cart not found for user: " + userId
            ));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            return cartMapper.toCartResponse(cart);
        }

        // Sync each item - iterate and remove in place to avoid orphanRemoval issues
        List<CartItem> itemsToRemove = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            try {
                // In production, fetch from Product Service
                // For now, keep the item as is
            } catch (Exception e) {
                // Product no longer available, mark for removal
                itemsToRemove.add(item);
            }
        }

        // Remove unavailable items from the cart collection (orphanRemoval will handle DB deletion)
        cart.getItems().removeAll(itemsToRemove);
        recalculateCartTotal(cart);
        cart = cartRepository.save(cart);

        // Save to Redis
        saveToRedis(cart);

        return cartMapper.toCartResponse(cart);
    }

    /**
     * Gets a specific cart item details.
     */
    @Override
    public CartItemResponse getCartItem(UUID userId, UUID itemId) {
        // Get cart
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                ErrorCode.CART_NOT_FOUND,
                "Cart not found for user: " + userId
            ));

        // Find item
        CartItem cartItem = cartItemRepository.findById(itemId)
            .orElseThrow(() -> new ResourceNotFoundException(
                ErrorCode.CART_ITEM_NOT_FOUND,
                "Cart item not found with id: " + itemId
            ));

        // Verify item belongs to cart
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException(
                ErrorCode.INVALID_PARAMETER,
                "Cart item does not belong to this cart"
            );
        }

        return cartMapper.toCartItemResponse(cartItem);
    }

    /**
     * Helper method: Creates a new cart for a user.
     */
    private Cart createNewCart(UUID userId) {
        Cart cart = Cart.builder()
            .userId(userId)
            .status(CartStatus.ACTIVE)
            .totalAmount(BigDecimal.ZERO)
            .items(new ArrayList<>())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        return cartRepository.save(cart);
    }

    /**
     * Helper method: Recalculates cart total from all items.
     */
    private void recalculateCartTotal(Cart cart) {
        BigDecimal total = BigDecimal.ZERO;
        if (cart.getItems() != null && !cart.getItems().isEmpty()) {
            for (CartItem item : cart.getItems()) {
                total = total.add(item.getTotalPrice());
            }
        }
        cart.setTotalAmount(total);
        cart.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * Helper method: Saves cart to Redis for caching.
     */
    private void saveToRedis(Cart cart) {
        List<RedisCartItem> redisItems = new ArrayList<>();
        if (cart.getItems() != null) {
            for (CartItem item : cart.getItems()) {
                redisItems.add(RedisCartItem.builder()
                    .itemId(item.getId().toString())
                    .productId(item.getProductId().toString())
                    .productName(item.getProductName())
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .totalPrice(item.getTotalPrice())
                    .createdAt(item.getCreatedAt())
                    .updatedAt(item.getUpdatedAt())
                    .build());
            }
        }

        RedisCart redisCart = RedisCart.builder()
            .cartId(cart.getId().toString())
            .userId(cart.getUserId().toString())
            .status(cart.getStatus().name())
            .totalAmount(cart.getTotalAmount())
            .items(redisItems)
            .createdAt(cart.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .build();

        redisCartRepository.save(redisCart);
    }

    /**
     * Helper method: Converts RedisCart to CartResponse.
     */
    private CartResponse convertRedisCartToResponse(RedisCart redisCart) {
        List<CartItemResponse> itemResponses = new ArrayList<>();
        if (redisCart.getItems() != null) {
            for (RedisCartItem item : redisCart.getItems()) {
                itemResponses.add(CartItemResponse.builder()
                    .itemId(UUID.fromString(item.getItemId()))
                    .productId(UUID.fromString(item.getProductId()))
                    .productName(item.getProductName())
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .totalPrice(item.getTotalPrice())
                    .createdAt(item.getCreatedAt())
                    .updatedAt(item.getUpdatedAt())
                    .build());
            }
        }

        return CartResponse.builder()
            .cartId(UUID.fromString(redisCart.getCartId()))
            .userId(UUID.fromString(redisCart.getUserId()))
            .status(CartStatus.valueOf(redisCart.getStatus()))
            .totalAmount(redisCart.getTotalAmount())
            .items(itemResponses)
            .createdAt(redisCart.getCreatedAt())
            .updatedAt(redisCart.getUpdatedAt())
            .build();
    }
}
