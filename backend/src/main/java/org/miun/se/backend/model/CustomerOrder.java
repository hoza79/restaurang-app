package org.miun.se.backend.model;


import jakarta.persistence.*;
import org.miun.se.backend.model.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer_order")
public class CustomerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    /* Implement when table is implemented
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "table_id", nullable = false)
    private DiningTable dining_table;
    */

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "total_price", nullable = false)
    private double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "customerOrder", cascade = CascadeType.ALL)
    private List<OrderBatch> orderBatches = new ArrayList<>();

    // Constructors
    protected CustomerOrder() {}

    public CustomerOrder(Employee employee) {
        //this.table = table;
        this.employee = employee;
    }

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        if(orderStatus == null){
            orderStatus = OrderStatus.IN_PROGRESS;
        }
        calculateTotalPrice();
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    //Calculate total price of order
    private void calculateTotalPrice(){
        totalPrice = 0.0;
        if (orderBatches != null) {
            for (OrderBatch batch : orderBatches) {
                if (batch.getItems() != null) {
                    for (OrderItem item : batch.getItems()) {
                        if (item.getQuantity() != null) {
                            totalPrice += item.getItemPrice() * item.getQuantity();
                        }
                    }
                }
            }
        }
    }

    // Getters and setters
    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public OrderStatus getOrderStatus() { return orderStatus; }
    public void setOrderStatus(OrderStatus orderStatus) { this.orderStatus = orderStatus; }

    public List<OrderBatch> getOrderBatches() { return orderBatches; }
    public void setOrderBatches(List<OrderBatch> orderBatches) { this.orderBatches = orderBatches; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
