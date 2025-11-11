package com.example.letmecook.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letmecook.Model.DanhSachMonAn;
import com.example.letmecook.R;
import com.example.letmecook.adapter.ListMonAnAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText edtSearch;
    private RecyclerView recyclerView;
    private ArrayList<DanhSachMonAn> listSearch;
    private ListMonAnAdapter adapter;
    private FirebaseFirestore db;
    private List<Long> favoritesUser = new ArrayList<>();
    private List<Long> LikeUser = new ArrayList<>();

    private boolean checkLike = false;
    private  boolean checkLove = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        edtSearch = findViewById(R.id.edtSearchMon);
        recyclerView = findViewById(R.id.recyclerViewSearch);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listSearch = new ArrayList<>();
        adapter = new ListMonAnAdapter(this, listSearch);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString().trim();
                if (!keyword.isEmpty()) {
                    searchData(keyword);
                } else {
                    listSearch.clear();
                    adapter.notifyDataSetChanged();
                }
              }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        adapter.setOnItemClickListener(new ListMonAnAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DanhSachMonAn DanhSachMonAn, int position) {
                    Intent intent = new Intent(SearchActivity.this, DetailItem.class);
                    intent.putExtra("TEN_MON_AN", DanhSachMonAn.getTen());
                    intent.putExtra("HINH_ANH", DanhSachMonAn.getHinhAnh());
                intent.putExtra("VIDEO",DanhSachMonAn.getVideo());
                    intent.putExtra("ID",DanhSachMonAn.getId());

                Log.d("DanhSachMonAn ",DanhSachMonAn.getCategories_id());
                    startActivity(intent);

            }
        });
    }

    private void searchData(String keyword) {{
        listSearch.clear();
        Log.d("FirestoreError keyword :", "FirestoreError keyword" + keyword);
        db.collection("recipes ")
                .whereGreaterThanOrEqualTo("title_lowercase", keyword.toLowerCase())
                .whereLessThanOrEqualTo("title_lowercase", keyword.toLowerCase() + "\uf8ff")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            String id = doc.getString("id");
                            String ten = doc.getString("title");
                            String hinhAnh = doc.getString("url");
                            String video = doc.getString("video");
                            String categories_id = doc.getString("categories_id");

                            ArrayList<String> nguyenlieu = (ArrayList<String>) doc.get("instructions");
                            ArrayList<String> buocnau = (ArrayList<String>) doc.get("instructions");
                            ArrayList<String> tips = (ArrayList<String>) doc.get("tips");
                            Long userView = doc.getLong("view");
                            Integer userViewInt = userView != null ? userView.intValue() : 0;

                            Long userLike = doc.getLong("like");
                            Integer userLikeInt = userLike != null ? userLike.intValue() : 0;

                            Long userCoin = doc.getLong("coin");
                            Integer userCoinInt = userCoin != null ? userCoin.intValue() : 0;
                            String timeCook = doc.getString("cooking_time");

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
                            listSearch.add(monAn);
                           Log.d("FirestoreError ", " Tìm thấy title " + listSearch.get(0).getTen());
                        }
                        adapter.notifyDataSetChanged();

                    } else {
                        Log.d("FirestoreError", "Không tìm thấy kết quả nào");
                    }
                })
                .addOnFailureListener(e ->
                        Log.e("FirestoreSearch", "Lỗi khi tìm kiếm: " + e.getMessage()));
    }

    }
}
