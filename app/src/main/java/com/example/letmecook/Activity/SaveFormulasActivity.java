package com.example.letmecook.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letmecook.R;
import com.example.letmecook.adapter.OfflineRecipeAdapter;
import com.example.letmecook.cache.AppDatabase;
import com.example.letmecook.cache.RecipeEntity;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SaveFormulasActivity extends AppCompatActivity {

    private RecyclerView recyclerSave;
    private ImageView imageNodata;
    private OfflineRecipeAdapter adapter;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_save_formulas);
        recyclerSave = findViewById(R.id.recyclerSave);
        recyclerSave.setLayoutManager(new LinearLayoutManager(this));
        imageNodata = findViewById(R.id.imageNodata);
        db = AppDatabase.getInstance(this);

        // 🔹 Khởi tạo adapter với list trống
        adapter = new OfflineRecipeAdapter(this, new ArrayList<>(), db);
        recyclerSave.setAdapter(adapter);

        // 🔹 Bắt sự kiện click vào item
        adapter.setOnItemClickListener(recipe -> {
            Intent intent = new Intent(this, DetailItemSaveOffActivity.class);
            intent.putExtra("ID",recipe.id);
            intent.putExtra("NAME",recipe.title);
            startActivity(intent);
        });

        // 🔹 Sau đó mới load dữ liệu thật
        loadOfflineRecipes();
    }

    private void loadOfflineRecipes() {
        io.reactivex.rxjava3.core.Single.fromCallable(() -> db.recipeDao().getAllRecipes())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(recipes -> {
                    if (recipes.isEmpty()) {
                        imageNodata.setVisibility(View.VISIBLE);
                    } else {
                        adapter.setData(recipes);
                        imageNodata.setVisibility(View.GONE);
                    }
                }, throwable ->
                        Toast.makeText(this, "Lỗi tải dữ liệu offline!", Toast.LENGTH_SHORT).show());
    }
}
