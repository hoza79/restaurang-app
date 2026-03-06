package com.antonsskafferi.staff.network;

public class ShiftDto {
    public Integer shiftId;
    public Integer employeeId;
    public String startTime;
    public String endTime;
    public ShiftStatus shiftStatus;

    public ShiftDto() {}

    public ShiftDto(Integer shiftId, Integer employeeId, String startTime, String endTime, ShiftStatus shiftStatus) {
        this.shiftId = shiftId;
        this.employeeId = employeeId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.shiftStatus = shiftStatus;
    }
}
