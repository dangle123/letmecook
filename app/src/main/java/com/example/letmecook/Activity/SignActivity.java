package com.example.letmecook.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.letmecook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
   Button btnSign;
   TextView textviewLogin;

    public  FirebaseFirestore db;
   EditText edtUnameSign,edtPword1,edtPword2;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign);
        btnSign = findViewById(R.id.btnSign);
        edtUnameSign = findViewById(R.id.edtUnameSign);
        edtPword1 = findViewById(R.id.edtPword1);
        edtPword2 = findViewById(R.id.edtPword2);
        mAuth = FirebaseAuth.getInstance();
        textviewLogin = findViewById(R.id.textViewLogin);

        textviewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignActivity.this, UpdateProfile.class);
                startActivity(intent);
                finish();
            }
        });
        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSign.setEnabled(false); // Vô hiệu hóa

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    btnSign.setEnabled(true); // Bật lại sau 3 giây
                }, 3000);
                checkPassword();
            }
        });

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

        edtPword1.addTextChangedListener(textWatcher);
        edtPword2.addTextChangedListener(textWatcher);
        edtUnameSign.addTextChangedListener(textWatcher);

    }

    private void checkFields() {
        String username = edtUnameSign.getText().toString().trim();
        String password1 = edtPword1.getText().toString().trim();
        String password2 = edtPword2.getText().toString().trim();
        if (!username.isEmpty() && !password1.isEmpty() && !password2.isEmpty()) {
            btnSign.setEnabled(true);
            btnSign.setBackgroundColor(Color.parseColor("#FF8B00"));
        } else {
            btnSign.setEnabled(false);
            btnSign.setBackgroundColor(Color.parseColor("#E7E8EB"));
        }
    }
    private  void  checkPassword(){
        String password1 = edtPword1.getText().toString().trim();
        String password2 = edtPword2.getText().toString().trim();
        if (password1.equals(password2)){
            CheckSign();
        } else {
            Toast.makeText(SignActivity.this, "Mật khẩu chưa khớp.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void CheckSign() {
        String userName = edtUnameSign.getText().toString();
        String password = edtPword1.getText().toString();
        mAuth.createUserWithEmailAndPassword(userName, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            user.reload().addOnCompleteListener(reloadTask -> {
                                FirebaseUser reloadedUser = mAuth.getCurrentUser();
                                if (reloadedUser != null) {
                                    String userId = reloadedUser.getUid();
                                    Log.d("checkSign", userId);
                                    SenRequest(reloadedUser);
                                    creatChat(reloadedUser);
                                }
                            });
                        }
                    } else {
                        Log.e("Auth", "Đăng ký thất bại: ", task.getException());
                    }
                });

    }
    private void creatChat(FirebaseUser user) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String email = edtUnameSign.getText().toString();

        String userId = user.getUid();
        db = FirebaseFirestore.getInstance();
        Map<String, Object> SenRequestUser = new HashMap<>();

        SenRequestUser.put("userId", userId);

        SenRequestUser.put("messages", new ArrayList<String>());

        db.collection("conversations")
                .add(SenRequestUser)
                .addOnSuccessListener(documentReference -> {
                    String conversationId = documentReference.getId();
                    Log.d("Firestore", "Tạo chat thành công với ID: " + conversationId);

//                    saveConversationIdToUser(userId, conversationId);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Lỗi khi tạo chat", e);
                    Toast.makeText(SignActivity.this, "Tạo chat thất bại", Toast.LENGTH_SHORT).show();
                });

    }

    private void SenRequest(FirebaseUser user) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String email = edtUnameSign.getText().toString();


        if (user == null) {
            Toast.makeText(this, "Lỗi xác thực, vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        db = FirebaseFirestore.getInstance();
        Map<String, Object> SenRequestUser = new HashMap<>();
        SenRequestUser.put("avata", "https://i.pinimg.com/736x/b1/8a/4a/b18a4ac5454c2e3d8d0b519f3b84dcb6.jpg");
        SenRequestUser.put("birth", "");
        SenRequestUser.put("email", email);
        SenRequestUser.put("userId", userId);
        SenRequestUser.put("favorites", new ArrayList<String>());
        SenRequestUser.put("like", new ArrayList<String>());
        SenRequestUser.put("notified", new ArrayList<String>());
        SenRequestUser.put("imageUrl", "https://i.pinimg.com/736x/b1/8a/4a/b18a4ac5454c2e3d8d0b519f3b84dcb6.jpg");
        SenRequestUser.put("note", "0");
        SenRequestUser.put("lv", "0");
        SenRequestUser.put("name", "");
        SenRequestUser.put("coin", 100);
        SenRequestUser.put("timestamp", System.currentTimeMillis());

        db.collection("user").document(userId).set(SenRequestUser)
                .addOnSuccessListener(aVoid -> {
                    FirebaseUser reloadedUser = mAuth.getCurrentUser();
                    if (reloadedUser != null) {
                        intentOk(reloadedUser);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Lỗi khi thêm dữ liệu!", e);
                    Toast.makeText(SignActivity.this, "Lỗi khi gửi yêu cầu!", Toast.LENGTH_SHORT).show();
                });

    }

    private void intentOk(FirebaseUser user) {

        String userId = user.getUid();
        Log.d("IntentOK", "Chuyển sang UpdateProfile với userId = " + userId);
        Toast.makeText(getApplicationContext(), ((FirebaseUser) user).getEmail(), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(SignActivity.this,UpdateProfileActivity.class);
        intent.putExtra("ID",userId);
        intent.putExtra("AVATA","https://i.pinimg.com/736x/b1/8a/4a/b18a4ac5454c2e3d8d0b519f3b84dcb6.jpg");
        intent.putExtra("MAIL",user.getEmail());
        intent.putExtra("TYPE",1);
        startActivity(intent);
        finish();
    }

}