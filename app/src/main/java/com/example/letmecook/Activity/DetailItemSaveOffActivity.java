package com.example.letmecook.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.letmecook.R;
import com.example.letmecook.adapter.BuocNauAdapter;

import com.example.letmecook.adapter.NguyenLieuAdapter;

import com.example.letmecook.adapter.TipsAdapter;
import com.example.letmecook.cache.AppDatabase;
import com.example.letmecook.cache.RecipeEntity;

import java.util.ArrayList;

public class DetailItemSaveOffActivity extends AppCompatActivity {

    private TextView tvTenMonAn;
    private ImageView imgRecipe;
    private RecyclerView recyclerViewNguyenLieu, recyclerViewBuocNau, recyclerViewTips;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_item_save_off);

        tvTenMonAn = findViewById(R.id.tvTenMonAn);
        imgRecipe = findViewById(R.id.imgRecipe);
        recyclerViewNguyenLieu = findViewById(R.id.recyclerViewNguyenLieu);
        recyclerViewBuocNau = findViewById(R.id.recyclerViewBuocNau);
        recyclerViewTips = findViewById(R.id.recyclerViewTips);

        db = AppDatabase.getInstance(this);

        Intent intent = getIntent();
        tvTenMonAn.setText(intent.getStringExtra("NAME"));
        String recipeId = intent.getStringExtra("ID");

        loadRecipeDetail(recipeId);
    }

    private void loadRecipeDetail(String recipeId) {
        new Thread(() -> {
            RecipeEntity recipe = db.recipeDao().getRecipeById(recipeId);
            runOnUiThread(() -> {
                if (recipe != null) {
                    tvTenMonAn.setText(recipe.title);

                    Glide.with(this)
                            .load(recipe.imageUrl)
                            .placeholder(R.drawable.placeholder)
                            .into(imgRecipe);


                    recyclerViewNguyenLieu.setLayoutManager(new LinearLayoutManager(this));
                    recyclerViewNguyenLieu.setAdapter(new NguyenLieuAdapter(new ArrayList<>(recipe.ingredients)));



                    recyclerViewBuocNau.setLayoutManager(new LinearLayoutManager(this));
                    recyclerViewBuocNau.setAdapter(new BuocNauAdapter(new ArrayList<>(recipe.tips)));


                    recyclerViewTips.setLayoutManager(new LinearLayoutManager(this));
                    recyclerViewTips.setAdapter(new TipsAdapter(new ArrayList<>(recipe.instructions)));

                } else {
                    Toast.makeText(this, "Không tìm thấy công thức!", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}
