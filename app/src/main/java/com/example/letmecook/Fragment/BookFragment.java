package com.example.letmecook.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letmecook.Activity.DetailItem;
import com.example.letmecook.Model.DanhSachMonAn;
import com.example.letmecook.Model.LoaiMonAn;
import com.example.letmecook.R;
import com.example.letmecook.adapter.ListMonAnAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class BookFragment extends Fragment {
    RecyclerView recyclerViewBook;
    private FirebaseFirestore db;
    private boolean isLiked = true;
    private ListMonAnAdapter monAnAdapter;
    private ImageView imageNodata;
    private List<Long> favorites = new ArrayList<>();
    private List<DanhSachMonAn> DanhSachMonAn = new ArrayList<>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_book, container, false);

                recyclerViewBook = view.findViewById(R.id.recyclerBook);
        imageNodata = view.findViewById(R.id.imageNodata);
        recyclerViewBook.setLayoutManager(new LinearLayoutManager(getContext()));
        LinearLayoutManager layoutLoadMonAnManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewBook.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        monAnAdapter = new ListMonAnAdapter(getContext(), DanhSachMonAn);
        recyclerViewBook.setAdapter(monAnAdapter);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            favorites(user.getUid());
        }
        favorites(user.getUid());

        imageNodata.setVisibility(view.VISIBLE);

        monAnAdapter.setOnItemClickListener(new ListMonAnAdapter.OnItemClickListener() {

            public void onItemClick(DanhSachMonAn DanhSachMonAn,int position) {
                Log.d("listenner","checkHome" );
                Intent intent = new Intent(requireActivity(), DetailItem.class);
                intent.putExtra("TEN_MON_AN", DanhSachMonAn.getTen());
                intent.putExtra("HINH_ANH", DanhSachMonAn.getHinhAnh());
                intent.putExtra("ID",DanhSachMonAn.getCategories_id());
                intent.putExtra("VIDEO",DanhSachMonAn.getVideo());
                startActivity(intent);
            }
        });

        return view;
    }

    private void favorites(String userId) {

        db = FirebaseFirestore.getInstance();

        db.collection("user").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        favorites = (List<Long>) documentSnapshot.get("favorites");

                        if (favorites != null) {
                            getFavoriteDishes(favorites);
                            Log.d("UserFavorites", "Danh sách món ăn yêu thích: " + favorites);
                        } else {
                            Log.d("UserFavorites", "Người dùng chưa có món ăn yêu thích!");
                        }
                    } else {
                        Log.e("UserFavorites", "Không tìm thấy user!");
                    }
                })
                .addOnFailureListener(e -> Log.e("UserFavorites", "Lỗi khi lấy dữ liệu", e));

    }

    private void getFavoriteDishes(List<Long> favoriteIds) {
        if (favoriteIds == null || favoriteIds.isEmpty()) {
            return;
        }
        db = FirebaseFirestore.getInstance();
        db.collection("recipes ") .whereIn("id", favoriteIds)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        imageNodata.setVisibility(View.GONE);
                        DanhSachMonAn.clear();

                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            if (document.exists()) {
                                DanhSachMonAn monAn = document.toObject(DanhSachMonAn.class);
                                if (monAn != null) {
                                    String id = document.getString("id");
                                    String ten = document.getString("title");
                                    String hinhAnh = document.getString("url");

                                    String categories_id = document.getString("categories_id");
                                    String video = document.getString("video");
                                    Log.d("video categori ",video + " / " + categories_id );
                                    ArrayList<String> nguyenlieu = (ArrayList<String>) document.get("ingredients");
                                    ArrayList<String> buocnau = (ArrayList<String>) document.get("instructions");
                                    ArrayList<String> tips = (ArrayList<String>) document.get("tips");
                                    Long userView = document.getLong("view");
                                    Integer userViewInt = userView != null ? userView.intValue() : 0;

                                    Long userLike = document.getLong("like");
                                    Integer userLikeInt = userView != null ? userLike.intValue() : 0;

                                    Long userCoin = document.getLong("coin");
                                    Integer userCoinInt = userView != null ? userCoin.intValue() : 0;

                                    String timeCook = document.getString("cooking_time");
                                    boolean checkLike = true;
                                    boolean checkLove = true;
                                    DanhSachMonAn monAn2 = new DanhSachMonAn(id,ten,hinhAnh,video,categories_id,nguyenlieu,buocnau,tips,checkLike,userCoinInt,userLikeInt,userViewInt,timeCook,checkLove);
                                    DanhSachMonAn.add(monAn2);
                                } else {

                                }
                            }
                        }

                    } else {

                    }

                        monAnAdapter.notifyDataSetChanged();


        }).addOnFailureListener(e -> Log.e("Firebase", "Lỗi tải dữ liệu", e));
    }
}