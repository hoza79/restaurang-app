package org.miun.se.backend.model;

import jakarta.persistence.*;
import org.miun.se.backend.model.enums.BatchStatus;
import org.miun.se.backend.model.enums.BatchType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_batch")
public class OrderBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_id")
    private Integer batchId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private CustomerOrder customerOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "batch_type", nullable = false)
    private BatchType batchType;

    @Enumerated(EnumType.STRING)
    @Column(name = "batch_status", nullable = false)
    private BatchStatus batchStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "orderBatch", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    //Constructors
    protected OrderBatch() {}

    public OrderBatch(CustomerOrder customerOrder, BatchType batchType) {
        this.customerOrder = customerOrder;
        this.batchType = batchType;
    }

    // Lifecycle callbacks

    @PrePersist
    protected void onCreate() {
        if(batchStatus == null){
            batchStatus = BatchStatus.PROCESSING;
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    //Getters and setters

    public Integer getBatchId() { return batchId; }
    public void setBatchId(Integer batchId) { this.batchId = batchId; }

    public CustomerOrder getCustomerOrder() { return customerOrder; }
    public void setCustomerOrder(CustomerOrder customerOrder) { this.customerOrder = customerOrder; }

    public BatchType getBatchType() { return batchType; }
    public void setBatchType(BatchType batchType) { this.batchType = batchType; }

    public BatchStatus getBatchStatus() { return batchStatus; }
    public void setBatchStatus(BatchStatus batchStatus) { this.batchStatus = batchStatus; }

    public List<OrderItem> getItems() { return orderItems; }
    public void setItems(List<OrderItem> items) { this.orderItems = items; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
