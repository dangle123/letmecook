package com.example.letmecook.Model;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface OpenAIService {

    @Headers({
            "Content-Type: application/json",
            "Authorization: Bearer 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"
    })
    @POST("v1/chat/completions")
    Call<ResponseBody> sendMessage(@Body RequestBody body);
}
