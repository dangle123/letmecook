package com.example.letmecook.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateProfile extends AppCompatActivity {

    public TextView btnUpdate;
    public  FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userId;
    public EditText edtUserName,edtEmail,edtBirth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_profile);
//        btnUpdate = findViewById(R.id.btnUpdate);
//        edtBirth = findViewById(R.id.edtBirth);
//        edtUserName = findViewById(R.id.edtUserName);
//        edtEmail = findViewById(R.id.edtEmail);


         userId = getIntent().getStringExtra("userId");
         Log.d("id user","id user la" + userId);


        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFields();
            }



            @Override
            public void afterTextChanged(Editable s) {}
        };

        edtUserName.addTextChangedListener(textWatcher);
        edtBirth.addTextChangedListener(textWatcher);
        edtEmail.addTextChangedListener(textWatcher);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    SenRequest();

            }
        });



    }

    private void checkFields() {
        String username = edtUserName.getText().toString().trim();
        String Birth = edtBirth.getText().toString().trim();
        String Email = edtEmail.getText().toString().trim();

        if (!username.isEmpty() && !Birth.isEmpty() && !Email.isEmpty()) {
            btnUpdate.setEnabled(true);
            btnUpdate.setBackgroundColor(Color.parseColor("#FF8B00"));
        } else {
            btnUpdate.setEnabled(false);
            btnUpdate.setBackgroundColor(Color.parseColor("#E7E8EB"));
        }
    }

    private void SenRequest() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String email = edtEmail.getText().toString();
        String birth = edtBirth.getText().toString();
        String userName = edtUserName.getText().toString();
        FirebaseUser user = mAuth.getCurrentUser();

        db = FirebaseFirestore.getInstance();
        Map<String, Object> SenRequestUser = new HashMap<>();
        SenRequestUser.put("avata", "https://i.pinimg.com/736x/b1/8a/4a/b18a4ac5454c2e3d8d0b519f3b84dcb6.jpg");
        SenRequestUser.put("birth", birth);
        SenRequestUser.put("e-mail", email);
        SenRequestUser.put("favorites", new ArrayList<String>());
        SenRequestUser.put("like", new ArrayList<String>());
        SenRequestUser.put("notified", new ArrayList<String>());
        SenRequestUser.put("lv", "0");
        SenRequestUser.put("name", userName);
        SenRequestUser.put("coin", 100);
        SenRequestUser.put("note", "100");
        SenRequestUser.put("timestamp", System.currentTimeMillis());

        db.collection("user").document(userId)
                .set(SenRequestUser)
                .addOnSuccessListener(unused -> {
                    Log.d("Firestore", "Thêm thành công với ID: " + userId);
                    intentOk();
                    Toast.makeText(UpdateProfile.this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Lỗi khi thêm dữ liệu!", e);
                    Toast.makeText(UpdateProfile.this, "Lỗi khi gửi yêu cầu!", Toast.LENGTH_SHORT).show();
                });
    }

    private void intentOk() {

        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(UpdateProfile.this,LoginActivity.class);

        startActivity(intent);

        finish();
    }


}