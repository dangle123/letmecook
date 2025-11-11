package com.example.letmecook.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.example.letmecook.Activity.AllItemActivity;
import com.example.letmecook.Activity.CategoriActivity;
import com.example.letmecook.Activity.DetailItem;
import com.example.letmecook.Model.DanhSachMonAn;
import com.example.letmecook.Model.User;
import com.example.letmecook.R;
import com.example.letmecook.adapter.ListMonAnAdapter;
import com.example.letmecook.adapter.LoaiMonAnAdapter;
import com.example.letmecook.cache.CachedUserManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.letmecook.Model.LoaiMonAn;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Source;


public class HomeProductFragment extends Fragment {

    ViewFlipper viewFlipper;

    private RecyclerView recyclerView,recyclerViewLoadmonan;
    private LoaiMonAnAdapter adapter;
    private List<LoaiMonAn> loaiMonAnList;

    private List<Long> favoritesUser = new ArrayList<>();
    private List<Long> LikeUser = new ArrayList<>();

    private boolean checkLike = false;
    private  boolean checkLove = false;
public TextView tvAlltem;
    private String userID;
    private int userCoin = 0;
    private FirebaseFirestore db;
    private List<String> mangquangcao = new ArrayList<>();
    private ListMonAnAdapter monAnAdapter;
    private List<DanhSachMonAn> DanhSachMonAn = new ArrayList<>();
    private LottieAnimationView lottieLoading;
    ProgressBar progressBar;
    LinearLayout contentLayout;
    RelativeLayout loadingOverlay;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_home_fragment, container, false);
        viewFlipper = view.findViewById(R.id.viewflipper);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        userID = user.getUid();
        userDetail(user.getUid());

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        tvAlltem = view.findViewById(R.id.tvAlltem);
        lottieLoading = view.findViewById(R.id.lottieLoading);
        contentLayout = view.findViewById(R.id.contentLayout);
        loadingOverlay = view.findViewById(R.id.loadingOverlay);

        showLoading(true);

        tvAlltem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), CategoriActivity.class);
                intent.putExtra("theloai", "-1");
                intent.putExtra("Tenloai","ALL");
                startActivity(intent);
            }
        });




        db.collection("banner")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String url = doc.getString("url");
                        if (url != null) {
                            mangquangcao.add(url);
                        }
                    }
                    ActionViewFlipper(mangquangcao);

                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Lỗi lấy banner: ", e);
                });



        loaiMonAnList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerViewLoaiMonAn);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        adapter = new LoaiMonAnAdapter(getContext(), loaiMonAnList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


        loadLoaimonan();

        recyclerViewLoadmonan = view.findViewById(R.id.recyclerViewLoadmonan);
        recyclerViewLoadmonan.setLayoutManager(new LinearLayoutManager(getContext()));
        LinearLayoutManager layoutLoadMonAnManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewLoadmonan.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        monAnAdapter = new ListMonAnAdapter(getContext(), DanhSachMonAn);
        recyclerViewLoadmonan.setAdapter(monAnAdapter);

        loadMonAn();
        monAnAdapter.setOnItemClickListener(new ListMonAnAdapter.OnItemClickListener() {

            public void onItemClick(DanhSachMonAn DanhSachMonAn, int position) {
                if (userCoin >= DanhSachMonAn.getCoinUser()){

                        db.collection("recipes ")
                                .whereEqualTo("id", DanhSachMonAn.getId())
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots ->{
                                    if ( !queryDocumentSnapshots.isEmpty()){
                                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                                        String documentId = documentSnapshot.getId();

                                        db.collection("recipes ").document(documentId)
                                                .update("view", FieldValue.increment(1))
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d("danhsach","danhsach" + DanhSachMonAn.getViewUser() +1);
                                                    DanhSachMonAn.setViewUser(DanhSachMonAn.getViewUser() +1);
                                                    monAnAdapter.notifyItemChanged(position);
                                                })
                                                .addOnFailureListener(e ->
                                                        Toast.makeText(getContext(), "Lỗi khi cập nhật lượt xem", Toast.LENGTH_SHORT).show()
                                                );
                                    }

                        });

                        int tam = userCoin - DanhSachMonAn.getCoinUser();
                        db.collection("user").document(userID).update("coin",tam);
                    Log.d("coinuser","coinuser" + userCoin +":" +tam);

                    Intent intent = new Intent(getContext(), DetailItem.class);
                    intent.putExtra("TEN_MON_AN", DanhSachMonAn.getTen());
                    intent.putExtra("VIDEO",DanhSachMonAn.getVideo());
                    intent.putExtra("HINH_ANH", DanhSachMonAn.getHinhAnh());
                    intent.putExtra("ID", DanhSachMonAn.getId());
                    startActivity(intent);

                } else {
                    Toast.makeText(getContext(), "Tài khoản không đủ Xu thanh toán", Toast.LENGTH_SHORT).show();
                }

            }
        });
        adapter.setOnItemClickListener(new LoaiMonAnAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(LoaiMonAn LoaiMonAn) {


                Intent intent = new Intent(requireActivity(), CategoriActivity.class);
                intent.putExtra("theloai", LoaiMonAn.getId());
                intent.putExtra("Tenloai",LoaiMonAn.getTen());
                startActivity(intent);
            }
        });

        return view;
    }

    private void showLoading(boolean show) {
        if (loadingOverlay == null || contentLayout == null) return;

        if (show) {
            loadingOverlay.setVisibility(View.VISIBLE);
            lottieLoading.playAnimation();
        } else {
            lottieLoading.cancelAnimation();
            loadingOverlay.setVisibility(View.GONE);
        }
    }


