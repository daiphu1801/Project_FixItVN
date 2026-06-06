package com.fixit.feature.upload.data.remote.api;

import com.fixit.feature.upload.data.remote.dto.response.CloudinaryUploadResponse;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Url;

public interface CloudinaryUploadApi {

    @Multipart
    @POST
    Call<CloudinaryUploadResponse> uploadImage(
            @Url String uploadUrl,
            @PartMap Map<String, RequestBody> formData,
            @Part MultipartBody.Part file
    );
}