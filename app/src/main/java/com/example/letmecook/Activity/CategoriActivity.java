package com.example.letmecook.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letmecook.Model.DanhSachMonAn;
import com.example.letmecook.Model.LoaiMonAn;
import com.example.letmecook.R;
import com.example.letmecook.adapter.CategoriAdapter;
import com.example.letmecook.adapter.ListMonAnAdapter;
import com.example.letmecook.adapter.LoaiMonAnAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class CategoriActivity extends AppCompatActivity {
    private RecyclerView recyclerView,recyclerViewLoadmonan;
    private CategoriAdapter adapter;
    private List<LoaiMonAn> loaiMonAnList;
    private ListMonAnAdapter monAnAdapter;
    private List<DanhSachMonAn> DanhSachMonAn = new ArrayList<>();
    private FirebaseFirestore db;
    private List<Long> favoritesUser = new ArrayList<>();
    private List<Long> LikeUser = new ArrayList<>();
    private String categoryId ;
    private boolean checkLike = false;
    private  boolean checkLove = false;
    private int selectedCategoryId = -1;
    private int selectedPosition = -1;

    private ImageView edtSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_categori);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().setStatusBarColor(Color.parseColor("#EE7600"));

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        userDetail(user.getUid());

        Intent intent = getIntent();
        if (intent != null) {
            categoryId =  intent.getStringExtra("theloai");
            selectedCategoryId = Integer.parseInt(intent.getStringExtra("theloai"));
            selectedPosition = Integer.parseInt(intent.getStringExtra("theloai"));
        }


        recyclerView = findViewById(R.id.recyclervewItemCategori);
        recyclerViewLoadmonan = findViewById(R.id.recyclerViewCategori);
        edtSearch = findViewById(R.id.edtSearch);
        loaiMonAnList = new ArrayList<>();


        recyclerViewLoadmonan.setLayoutManager(new LinearLayoutManager(this));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        adapter = new CategoriAdapter(this, loaiMonAnList,selectedPosition );
        recyclerViewLoadmonan.setLayoutManager(layoutManager);
        recyclerViewLoadmonan.setAdapter(adapter);

        loadLoaimonan();


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        LinearLayoutManager layoutLoadMonAnManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        monAnAdapter = new ListMonAnAdapter(this, DanhSachMonAn);
        recyclerView.setAdapter(monAnAdapter);

        loadMonAn(categoryId);

        edtSearch.setOnClickListener(v -> {
            Intent intentSearch = new Intent(this, SearchActivity.class);
            startActivity(intentSearch);
        });
        monAnAdapter.setOnItemClickListener(new ListMonAnAdapter.OnItemClickListener() {

            public void onItemClick(DanhSachMonAn DanhSachMonAn,int position) {
                Intent intent = new Intent(CategoriActivity.this, DetailItem.class);
                intent.putExtra("TEN_MON_AN", DanhSachMonAn.getTen());
                intent.putExtra("HINH_ANH", DanhSachMonAn.getHinhAnh());
                intent.putExtra("ID",DanhSachMonAn.getId());
                intent.putExtra("VIDEO",DanhSachMonAn.getVideo());

                startActivity(intent);
            }
        });
        adapter.setOnItemClickListener(new CategoriAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int categoryId) {
                String categori = String.valueOf(categoryId);
                selectedCategoryId = categoryId;
                loadMonAn(categori);

            }
        });

    }
    private void setupRecyclerView(int selectedPosition) {
        recyclerViewLoadmonan.setLayoutManager(new LinearLayoutManager(this));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        adapter = new CategoriAdapter(this, loaiMonAnList,selectedPosition );
        recyclerViewLoadmonan.setLayoutManager(layoutManager);
        recyclerViewLoadmonan.setAdapter(adapter);
    }
    private void userDetail(String userId) {

        db = FirebaseFirestore.getInstance();
        db.collection("user").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        favoritesUser = (List<Long>) documentSnapshot.get("favorites");
                        LikeUser = (List<Long>) documentSnapshot.get("like");
                    } else {

                    }
                })
                .addOnFailureListener(e -> Log.e("UserFavorites", "Lỗi khi lấy dữ liệu", e));

    }

    private void loadMonAn(String categoryId) {
        Query query = db.collection("recipes ");
        if (selectedCategoryId != -1) {
            query = query.whereEqualTo("categories_id", categoryId);
        }

        query.get()
       .addOnSuccessListener(queryDocumentSnapshots -> {
            DanhSachMonAn.clear();
            for (DocumentSnapshot document : queryDocumentSnapshots) {
                String id = document.getString("id");
                String ten = document.getString("title");
                String hinhAnh = document.getString("url");

                String categories_id = document.getString("categories_id");
                String video = document.getString("video");
                ArrayList<String> nguyenlieu = (ArrayList<String>) document.get("ingredients");
                ArrayList<String> buocnau = (ArrayList<String>) document.get("instructions");
                ArrayList<String> tips = (ArrayList<String>) document.get("tips");
                Long userView = document.getLong("view");
                Integer userViewInt = userView != null ? userView.intValue() : 0;

                Long userLike = document.getLong("like");
                Integer userLikeInt = userLike != null ? userLike.intValue() : 0;

                Long userCoin = document.getLong("coin");
                Integer userCoinInt = userCoin != null ? userCoin.intValue() : 0;

                String timeCook = document.getString("cooking_time");
                for (int i= 0; i < favoritesUser.size();i++){
                    String tam = String.valueOf(favoritesUser.get(i));
                    if ( tam.equals(categories_id)){
                        checkLike = true;
                        break;
                    } else {
                        checkLike = false;
                    }
                }

                for (int i = 0; i < LikeUser.size();i++){
                    String tam = String.valueOf(LikeUser.get(i));
                    if (tam.equals(categories_id)){
                        checkLove = true;
                        break;
                    }else {
                        checkLove = false;
                    }
                }

                DanhSachMonAn monAn = new DanhSachMonAn(id,ten,hinhAnh,video,categories_id,nguyenlieu,tips,buocnau,checkLike,userCoinInt,userViewInt,userLikeInt,timeCook,checkLove);
                DanhSachMonAn.add(monAn);

            }
            monAnAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> Log.e("Firebase", "Lỗi tải dữ liệu", e));
    }

    private void loadLoaimonan() {
        db = FirebaseFirestore.getInstance();
        db.collection("categories ")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    loaiMonAnList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String id = document.getId();
                        String ten = document.getString("name");
                        String url = document.getString("url");

                        LoaiMonAn loaiMonAn = new LoaiMonAn(id, ten, url);
                        loaiMonAnList.add(loaiMonAn);
                    }
                    adapter.notifyDataSetChanged();

                })
                .addOnFailureListener(e -> {

                });
    }
}