package com.ecom.cart.mapper;

import java.util.List;

import com.ecom.cart.dto.response.CartItemResponse;
import com.ecom.cart.dto.response.CartResponse;
import com.ecom.cart.entity.Cart;
import com.ecom.cart.entity.CartItem;

/**
 * Mapper interface for converting between domain models and DTOs.
 * 
 * Follows the Mapper/Converter design pattern to maintain separation of concerns.
 * This interface ensures that domain entities remain independent of presentation layer concerns.
 * 
 * Design Pattern: Mapper Pattern
 * Principle Applied: Single Responsibility Principle (SRP)
 * - Mapper focuses solely on data transformation
 * 
 * Implementation: CartMapperImpl
 */
public interface CartMapper {

    /**
     * Converts a Cart entity to a CartResponse DTO.
     * 
     * Includes all cart items converted to CartItemResponse.
     * 
     * @param cart the Cart entity to convert
     * @return CartResponse containing cart details
     */
    CartResponse toCartResponse(Cart cart);

    /**
     * Converts a CartItem entity to a CartItemResponse DTO.
     * 
     * @param cartItem the CartItem entity
     * @return CartItemResponse containing item details
     */
    CartItemResponse toCartItemResponse(CartItem cartItem);

    /**
     * Converts a list of CartItem entities to a list of CartItemResponse DTOs.
     * 
     * @param cartItems the list of CartItem entities
     * @return List of CartItemResponse DTOs
     */
    List<CartItemResponse> toCartItemResponseList(List<CartItem> cartItems);

    /**
     * Converts a list of Cart entities to a list of CartResponse DTOs.
     * 
     * @param carts the list of Cart entities
     * @return List of CartResponse DTOs
     */
    List<CartResponse> toCartResponseList(List<Cart> carts);
}
