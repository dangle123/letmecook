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
import com.example.letmecook.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                        if (user != null) {
                            String userId = user.getUid();

                            Log.d("Firebase", "User ID: " + userId);
                        }
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}