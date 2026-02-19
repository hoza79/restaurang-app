package org.miun.se.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Integer orderItemId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_batch_id", nullable = false)
    private OrderBatch orderBatch;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "item_price", nullable = false)
    private double itemPrice;

    @Column(name = "applied_priority", nullable = false)
    private Integer appliedPriority;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    protected OrderItem() {}

    public OrderItem(OrderBatch orderBatch, MenuItem menuItem, int quantity, String notes) {

        if (orderBatch == null) {
            throw new IllegalArgumentException("OrderBatch cannot be null");
        }

        if (menuItem == null) {
            throw new IllegalArgumentException("MenuItem cannot be null");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        this.orderBatch = orderBatch;
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.itemPrice = menuItem.getPrice();
        this.appliedPriority = menuItem.getDefaultPriority();
        this.notes = notes;
    }

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and setters
    public Integer getOrderItemId() { return orderItemId; }
    public void setOrderItemId(Integer orderItemId) { this.orderItemId = orderItemId; }

    public MenuItem getMenuItem() { return menuItem; }
    public void setMenuItem(MenuItem menuItem) { this.menuItem = menuItem; }

    public OrderBatch getOrderBatch() { return orderBatch; }
    public void setOrderBatch(OrderBatch orderBatch) { this.orderBatch = orderBatch; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public double getItemPrice() { return itemPrice; }
    public void setItemPrice(double itemPrice) { this.itemPrice = itemPrice; }

    public Integer getAppliedPriority() { return appliedPriority; }
    public void setAppliedPriority(Integer appliedPriority) { this.appliedPriority = appliedPriority; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
