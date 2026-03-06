package com.antonsskafferi.staff.network;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    @GET("employees/login")
    Call<EmployeeDto> login(@Query("email") String email);

    @GET("employees")
    Call<List<EmployeeDto>> getAllEmployees();

    @GET("employees/{id}")
    Call<EmployeeDto> getEmployee(@Path("id") Integer id);

    @GET("employees/{id}/shifts")
    Call<List<ShiftDto>> getEmployeeShifts(@Path("id") Integer id);

    @GET("shifts")
    Call<List<ShiftDto>> getAllShifts();

    @POST("swap-requests")
    Call<SwapRequestDto> createSwapRequest(@Body SwapRequestDto dto);

    @GET("swap-requests/incoming")
    Call<List<SwapRequestDto>> getIncomingSwaps(@Query("receiverId") Integer receiverId);

    @GET("swap-requests/outgoing")
    Call<List<SwapRequestDto>> getOutgoingSwaps(@Query("senderId") Integer senderId);

    @PUT("swap-requests/{id}/accept")
    Call<SwapRequestDto> acceptSwap(@Path("id") Integer id);

    @PUT("swap-requests/{id}/reject")
    Call<SwapRequestDto> rejectSwap(@Path("id") Integer id);

    @DELETE("swap-requests/{id}")
    Call<Void> deleteSwapRequest(@Path("id") Integer id);
}
