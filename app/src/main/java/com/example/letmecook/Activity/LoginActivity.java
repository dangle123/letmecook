package com.example.letmecook.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.letmecook.Fragment.NoteFragment;
import com.example.letmecook.Model.User;
import com.example.letmecook.R;
import com.example.letmecook.cache.CachedUserManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    EditText edtUname,edtPword;
    TextView lblForgot;
    private FirebaseAuth mAuth;
    Button btnLogin,btnSignLogin;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        btnLogin = findViewById(R.id.btnLogin);
        edtPword = findViewById(R.id.edtPword);
        edtUname = findViewById(R.id.edtUname);
        btnSignLogin = findViewById(R.id.btnSignLogin);
        lblForgot = findViewById(R.id.textviewForgot);
        SharedPreferences preferences = getSharedPreferences("USER_FILE",MODE_PRIVATE);
        String uName = preferences.getString("USERNAME","");
        String pWord = preferences.getString("PASSWORD","");
        edtUname.setText(uName);
        edtPword.setText(pWord);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser userID = auth.getCurrentUser();

        if (userID != null) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.putExtra("ID_USER", userID);
            startActivity(intent);
            finish();
        } else {

        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();
            }
        });
        btnSignLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignActivity.class);
                startActivity(intent);
                finish();
            }
        });
        lblForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, FotgotActivity.class);
                startActivity(intent);
                finish();
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

        edtUname.addTextChangedListener(textWatcher);
        edtPword.addTextChangedListener(textWatcher);
    }

    private void checkFields() {
        String username = edtUname.getText().toString().trim();
        String password = edtPword.getText().toString().trim();

        if (!username.isEmpty() && !password.isEmpty()) {
            btnLogin.setEnabled(true);
            btnLogin.setBackgroundColor(Color.parseColor("#FF8B00"));
        } else {
            btnLogin.setEnabled(false);
            btnLogin.setBackgroundColor(Color.parseColor("#E7E8EB"));
        }
    }



    private void checkLogin() {
        String userName = edtUname.getText().toString();
        String password = edtPword.getText().toString();
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập Email và Mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(userName, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();

                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            db.collection("user").document(userId)
                                    .get()
                                    .addOnSuccessListener(snapshot -> {
                                        if (snapshot.exists()) {
                                            User user = snapshot.toObject(User.class);
                                            if (user != null) {
                                                Toast.makeText(this, "Đăng nhập thành công! " + user.getName(), Toast.LENGTH_SHORT).show();
                                                CachedUserManager.saveUser(this, user);
                                                Log.d("FirestoreLogin", "User cached: " + user.getName());
                                                startActivity(new Intent(this, HomeActivity.class));
                                                finish();
                                            }
                                        } else {
                                            Toast.makeText(this, "Không tìm thấy dữ liệu người dùng!", Toast.LENGTH_SHORT).show();
                                            Log.d("FirestoreLogin", "Không tìm thấy document userId: " + userId);
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("FirestoreLogin", "Lỗi tải dữ liệu người dùng", e);
                                        Toast.makeText(this, "Lỗi tải dữ liệu người dùng!", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                        Log.e("FirebaseAuth", "Đăng nhập thất bại: " + task.getException());
                    }
                });


    }
}