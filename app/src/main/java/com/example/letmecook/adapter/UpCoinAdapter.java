package com.example.letmecook.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.letmecook.Activity.SendRequest;
import com.example.letmecook.Model.UpCoin;
import com.example.letmecook.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class UpCoinAdapter extends RecyclerView.Adapter<UpCoinAdapter.ViewHolder> {
    public FirebaseFirestore db;
    public Context context;
    public List<UpCoin> upCoins;

    public UpCoinAdapter(Context context, List<UpCoin> upCoins)
    {
        this.context = context;
        this.upCoins = upCoins;
    }


    @NonNull
    @Override
    public UpCoinAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_up_coin,parent,false);

        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        UpCoin item = upCoins.get(position);
        holder.tvCoin.setText(String.valueOf(item.getCoin()));
        holder.tvValue.setText(String.valueOf(item.getValue()));

        View popupView = LayoutInflater.from(context).inflate(R.layout.pop_up_coin, null);
        TextView tvPopupCoin = popupView.findViewById(R.id.tvPopupCoin);
        TextView tvPopupValue = popupView.findViewById(R.id.tvPopupValue);
        TextView btnClose = popupView.findViewById(R.id.btnClosePopup);
        LinearLayout linearLayout = popupView.findViewById(R.id.linearLayout13);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imageCheck.setImageResource(R.drawable.uncheck);
                showPopUp(holder.itemView, item);
            }

            private void showPopUp(View itemView, UpCoin item) {


                tvPopupCoin.setText("Coin: " + item.getCoin());
                tvPopupValue.setText("Giá trị: " + item.getValue());

                Animation slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up);
                popupView.startAnimation(slideUp);

                createMomoPayment();

                final PopupWindow popupWindow = new PopupWindow(
                        popupView,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        true
                );


                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        holder.imageCheck.setImageResource(R.drawable.checkun);
                    }
                });

                btnClose.setOnClickListener(v -> {


                    String title = holder.tvValue.getText().toString().trim();
                    String content = holder.tvCoin.getText().toString().trim();

                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userId = user.getUid();
                    db = FirebaseFirestore.getInstance();
                    Map<String, Object> SenRequestData = new HashMap<>();
                    SenRequestData.put("title", title);
                    SenRequestData.put("decrible", content);
                    SenRequestData.put("userid", userId);
                    SenRequestData.put("note","nap xu momo");
                    SenRequestData.put("type","1");
                    SenRequestData.put("status","0");
                    SenRequestData.put("timestamp", System.currentTimeMillis());

                    db.collection("notification").add(SenRequestData).addOnSuccessListener(documentReference ->
                            {  Log.d("Firestore", "Thêm thành công với ID: " + documentReference.getId());
                                String documentID = documentReference.getId();
                                updateNotifi(documentID);
                                Toast.makeText(v.getContext(),"Gửi yêu cầu thành công!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {

                                Log.e("Firestore", "Lỗi khi thêm dữ liệu!", e);
                                Toast.makeText(v.getContext(), "Lỗi khi gửi yêu cầu!", Toast.LENGTH_SHORT).show();
                            });

                    popupWindow.dismiss();
                    holder.imageCheck.setImageResource(R.drawable.checkun);
                });

            }

            private void createMomoPayment() {
                String endpoint = "https://test-payment.momo.vn/v2/gateway/api/create";
                String partnerCode = "MOMOxxxxxx"; // Thay bằng mã của bạn
                String accessKey = "xxxxxxxx"; // Thay bằng AccessKey của bạn
                String secretKey = "xxxxxxxx"; // Thay bằng SecretKey của bạn
                String orderId = UUID.randomUUID().toString();
                String requestId = UUID.randomUUID().toString();
                String amount = "20000";
                String orderInfo = "Nạp xu vào tài khoản";
                String redirectUrl = "yourapp://success";
                String ipnUrl = "https://your-server.com/ipn";

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("partnerCode", partnerCode);
                    jsonObject.put("accessKey", accessKey);
                    jsonObject.put("requestId", requestId);
                    jsonObject.put("amount", amount);
                    jsonObject.put("orderId", orderId);
                    jsonObject.put("orderInfo", orderInfo);
                    jsonObject.put("redirectUrl", redirectUrl);
                    jsonObject.put("ipnUrl", ipnUrl);
                    jsonObject.put("requestType", "captureWallet");

                    String dataToSign = "accessKey=" + accessKey +
                            "&amount=" + amount +
                            "&extraData=" +
                            "&ipnUrl=" + ipnUrl +
                            "&orderId=" + orderId +
                            "&orderInfo=" + orderInfo +
                            "&partnerCode=" + partnerCode +
                            "&redirectUrl=" + redirectUrl +
                            "&requestId=" + requestId +
                            "&requestType=captureWallet";

                    String signature = hmacSHA256(dataToSign, secretKey);
                    jsonObject.put("signature", signature);
                    Log.d("bitmap","signature" + signature);
                    sendMomoRequest(endpoint, jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            private void showQRCode(String qrUrl) {
                ImageView qrImageView = popupView.findViewById(R.id.qrImageView);
                new Thread(() -> {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(new URL(qrUrl).openStream());
                        ((Activity) context).runOnUiThread(() -> {
                             qrImageView.setImageBitmap(bitmap);

                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }

            private String hmacSHA256(String data, String key) {
                try {
                    SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA256");
                    Mac mac = Mac.getInstance("HmacSHA256");
                    mac.init(secretKeySpec);
                    byte[] hmacData = mac.doFinal(data.getBytes());
                    return Base64.encodeToString(hmacData, Base64.NO_WRAP);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            private void sendMomoRequest(String url, JSONObject jsonObject) {
                RequestQueue queue = Volley.newRequestQueue(context);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                        response -> {
                            try {
                                String qrCodeUrl = response.getString("deeplink");

                                showQRCode(qrCodeUrl);
                            } catch (JSONException e) {
                                Log.d("bitmap","qrCodeUrl" +  e);
                                e.printStackTrace();
                            }
                        },
                        error -> Log.e("Momo", "Lỗi kết nối: " + error.toString())
                ) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json");
                        return headers;
                    }
                };

                queue.add(jsonObjectRequest);
            }

            private void updateNotifi(String documentID) {
                db.collection("notification").document(documentID).update("id",documentID);
            }
        });
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageCheck;
        TextView tvValue,tvCoin;
        public ViewHolder(@NonNull View itemView){
            super(itemView);

            tvCoin = itemView.findViewById(R.id.tvcoinValue);
            tvValue = itemView.findViewById(R.id.tvValue);
            imageCheck = itemView.findViewById(R.id.imageCheck);
        }

    }


    @Override
    public int getItemCount() {
        return upCoins.size();
    }
}
