package com.antonsskafferi.android_ordertablet.net;

import java.util.List;

public class CreateBatchRequest {
    public String batchType; // "DRINK", "APPETIZER", "MAIN_COURSE", "DESSERT"
    public List<CreateBatchItemRequest> items;

    public CreateBatchRequest(String batchType, List<CreateBatchItemRequest> items) {
        this.batchType = batchType;
        this.items = items;
    }
}