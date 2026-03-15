package com.ecom.ordersystem.orderservice.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OrderAddress - Domain model representing shipping/delivery address for an order.
 * 
 * Stores complete delivery address information including:
 * - Receiver name and contact number
 * - Full address (line1, line2, city, state, country, postal code)
 * 
 * Database Mapping:
 * - Table Name: order_address
 * - Primary Key: address_id (UUID)
 * - Foreign Key: order_id (references orders table, unique constraint)
 * 
 * Relationship:
 * - One-to-One with Order
 * - Child entity in Order aggregate
 * 
 * Design Patterns:
 * - Entity Pattern: JPA entity with ORM mapping
 * - Value Object: Encapsulates address information
 * 
 * Unique Constraint:
 * - One address per order (one-to-one relationship)
 * - Can be updated but not duplicated
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_address")
public class OrderAddress {

    /**
     * Unique identifier for the address record.
     * 
     * - Type: UUID
     * - Generated: Automatically via database
     * - Primary Key: Yes
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "address_id")
    private UUID id;

    /**
     * Reference to the parent Order entity.
     * 
     * One-to-One relationship: Each order has exactly one shipping address.
     * 
     * - Type: Order entity
     * - Required: Yes
     * - Foreign Key Column: order_id
     * - Unique: Yes (one address per order)
     * - Cascade: Handled by Order entity
     * 
     * @see Order
     */
    @OneToOne
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    /**
     * Name of the person receiving the order.
     * 
     * Used on delivery labels and communication with receiver.
     * 
     * - Type: String
     * - Length: Max 100 characters
     * - Required: Yes
     * - Example: "John Doe"
     */
    @Column(name = "receiver_name", nullable = false, length = 100)
    private String receiverName;

    /**
     * Contact phone number of the receiver.
     * 
     * Used for delivery notifications and contact purposes.
     * May be in various formats depending on country.
     * 
     * - Type: String
     * - Length: Max 20 characters
     * - Required: Yes
     * - Example: "+1-555-0100" or "9876543210"
     */
    @Column(name = "receiver_phone", nullable = false, length = 20)
    private String receiverPhone;

    /**
     * Primary address line.
     * 
     * Usually contains street number and street name.
     * 
     * - Type: String
     * - Length: Max 255 characters
     * - Required: Yes
     * - Example: "123 Main Street, Apt 4B"
     */
    @Column(name = "address_line1", nullable = false, length = 255)
    private String addressLine1;

    /**
     * Secondary address line (optional).
     * 
     * Can contain additional address details like building name, suite number, etc.
     * 
     * - Type: String
     * - Length: Max 255 characters
     * - Required: No (nullable)
     * - Example: "Suite 500" or "Behind City Center"
     */
    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    /**
     * City or town name.
     * 
     * Delivery location city/town.
     * 
     * - Type: String
     * - Length: Max 100 characters
     * - Required: Yes
     * - Example: "New York"
     */
    @Column(name = "city", nullable = false, length = 100)
    private String city;

    /**
     * State or province name.
     * 
     * Administrative division of the delivery location.
     * 
     * - Type: String
     * - Length: Max 100 characters
     * - Required: Yes
     * - Example: "NY" or "New York"
     */
    @Column(name = "state", nullable = false, length = 100)
    private String state;

    /**
     * Country name.
     * 
     * Country where the order is to be delivered.
     * 
     * - Type: String
     * - Length: Max 100 characters
     * - Required: Yes
     * - Example: "United States" or "USA"
     */
    @Column(name = "country", nullable = false, length = 100)
    private String country;

    /**
     * Postal or ZIP code.
     * 
     * Postal code for the delivery location.
     * Format varies by country.
     * 
     * - Type: String
     * - Length: Max 20 characters
     * - Required: Yes
     * - Example: "10001" (US ZIP) or "SW1A 1AA" (UK postcode)
     */
    @Column(name = "postal_code", nullable = false, length = 20)
    private String postalCode;
}

