package com.example.letmecook.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.letmecook.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SendRequest extends AppCompatActivity {
    public TextView btnSen, tvContent, tvTitle;
    public  FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_send_request);

        btnSen = findViewById(R.id.btnSendRequset);
        tvContent = findViewById(R.id.tvContent);
        tvTitle = findViewById(R.id.tvTitle);

        btnSen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = tvTitle.getText().toString().trim();
                String content = tvContent.getText().toString().trim();
                SenRequest(title,content);
               finish();
            }

            private void SenRequest(String title, String content) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                String userId = user.getUid();
                db = FirebaseFirestore.getInstance();
                Map<String, Object> SenRequestData = new HashMap<>();
                SenRequestData.put("title", title);
                SenRequestData.put("decrible", content);
                SenRequestData.put("userid", userId);
                SenRequestData.put("type","0");
                SenRequestData.put("status","0");
                SenRequestData.put("timestamp", System.currentTimeMillis());

                db.collection("notification").add(SenRequestData).addOnSuccessListener(documentReference ->
                        {  Log.d("Firestore", "Thêm thành công với ID: " + documentReference.getId());
                            Toast.makeText(SendRequest.this,"Gửi yêu cầu thành công!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {

                            Log.e("Firestore", "Lỗi khi thêm dữ liệu!", e);
                            Toast.makeText(SendRequest.this, "Lỗi khi gửi yêu cầu!", Toast.LENGTH_SHORT).show();
                        });
            }


        });
    }
}