package com.antonsskafferi.android_ordertablet.net;

public class PayRequest {
    public Integer tableId;
    public String method;

    public PayRequest(Integer tableId, String method) {
        this.tableId = tableId;
        this.method = method;
    }
}