package com.ecom.cart.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ecom.cart.dto.response.CartItemResponse;
import com.ecom.cart.dto.response.CartResponse;
import com.ecom.cart.entity.Cart;
import com.ecom.cart.entity.CartItem;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of CartMapper interface.
 *
 * Handles all data transformation between domain entities and Data Transfer Objects (DTOs).
 * This component ensures clear separation between business logic (service layer) and
 * presentation concerns (controller/API layer).
 *
 * Design Pattern: Component/Converter Pattern
 * Principle Applied:
 * - Single Responsibility Principle (SRP): Focused solely on data transformation
 * - Dependency Inversion Principle (DIP): Depends on abstraction (CartMapper interface)
 */
@Slf4j
@Component
public class CartMapperImpl implements CartMapper {

    /**
     * Converts a Cart entity to a CartResponse DTO.
     *
     * Includes conversion of all nested CartItem entities to CartItemResponse DTOs.
     *
     * @param cart the Cart entity to convert
     * @return CartResponse built from entity, or null if entity is null
     */
    @Override
    public CartResponse toCartResponse(Cart cart) {
        if (cart == null) {
            log.trace("Cart entity is null, returning null");
            return null;
        }

        int itemCount = cart.getItems() != null ? cart.getItems().size() : 0;
        log.trace("Converting Cart to CartResponse: cartId={}, userId={}, itemCount={}", cart.getId(), cart.getUserId(), itemCount);

        return CartResponse.builder()
            .cartId(cart.getId())
            .userId(cart.getUserId())
            .status(cart.getStatus())
            .totalAmount(cart.getTotalAmount())
            .items(toCartItemResponseList(cart.getItems()))
            .createdAt(cart.getCreatedAt())
            .updatedAt(cart.getUpdatedAt())
            .build();
    }

    /**
     * Converts a CartItem entity to a CartItemResponse DTO.
     *
     * @param cartItem the CartItem entity to convert
     * @return CartItemResponse built from entity, or null if entity is null
     */
    @Override
    public CartItemResponse toCartItemResponse(CartItem cartItem) {
        if (cartItem == null) {
            log.trace("CartItem entity is null, returning null");
            return null;
        }

        log.trace("Converting CartItem to CartItemResponse: itemId={}, productId={}", cartItem.getId(), cartItem.getProductId());

        return CartItemResponse.builder()
            .itemId(cartItem.getId())
            .productId(cartItem.getProductId())
            .productName(cartItem.getProductName())
            .quantity(cartItem.getQuantity())
            .price(cartItem.getPrice())
            .totalPrice(cartItem.getTotalPrice())
            .createdAt(cartItem.getCreatedAt())
            .updatedAt(cartItem.getUpdatedAt())
            .build();
    }

    /**
     * Converts a list of CartItem entities to a list of CartItemResponse DTOs.
     *
     * Handles null and empty lists gracefully.
     *
     * @param cartItems the list of CartItem entities
     * @return List of CartItemResponse DTOs, or empty list if input is null/empty
     */
    @Override
    public List<CartItemResponse> toCartItemResponseList(List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            log.trace("CartItem list is null or empty, returning empty list");
            return List.of();
        }

        log.trace("Converting CartItem list to CartItemResponse list: itemCount={}", cartItems.size());
        return cartItems.stream()
            .map(this::toCartItemResponse)
            .toList();
    }

    /**
     * Converts a list of Cart entities to a list of CartResponse DTOs.
     *
     * Handles null and empty lists gracefully.
     *
     * @param carts the list of Cart entities
     * @return List of CartResponse DTOs, or empty list if input is null/empty
     */
    @Override
    public List<CartResponse> toCartResponseList(List<Cart> carts) {
        if (carts == null || carts.isEmpty()) {
            log.trace("Cart list is null or empty, returning empty list");
            return List.of();
        }

        log.trace("Converting Cart list to CartResponse list: cartCount={}", carts.size());
        return carts.stream()
            .map(this::toCartResponse)
            .toList();
    }
}