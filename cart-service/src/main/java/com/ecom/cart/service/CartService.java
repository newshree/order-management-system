package com.ecom.cart.service;

import java.util.UUID;

import com.ecom.cart.dto.request.AddItemToCartRequest;
import com.ecom.cart.dto.request.UpdateCartItemQuantityRequest;
import com.ecom.cart.dto.response.CartItemResponse;
import com.ecom.cart.dto.response.CartResponse;
import com.ecom.cart.dto.response.CartValidationResponse;
import com.ecom.cart.dto.response.CheckoutSummaryResponse;

/**
 * CartService - Business logic interface for cart operations.
 * 
 * Defines all business operations for cart management including:
 * - Cart CRUD operations
 * - Item management (add, update, remove)
 * - Validation and checkout
 * - Price and inventory synchronization
 * 
 */
public interface CartService {

    /**
     * Retrieves the cart for a specific user.
     * 
     * If cart doesn't exist, creates a new one.
     * Always returns a cart (never null).
     * 
     * Business Logic:
     * 1. Check Redis for active cart
     * 2. If not found, check database
     * 3. If not exists, create new cart
     * 4. Store in Redis for fast access
     * 
     * @param userId the UUID of the user
     * @return CartResponse containing cart details
     * @throws ResourceNotFoundException should not be thrown (cart is created if needed)
     */
    CartResponse getCart(UUID userId);

    /**
     * Adds an item to the user's cart.
     * 
     * Core Add Item Logic:
     * 1. Fetch product from Product Service
     * 2. Validate product exists
     * 3. Check if item already in cart
     *    - If yes: increase quantity
     *    - If no: add new item
     * 4. Store current product price
     * 5. Recalculate cart total
     * 6. Persist to both Redis and database
     * 
     * @param userId the UUID of the user
     * @param request AddItemToCartRequest containing product ID and quantity
     * @return CartResponse with updated cart
     * @throws ResourceNotFoundException if product not found
     * @throws BadRequestException if quantity is invalid
     */
    CartResponse addItemToCart(UUID userId, AddItemToCartRequest request);

    /**
     * Updates the quantity of an item in the cart.
     * 
     * Business Logic:
     * 1. Get cart by user ID
     * 2. Find cart item by item ID
     * 3. Validate quantity > 0
     * 4. Update quantity
     * 5. Recalculate total price for item
     * 6. Recalculate cart total
     * 7. Persist changes
     * 
     * @param userId the UUID of the user
     * @param itemId the UUID of the cart item
     * @param request UpdateCartItemQuantityRequest containing new quantity
     * @return CartResponse with updated cart
     * @throws ResourceNotFoundException if cart or item not found
     * @throws BadRequestException if quantity is invalid (0 or negative)
     */
    CartResponse updateItemQuantity(UUID userId, UUID itemId, UpdateCartItemQuantityRequest request);

    /**
     * Removes an item from the cart.
     * 
     * Business Logic:
     * 1. Get cart by user ID
     * 2. Find and delete cart item
     * 3. Recalculate cart total
     * 4. If cart is empty, optionally delete cart
     * 5. Persist changes
     * 
     * @param userId the UUID of the user
     * @param itemId the UUID of the cart item
     * @return CartResponse with updated cart
     * @throws ResourceNotFoundException if cart or item not found
     */
    CartResponse removeItemFromCart(UUID userId, UUID itemId);

    /**
     * Clears all items from the user's cart.
     * 
     * Business Logic:
     * 1. Get cart by user ID
     * 2. Delete all cart items
     * 3. Reset cart total to 0.00
     * 4. Persist changes
     * 5. Update Redis cache
     * 
     * @param userId the UUID of the user
     * @return CartResponse with empty cart
     * @throws ResourceNotFoundException if cart not found
     */
    CartResponse clearCart(UUID userId);

    /**
     * Validates the cart for checkout.
     * 
     * Comprehensive Validation Logic:
     * For each item:
     * 1. Check if product still exists
     * 2. Check if price has changed (price mismatch)
     * 3. Check inventory availability
     * 
     * Returns detailed validation results with errors and warnings.
     * 
     * @param userId the UUID of the user
     * @return CartValidationResponse with validation results
     * @throws ResourceNotFoundException if cart not found
     */
    CartValidationResponse validateCart(UUID userId);

    /**
     * Gets checkout summary with pricing details.
     * 
     * Checkout Logic:
     * 1. Fetch cart items
     * 2. Calculate subtotal
     * 3. Calculate tax (10% of subtotal)
     * 4. Calculate delivery charge (fixed or based on location)
     * 5. Calculate final amount
     * 
     * @param userId the UUID of the user
     * @return CheckoutSummaryResponse with complete pricing breakdown
     * @throws ResourceNotFoundException if cart not found or empty
     * @throws BadRequestException if cart is empty
     */
    CheckoutSummaryResponse getCheckoutSummary(UUID userId);

    /**
     * Syncs cart with latest product information.
     * 
     * Update prices from Product Service:
     * 1. Fetch all cart items
     * 2. For each item, call Product Service to get current price
     * 3. Update price in cart item
     * 4. Remove unavailable items
     * 5. Recalculate cart total
     * 6. Persist changes
     * 
     * This operation is optional but recommended before checkout
     * to ensure prices are current.
     * 
     * @param userId the UUID of the user
     * @return CartResponse with synced cart
     * @throws ResourceNotFoundException if cart not found
     */
    CartResponse syncCart(UUID userId);

    /**
     * Gets a specific cart item details.
     * 
     * @param userId the UUID of the user
     * @param itemId the UUID of the cart item
     * @return CartItemResponse with item details
     * @throws ResourceNotFoundException if cart or item not found
     */
    CartItemResponse getCartItem(UUID userId, UUID itemId);
}
