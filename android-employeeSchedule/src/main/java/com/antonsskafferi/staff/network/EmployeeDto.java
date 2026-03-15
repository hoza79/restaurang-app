package com.antonsskafferi.staff.network;

import java.util.List;

public class EmployeeDto {
    public Integer employeeId;
    public String firstName;
    public String lastName;
    public EmployeeRole role;
    public String phoneNumber;
    public String emailAddress;
    public List<ShiftDto> shifts;

    public EmployeeDto() {}
}
