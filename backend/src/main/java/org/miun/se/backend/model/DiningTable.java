package org.miun.se.backend.model;

import jakarta.persistence.*;
import org.miun.se.backend.model.enums.TableStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
    @Table(name = "dining_table")
    public class DiningTable {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "table_id")
        private Integer tableId;

        @Column(name = "table_number", nullable = false, unique = true)
        private Integer tableNumber;

        @Column(name = "capacity", nullable = false)
        private Integer capacity;

        @Enumerated(EnumType.STRING)
        @Column(name = "table_status", nullable = false)
        private TableStatus tableStatus;

        @Column(name = "created_at", nullable = false, updatable = false)
        private LocalDateTime createdAt;

        @Column(name = "updated_at", nullable = false)
        private LocalDateTime updatedAt;

        @OneToMany(mappedBy = "diningTable")
        private List<Booking> bookings = new ArrayList<>();

        // Constructors
        protected DiningTable() {}

        public DiningTable(Integer tableNumber, Integer capacity) {
            this.tableNumber = tableNumber;
            this.capacity = capacity;
            this.tableStatus = TableStatus.AVAILABLE;
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
        public Integer getTableId() { return tableId; }
        public void setTableId(Integer tableId) { this.tableId = tableId; }

        public Integer getTableNumber() { return tableNumber; }
        public void setTableNumber(Integer tableNumber) { this.tableNumber = tableNumber; }

        public Integer getCapacity() { return capacity; }
        public void setCapacity(Integer capacity) { this.capacity = capacity; }

        public TableStatus getTableStatus() { return tableStatus; }
        public void setTableStatus(TableStatus tableStatus) { this.tableStatus = tableStatus; }

        public List<Booking> getBookings() { return bookings; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }

    }
