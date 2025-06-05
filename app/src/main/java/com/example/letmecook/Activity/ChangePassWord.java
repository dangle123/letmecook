package com.example.letmecook.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class ChangePassWord extends AppCompatActivity {

    public TextView btnChange,edtPass,edtAgain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_pass_word);

        btnChange = findViewById(R.id.btnChange);
        edtPass = findViewById(R.id.edtChage);
        edtAgain = findViewById(R.id.edtagain);


        edtAgain.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                String inputText1 = edtAgain.getText().toString().trim();
                int length1 = inputText1.length();

                String inputText2 = edtPass.getText().toString().trim();
                int length2 = inputText2.length();


                String text1 = edtAgain.getText().toString().trim();
                String text2 = edtPass.getText().toString().trim();
                boolean isValid = !text1.isEmpty() && !text2.isEmpty() && text1.length() >= 6 && text2.length() >= 6;

                btnChange.setEnabled(isValid);
                btnChange.setBackgroundColor(Color.parseColor(isValid ? "#FF8B00" : "#E7E8EB"));
            }


            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}


            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });


        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( (edtAgain.getText().toString().equals(edtPass.getText().toString()))){
                    String newPassword = edtAgain.toString().trim();
                    Changepassword(newPassword);
                } else {
                    Toast.makeText(v.getContext(), "Sai mật khẩu", Toast.LENGTH_SHORT).show();
                }

            }

            private void Changepassword(String newPassword) {

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if (user != null) {
                        user.updatePassword(newPassword)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        System.out.println("Mật khẩu đã được thay đổi thành công!");
                                    } else {
                                        System.out.println("Thay đổi mật khẩu thất bại: " + task.getException().getMessage());
                                    }
                                });
                    } else {
                        System.out.println("Người dùng chưa đăng nhập!");
                    }

            }
        });

    }
}