package org.miun.se.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import org.miun.se.backend.model.enums.ShiftStatus;
import org.miun.se.backend.model.enums.SwapStatus;

@Entity
@Table(name = "swap_request")
public class SwapRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "swap_id")
    private Integer swapId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_employee_id", nullable = false)
    private Employee senderEmployee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_employee_id", nullable = false)
    private Employee receiverEmployee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;

    @Enumerated(EnumType.STRING)
    @Column(name = "swap_status", nullable = false)
    private SwapStatus swapStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    protected SwapRequest() {}

    public SwapRequest(Employee senderEmployee, Employee receiverEmployee, Shift shift) {
        this.senderEmployee = senderEmployee;
        this.receiverEmployee = receiverEmployee;
        this.shift = shift;
        this.swapStatus = SwapStatus.PENDING;
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

    public Integer getSwapId() { return swapId; }
    public void setSwapId(Integer swapId) { this.swapId = swapId; }

    public Integer getSenderEmployeeId() { return senderEmployee.getEmployeeId(); }
    public void setSenderEmployeeId(Employee senderEmployee) { this.senderEmployee = senderEmployee; }

    public Integer getReceiverEmployeeId() { return receiverEmployee.getEmployeeId(); }
    public void setReceiverEmployee(Employee receiverEmployee) { this.receiverEmployee = receiverEmployee; }

    public Integer getShiftId() { return shift.getShiftId(); }
    public void setShiftId(Shift shift) { this.shift = shift; }

    public SwapStatus getSwapStatus() { return swapStatus; }
    public void setSwapStatus(SwapStatus swapStatus) { this.swapStatus = swapStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}