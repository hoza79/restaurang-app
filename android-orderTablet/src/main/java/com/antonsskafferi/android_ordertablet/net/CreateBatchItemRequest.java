package com.antonsskafferi.android_ordertablet.net;

public class CreateBatchItemRequest {
    public Integer menuItemId;
    public Integer quantity;
    public String notes;

    public CreateBatchItemRequest(Integer menuItemId, Integer quantity, String notes) {
        this.menuItemId = menuItemId;
        this.quantity = quantity;
        this.notes = notes;
    }
}