package com.antonsskafferi.android_ordertablet.net;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {

    private static final String BASE_URL = "http://10.0.2.2:8080/backend/api/";

    // Emulator -> your PC localhost:
    //private static final String BASE_URL = "http://192.168.0.205:8080/backend/api/";

    // If testing on a physical phone on same Wi-Fi, use:
    // private static final String BASE_URL = "http://192.168.x.y:8080/backend/api/";

    private static RestaurantApi api;

    private ApiClient() {}

    public static RestaurantApi api() {
        if (api == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            api = retrofit.create(RestaurantApi.class);
        }
        return api;
    }
}