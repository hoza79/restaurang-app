package com.antonsskafferi.android_ordertablet.net;

import java.util.List;

public class KitchenBatchDto {
    public Integer batchId;
    public String batchType;
    public String batchStatus;
    public String createdAt;
    public Integer tableNumber;
    public List<KitchenItemDto> items;
}