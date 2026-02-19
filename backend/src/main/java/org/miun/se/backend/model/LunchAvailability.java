package org.miun.se.backend.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "lunch_availability",
        uniqueConstraints = @UniqueConstraint(
                name = "unique_date_per_menu_item",
                columnNames = {"menu_item_id", "available_date"}
        )
)
public class LunchAvailability {

    private static final String LUNCH_CATEGORY_NAME = "Lunch";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lunch_availability_id")
    private Integer lunchAvailabilityId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @Column(name = "available_date", nullable = false)
    private LocalDate availableDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    protected LunchAvailability() {}

    public LunchAvailability(MenuItem menuItem, LocalDate availableDate) {
        this.menuItem = menuItem;
        this.availableDate = availableDate;
    }

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        validate();
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        validate();
        updatedAt = LocalDateTime.now();
    }

    private void validate() {
        if (menuItem == null || menuItem.getCategory() == null || menuItem.getCategory().getCategoryName() == null) {
            throw new IllegalArgumentException("MenuItem and MenuItem category must be set");
        }

        if (!LUNCH_CATEGORY_NAME.equals(menuItem.getCategory().getCategoryName())) {
            throw new IllegalArgumentException("MenuItem must be in category " + LUNCH_CATEGORY_NAME);
        }
    }

    // Getters and setters

    public Integer getLunchAvailabilityId() {
        return lunchAvailabilityId;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public LocalDate getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(LocalDate availableDate) {
        this.availableDate = availableDate;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
