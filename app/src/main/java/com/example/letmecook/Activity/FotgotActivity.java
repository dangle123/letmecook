package com.example.letmecook.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.letmecook.R;

public class FotgotActivity extends AppCompatActivity {
    TextView textviewLoginForgot;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fotgot);
       textviewLoginForgot = findViewById(R.id.textviewLoginForgot);
       textviewLoginForgot.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(FotgotActivity.this,LoginActivity.class);
               startActivity(intent);
               finish();
           }
       });
    }
}