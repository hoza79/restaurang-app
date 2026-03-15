package com.antonsskafferi.android_ordertablet.net;

import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RestaurantApi {
    @GET("tables")
    Call<List<DiningTableDto>> getTables();

    @GET("menu")
    Call<MenuDto> getMenu();

    @GET("kitchen/batches")
    Call<List<KitchenBatchDto>> getKitchenBatches();

    @PUT("kitchen/batches/{id}/complete")
    Call<Map<String, Object>> completeKitchenBatch(@Path("id") int batchId);

    @POST("orders/pay")
    Call<Map<String, Object>> payOrder(@Body PayRequest body);
    @POST("orders")
    Call<Map<String, Object>> createOrder(@Body CreateOrderRequest body);

    @POST("orders/{orderId}/batches")
    Call<Map<String, Object>> createBatch(
            @Path("orderId") int orderId,
            @Body CreateBatchRequest body
    );
    @retrofit2.http.GET("bookings")
    retrofit2.Call<java.util.List<com.antonsskafferi.android_ordertablet.net.BookingDto>> getBookings();
}