package com.example.letmecook.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.letmecook.R;

public class AllItemActivity extends AppCompatActivity {
    private TextView tvTheloai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all_item);


        Intent intent = getIntent();
        if (intent != null) {
           String categories_id =  intent.getStringExtra("Tenloai");

           tvTheloai = findViewById(R.id.tvViewAll);
           tvTheloai.setText(categories_id);
        }


    }
}