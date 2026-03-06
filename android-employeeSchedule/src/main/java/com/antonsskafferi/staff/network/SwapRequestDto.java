package com.antonsskafferi.staff.network;

public class SwapRequestDto {
    public Integer swapId;
    public Integer senderId;
    public Integer receiverId;
    public Integer shiftId;
    public SwapStatus swapStatus;

    public SwapRequestDto() {}

    public SwapRequestDto(Integer swapId, Integer senderId, Integer receiverId, Integer shiftId, SwapStatus swapStatus) {
        this.swapId = swapId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.shiftId = shiftId;
        this.swapStatus = swapStatus;
    }
}
