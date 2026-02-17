package org.miun.se.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lunch_availability",
        uniqueConstraints = @UniqueConstraint(
                name = "unique_weekday_per_menu_item",
                columnNames = {"menu_item_id", "weekday"}
        )
)
public class LunchAvailability {

    private static final String LUNCH_CATEGORY_NAME = "Lunch";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer lunchAvailabilityId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @Column(nullable = false)
    private Integer weekday;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public LunchAvailability() {}

    public LunchAvailability(MenuItem menuItem, Integer weekday) {
        this.menuItem = menuItem;
        this.weekday = weekday;
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
        Integer weekday = getWeekday();
        if (weekday == null || weekday < 1 || weekday > 5) {
            throw new IllegalArgumentException("Weekday must be in range 1-5");
        }

        if (menuItem == null || menuItem.getCategory() == null || menuItem.getCategory().getCategoryName() == null) {
            throw new IllegalArgumentException("MenuItem and MenuItem category must be set");
        }

        if (!LUNCH_CATEGORY_NAME.equals(menuItem.getCategory().getCategoryName())) {
            throw new IllegalArgumentException("MenuItem must be in category " + LUNCH_CATEGORY_NAME);
        }
    }

    // Getters and setters

    public Integer getLunchItemAvailabilityId() {
        return lunchAvailabilityId;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public Integer getWeekday() {
        return weekday;
    }

    public void setWeekday(Integer weekday) {
        this.weekday = weekday;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
