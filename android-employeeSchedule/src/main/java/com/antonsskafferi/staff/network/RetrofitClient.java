package com.antonsskafferi.staff.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // VIKTIGT: 10.0.2.2 är "localhost" för Android-emulatorn.
    //private static final String BASE_URL = "http://10.0.2.2:8080/backend/api/";

    // If testing on a physical phone on same Wi-Fi, use:
    private static final String BASE_URL = "http://192.168.1.58:8080/backend/api/";

    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
