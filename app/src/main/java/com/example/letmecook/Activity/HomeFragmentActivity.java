package com.example.letmecook.Activity;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.letmecook.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class HomeFragmentActivity extends AppCompatActivity {

    ProgressBar progressBar;
    LinearLayout contentLayout;
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_fragment);


    }

}