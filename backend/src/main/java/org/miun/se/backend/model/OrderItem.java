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

    @Column(name = "notes", nullable = false)
    private String notes = "";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    protected OrderItem() {}

    public OrderItem(MenuItem menuItem, OrderBatch orderBatch, String notes) {
        this.menuItem = menuItem;
        this.orderBatch = orderBatch;
        this.notes = notes;
    }

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        if(menuItem != null){
            itemPrice = menuItem.getPrice();
        }
        if(appliedPriority == null && menuItem != null){
            appliedPriority = menuItem.getDefaultPriority();
        }
        if(quantity == null) {
            quantity = 1;
        }
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
