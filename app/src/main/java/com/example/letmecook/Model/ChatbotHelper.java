package com.example.letmecook.Model;

import android.util.Log;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatbotHelper {

    private OpenAIService openAIService;

    public ChatbotHelper() {
        openAIService = ApiClient.getClient().create(OpenAIService.class);
    }

    public void sendMessage(String userMessage, ChatbotCallback callback) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("model", "gpt-3.5-turbo");
            jsonObject.put("messages", new JSONArray()
                    .put(new JSONObject().put("role", "system").put("content", "Bạn là một trợ lý chuyên hướng dẫn nấu ăn. Hãy chỉ trả lời các câu hỏi liên quan đến công thức nấu ăn, nguyên liệu, mẹo nấu ăn. Nếu câu hỏi không liên quan đến nấu ăn, hãy yêu cầu thêm tiền ."))
                    .put(new JSONObject().put("role", "user").put("content", userMessage))
            );
            jsonObject.put("max_tokens", 1000);

            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

            Call<ResponseBody> call = openAIService.sendMessage(body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String responseBody = response.body().string();
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            String reply = jsonResponse.getJSONArray("choices")
                                    .getJSONObject(0)
                                    .getJSONObject("message")
                                    .getString("content");
                            callback.onSuccess(reply);


                        } catch (Exception e) {

                            callback.onFailure(e.getMessage());

                        }
                    } else {
                        Log.d("OpenAI Request", jsonObject.toString());
                        try {
                            Log.e("OpenAI Error", "Lỗi phản hồi: " + response.errorBody().string());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }


                        callback.onFailure("Lỗi phản hồi từ OpenAI");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    callback.onFailure(t.getMessage());
                }
            });

        } catch (JSONException e) {
            callback.onFailure(e.getMessage());
        }
    }

    public interface ChatbotCallback {
        void onSuccess(String response);
        void onFailure(String error);
    }
}