private void userDetail(String userId) {
    db = FirebaseFirestore.getInstance();


    db.collection("user").document(userId)
            .get(Source.SERVER)
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    com.example.letmecook.Model.User user =
                            documentSnapshot.toObject(com.example.letmecook.Model.User.class);
                    if (user != null) {
                        CachedUserManager.saveUser(getContext(), user);

                    }

                    Long coinTamp = documentSnapshot.getLong("coin");
                    Integer userCoinTamp = coinTamp != null ? coinTamp.intValue() : 0;
                    userCoin = userCoinTamp;

                    favoritesUser = (List<Long>) documentSnapshot.get("favorites");
                    LikeUser = (List<Long>) documentSnapshot.get("like");


                } else {

                    fetchUserFromServer(userId);
                }
            })
            .addOnFailureListener(e -> {

                fetchUserFromServer(userId);
            });
}


    private void fetchUserFromServer(String userId) {
        db.collection("user").document(userId)
                .get(Source.SERVER)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        Long coinTamp = documentSnapshot.getLong("coin");
                        Integer userCoinTamp = coinTamp != null ? coinTamp.intValue() : 0;
                        userCoin = userCoinTamp;

                        favoritesUser = (List<Long>) documentSnapshot.get("favorites");
                        LikeUser = (List<Long>) documentSnapshot.get("like");

                        Log.d("UserCache", "Đã tải dữ liệu user từ SERVER");
                    } else {
                        Log.d("UserCache", "Không tìm thấy user trong Firestore");
                    }
                })
                .addOnFailureListener(e -> Log.w("UserCache", "Lỗi lấy từ server", e));
    }


    private void loadMonAn() {

        db.collection("recipes ").get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<DocumentSnapshot> allRecipes = queryDocumentSnapshots.getDocuments();
            Collections.shuffle(allRecipes);
            List<DocumentSnapshot> randomRecipes = allRecipes.subList(0, Math.min(10, allRecipes.size()));


            for (DocumentSnapshot doc : randomRecipes){
                showLoading(false);
                String id = doc.getString("id");
                String ten = doc.getString("title");
                String hinhAnh = doc.getString("url");
                String categories_id = doc.getString("categories_id");
                String video = doc.getString("video");

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


    private void ActionViewFlipper(List<String> mangquangcao) {
        viewFlipper.removeAllViews();

        for (String link : mangquangcao) {
            ImageView imageView = new ImageView(requireContext());
            Glide.with(requireContext())
                    .load(link)
                    .transform(new RoundedCorners(30))
                    .into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(imageView);
        }

        viewFlipper.setFlipInterval(3000);

        Animation slide_in = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in);
        Animation slide_out = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out);
        viewFlipper.setInAnimation(slide_in);
        viewFlipper.setOutAnimation(slide_out);

        viewFlipper.setAutoStart(true);
        viewFlipper.startFlipping();
    }




}