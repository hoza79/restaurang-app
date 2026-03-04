package com.antonsskafferi.android_ordertablet.net;

public class CreateOrderRequest {
    public Integer employeeId;
    public Integer tableId;

    public CreateOrderRequest(Integer employeeId, Integer tableId) {
        this.employeeId = employeeId;
        this.tableId = tableId;
    }
}