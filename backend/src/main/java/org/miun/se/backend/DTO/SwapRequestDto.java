package org.miun.se.backend.DTO;

public class SwapRequestDto {
    private Long senderId;
    private Long receiverId;
    private Long shiftId;

    public SwapRequestDto() {}

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
    public Long getShiftId() { return shiftId; }
    public void setShiftId(Long shiftId) { this.shiftId = shiftId; }
}