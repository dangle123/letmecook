package com.example.letmecook.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

public class SignActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
   Button btnSign;
   TextView textviewLogin;
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
                Intent intent = new Intent(SignActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        mAuth.createUserWithEmailAndPassword(userName,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
            {
                Log.d("Main", "createUserWithEmail:success");
                FirebaseUser user = mAuth.getCurrentUser();

                Toast.makeText(getApplicationContext(), ((FirebaseUser) user).getEmail(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SignActivity.this,LoginActivity.class);
                startActivity(intent);

                finish();
            } else {
                Log.w("Main", "createUserWithEmail:failure", task.getException());
                String logCat = String.valueOf(task.getException());
                    String result = logCat.substring(60, 115);
                if (logCat.equals("The email address is already in use by another account")){
                    Toast.makeText(SignActivity.this, "Tài khoản đã tồn tại",
                            Toast.LENGTH_SHORT).show();
                } else
                Toast.makeText(SignActivity.this, "Đăng ký thất bại",
                        Toast.LENGTH_SHORT).show();
            }
            }

        });

    }


}