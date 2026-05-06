package com.fixit.data.remote.api;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("ping")
    Call<String> pingServer();
}
