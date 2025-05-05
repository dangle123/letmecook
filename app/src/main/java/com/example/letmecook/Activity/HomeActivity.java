package com.example.letmecook.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.widget.ListView;
import android.widget.ViewFlipper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letmecook.Fragment.BookFragment;
import com.example.letmecook.Fragment.HomeProductFragment;
import com.example.letmecook.Fragment.NoteFragment;
import com.example.letmecook.Fragment.NotificationFragment;
import com.example.letmecook.Fragment.ProfileFragment;
import com.example.letmecook.Model.DanhSachMonAn;
import com.example.letmecook.Model.LoaiMonAn;
import com.example.letmecook.R;
import com.example.letmecook.adapter.ListMonAnAdapter;
import com.example.letmecook.adapter.LoaiMonAnAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.List;
import android.widget.ImageView;
import android.view.MotionEvent;
import android.view.View;
import android.content.Intent;

public class HomeActivity extends AppCompatActivity {

    Toolbar toolbar;
    ViewFlipper viewFlipper;

    NavigationView navigationView;
    ListView listviewmanhinhchinh;

    private RecyclerView recyclerView, recyclerViewLoadmonan;
    private LoaiMonAnAdapter adapter;
    private List<LoaiMonAn> loaiMonAnList;
    private List<Long> favorites;
    private FirebaseFirestore db;

    private ListMonAnAdapter monAnAdapter;
    private List<DanhSachMonAn> DanhSachMonAn = new ArrayList<>();
    private boolean isActive = true;
    BottomNavigationView bottomNavigationView;

    private ImageView chatHeadIcon;
    private float dX, dY;
    private float startX, startY;
    private boolean isDragging = false;
    private int lastAction;
    private GestureDetector gestureDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        chatHeadIcon = findViewById(R.id.chatHeadIcon);
        chatHeadIcon.bringToFront();
        chatHeadIcon.setTranslationZ(100f);


        chatHeadIcon.setClickable(true);
        chatHeadIcon.setFocusable(true);


        chatHeadIcon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {

                    case MotionEvent.ACTION_DOWN:
                        dX = v.getX() - event.getRawX();
                        dY = v.getY() - event.getRawY();
                        startX = v.getX();
                        startY = v.getY();
                        isDragging = false;
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        v.setX(event.getRawX() + dX);
                        v.setY(event.getRawY() + dY);
                        isDragging = true;
                        return true;

                    case MotionEvent.ACTION_UP:
                    float endX = v.getX();
                    float endY = v.getY();
                    if (Math.abs(endX - startX) < 5 && Math.abs(endY - startY) < 5) {
                        Intent intent = new Intent(HomeActivity.this, TestActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        v.getContext().startActivity(intent);
                    }
                    return true;


                }
                return false;
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new HomeProductFragment())
                    .commit();
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
        bottomNavigationView.setOnItemSelectedListener(item -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_home && !(currentFragment instanceof HomeProductFragment)) {
                selectedFragment = new HomeProductFragment();
            } else if (item.getItemId() == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();

            } else if (item.getItemId() == R.id.nav_book && !(currentFragment instanceof BookFragment)) {
                selectedFragment = new BookFragment();
            } else if (item.getItemId() == R.id.nav_note && !(currentFragment instanceof NoteFragment)) {
                selectedFragment = new NoteFragment();
            }else if (item.getItemId() == R.id.nav_noti && !(currentFragment instanceof NotificationFragment)) {
                selectedFragment = new NotificationFragment();
            }
            if (selectedFragment != null) {
                for (Fragment fragment : fragmentManager.getFragments()) {
                    transaction.remove(fragment);
                }
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.commitNowAllowingStateLoss();
            }
            return true;
        });


    }
}