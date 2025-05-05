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

import com.example.letmecook.Activity.AllItemActivity;
import com.example.letmecook.Activity.CategoriActivity;
import com.example.letmecook.Activity.DetailItem;
import com.example.letmecook.Model.DanhSachMonAn;
import com.example.letmecook.R;
import com.example.letmecook.adapter.ListMonAnAdapter;
import com.example.letmecook.adapter.LoaiMonAnAdapter;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.letmecook.Model.LoaiMonAn;

import com.google.firebase.firestore.DocumentSnapshot;


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

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_home_fragment, container, false);

        viewFlipper = view.findViewById(R.id.viewflipper);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        userID = user.getUid();
        userDetail(user.getUid());

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        tvAlltem = view.findViewById(R.id.tvAlltem);

        tvAlltem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), CategoriActivity.class);
                intent.putExtra("theloai", "-1");
                intent.putExtra("Tenloai","ALL");
                startActivity(intent);
            }
        });




        db.collection("banner").document("0")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> quangcao = (List<String>) documentSnapshot.get("url");
                        mangquangcao.addAll(quangcao);
                        test(mangquangcao);
                    } else {

                    }
                })
                .addOnFailureListener(e -> {

                });

        test(mangquangcao);
        loaiMonAnList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerViewLoaiMonAn);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        adapter = new LoaiMonAnAdapter(getContext(), loaiMonAnList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        //addListRecipesToFirestore();


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
    private Map<String, Object> createRecipe(int id, String title, String cookingTime, List<String> ingredients, List<String> instructions, List<String> tips, int view, int like, double ratings, int coin, String url) {
        Map<String, Object> recipe = new HashMap<>();
        recipe.put("categories_id", "5"); // Mặc định là "1"
        recipe.put("id", String.valueOf(id));
        recipe.put("title", title);
        recipe.put("cooking_time", cookingTime);
        recipe.put("ingredients", ingredients);
        recipe.put("instructions", instructions);
        recipe.put("tips", tips);
        recipe.put("view", view);
        recipe.put("like", like);
        recipe.put("ratings", ratings);
        recipe.put("coin", coin);
        recipe.put("url", url);

        return recipe;
    }


    public void addListRecipesToFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        List<Map<String, Object>> recipes = Arrays.asList(
                createRecipe(47, "Nước mắm sả tắc", "10 phút",
                        Arrays.asList("3 muỗng nước mắm", "2 muỗng đường", "1 muỗng tắc (quất) băm", "1 muỗng sả băm", "1 muỗng ớt băm"),
                        Arrays.asList("Hòa tan nước mắm với đường.", "Thêm tắc, sả, ớt băm, khuấy đều.", "Dùng với đồ nướng, hải sản."),
                        Arrays.asList("Có thể thêm lá chanh để tăng hương vị.", "Dùng tắc tươi để có vị chua thanh."),
                        5100, 270, 4.7, 9, "https://example.com/nuoc-mam-sa-tac.jpg"
                ),
                createRecipe(48, "Nước mắm cà cuống", "10 phút",
                        Arrays.asList("3 muỗng nước mắm", "1 con cà cuống", "1 muỗng đường", "1 muỗng tỏi băm", "1 muỗng ớt băm"),
                        Arrays.asList("Giã nhỏ cà cuống, hòa với nước mắm, đường.", "Thêm tỏi, ớt băm, khuấy đều.", "Dùng với bún chả, chả cá."),
                        Arrays.asList("Cà cuống tạo mùi thơm đặc trưng, không thay thế được.", "Có thể thêm giấm để tăng độ chua."),
                        4800, 220, 4.6, 8, "https://example.com/nuoc-mam-ca-cuong.jpg"
                ),
                createRecipe(49, "Nước mắm mỡ hành", "10 phút",
                        Arrays.asList("3 muỗng nước mắm", "1 muỗng mỡ lợn", "1 muỗng hành lá băm", "1 muỗng đường"),
                        Arrays.asList("Đun nóng mỡ lợn, cho hành vào phi thơm.", "Thêm nước mắm, đường, khuấy đều.", "Dùng với bánh ướt, bánh bèo."),
                        Arrays.asList("Nên dùng mỡ lợn để có vị béo đặc trưng.", "Có thể thay bằng dầu ăn nếu thích."),
                        4700, 230, 4.5, 7, "https://example.com/nuoc-mam-mo-hanh.jpg"
                ),
                createRecipe(50, "Nước mắm giấm gừng", "10 phút",
                        Arrays.asList("3 muỗng nước mắm", "2 muỗng giấm gạo", "1 muỗng đường", "1 muỗng gừng băm"),
                        Arrays.asList("Hòa tan nước mắm, giấm, đường.", "Thêm gừng băm, khuấy đều.", "Dùng với thịt ba chỉ luộc."),
                        Arrays.asList("Có thể thay giấm bằng nước cốt chanh.", "Dùng giấm gạo để có vị chua nhẹ."),
                        4500, 210, 4.4, 7, "https://example.com/nuoc-mam-giam-gung.jpg"
                ),
                createRecipe(51, "Nước mắm tiêu", "10 phút",
                        Arrays.asList("3 muỗng nước mắm", "1 muỗng tiêu xay", "1 muỗng đường", "1 muỗng tỏi băm"),
                        Arrays.asList("Hòa tan nước mắm với đường.", "Thêm tiêu xay, tỏi băm, khuấy đều.", "Dùng với khô cá, cơm tấm."),
                        Arrays.asList("Có thể thêm ớt để tăng độ cay.", "Dùng tiêu sọ giúp mùi thơm đậm hơn."),
                        4600, 215, 4.5, 7, "https://example.com/nuoc-mam-tieu.jpg"
                ),
                createRecipe(52, "Muối ớt xanh", "10 phút",
                        Arrays.asList("2 muỗng muối", "1 muỗng đường", "2 trái ớt xiêm xanh", "1 muỗng nước cốt chanh"),
                        Arrays.asList("Xay nhuyễn muối, đường, ớt xiêm xanh.", "Thêm nước cốt chanh, khuấy đều.", "Dùng với hải sản, gà nướng."),
                        Arrays.asList("Có thể thêm sữa đặc để tạo độ sánh mịn.", "Nên dùng ớt xiêm xanh để có vị cay đặc trưng."),
                        5300, 280, 4.9, 10, "https://example.com/muoi-ot-xanh.jpg"
                ),
                createRecipe(53, "Muối tiêu chanh", "5 phút",
                        Arrays.asList("2 muỗng muối", "1 muỗng tiêu xay", "1 muỗng đường", "1 muỗng nước cốt chanh"),
                        Arrays.asList("Trộn đều muối, tiêu, đường.", "Thêm nước cốt chanh, khuấy đều.", "Dùng với gà luộc, vịt luộc."),
                        Arrays.asList("Có thể thêm sả băm để tạo hương thơm.", "Dùng tiêu sọ giúp vị thơm hơn."),
                        4900, 250, 4.8, 8, "https://example.com/muoi-tieu-chanh.jpg"
                ),
                createRecipe(54, "Mắm thái chua cay", "15 phút",
                        Arrays.asList("3 muỗng mắm thái", "1 muỗng đường", "1 muỗng ớt băm", "1 muỗng tỏi băm"),
                        Arrays.asList("Trộn đều mắm thái với đường.", "Thêm ớt, tỏi băm, khuấy đều.", "Dùng với đồ cuốn."),
                        Arrays.asList("Có thể thêm giấm để tạo vị chua nhẹ.", "Dùng mắm ngon giúp hương vị đậm đà hơn."),
                        4700, 230, 4.6, 8, "https://example.com/mam-thai.jpg"
                ),
                createRecipe(55, "Tương đậu phộng", "10 phút",
                        Arrays.asList("3 muỗng tương đậu phộng", "1 muỗng đường", "1 muỗng nước cốt dừa"),
                        Arrays.asList("Hòa tan tương đậu phộng với đường.", "Thêm nước cốt dừa, khuấy đều.", "Dùng với gỏi cuốn, nem lụi."),
                        Arrays.asList("Có thể thêm ớt băm để tạo độ cay nhẹ.", "Dùng nước cốt dừa giúp tương béo hơn."),
                        5200, 260, 4.8, 9, "https://example.com/tuong-dau-phong.jpg"
                ),
                createRecipe(56, "Tương bần", "10 phút",
                        Arrays.asList("3 muỗng tương bần", "1 muỗng đường", "1 muỗng hành phi"),
                        Arrays.asList("Trộn đều tương bần với đường.", "Thêm hành phi, khuấy đều.", "Dùng với cá kho, rau luộc."),
                        Arrays.asList("Có thể thêm tỏi băm để tăng hương vị.", "Nên dùng tương bần nguyên chất."),
                        4800, 220, 4.6, 8, "https://example.com/tuong-ban.jpg"
                ),
                createRecipe(57, "Tương ớt chua ngọt", "10 phút",
                        Arrays.asList("3 muỗng tương ớt", "1 muỗng đường", "1 muỗng giấm"),
                        Arrays.asList("Hòa tan tương ớt với đường.", "Thêm giấm, khuấy đều.", "Dùng với vịt quay, gà nướng."),
                        Arrays.asList("Có thể thêm tỏi băm để tăng hương vị.", "Dùng giấm táo giúp vị chua dịu hơn."),
                        5100, 250, 4.7, 9, "https://example.com/tuong-ot-chua-ngot.jpg"
                )
        );

        for (Map<String, Object> recipe : recipes) {
            String recipeId = db.collection("recipes ").document().getId();
            db.collection("recipes ").document(recipeId)
                    .set(recipe)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Thêm món thành công: " + recipe.get("title")))
                    .addOnFailureListener(e -> Log.e("Firestore", "Lỗi thêm món", e));
        }
    }



    private void userDetail(String userId) {

        db = FirebaseFirestore.getInstance();
        db.collection("user").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long coinTamp = documentSnapshot.getLong("coin");
                        Integer userCoinTamp = coinTamp != null ? coinTamp.intValue() : 0;
                        userCoin = userCoinTamp;

                        favoritesUser = (List<Long>) documentSnapshot.get("favorites");
                        LikeUser = (List<Long>) documentSnapshot.get("like");

                    } else {

                    }
                })
                .addOnFailureListener(e -> Log.e("UserFavorites", "Lỗi khi lấy dữ liệu", e));

    }


    private void loadMonAn() {

        db.collection("recipes ").get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<DocumentSnapshot> allRecipes = queryDocumentSnapshots.getDocuments();
            Collections.shuffle(allRecipes);
            List<DocumentSnapshot> randomRecipes = allRecipes.subList(0, Math.min(10, allRecipes.size()));
          //  DanhSachMonAn.clear();

            for (DocumentSnapshot doc : randomRecipes){
                String id = doc.getString("id");
                String ten = doc.getString("title");
                String hinhAnh = doc.getString("url");
                String categories_id = doc.getString("categories_id");
                ArrayList<Long> nguyenlieu = (ArrayList<Long>) doc.get("instructions");
                ArrayList<Long> buocnau = (ArrayList<Long>) doc.get("instructions");
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

                DanhSachMonAn monAn = new DanhSachMonAn(id,ten,hinhAnh,categories_id,nguyenlieu,buocnau,checkLike,userCoinInt,userViewInt,userLikeInt,timeCook,checkLove);
                DanhSachMonAn.add(monAn);
            }

//            for (DocumentSnapshot document : queryDocumentSnapshots) {
//                String id = document.getString("id");
//                String ten = document.getString("title");
//                String hinhAnh = document.getString("url");
//                String categories_id = document.getString("categories_id");
//                ArrayList<Long> nguyenlieu = (ArrayList<Long>) document.get("ingredients");
//                ArrayList<Long> buocnau = (ArrayList<Long>) document.get("instructions");
//                Long userView = document.getLong("view");
//                Integer userViewInt = userView != null ? userView.intValue() : 0;
//
//                Long userLike = document.getLong("like");
//                Integer userLikeInt = userLike != null ? userLike.intValue() : 0;
//
//                Long userCoin = document.getLong("coin");
//                Integer userCoinInt = userCoin != null ? userCoin.intValue() : 0;
//
//                String timeCook = document.getString("cooking_time");
//
//
//                DanhSachMonAn monAn = new DanhSachMonAn(id,ten,hinhAnh,categories_id,nguyenlieu,buocnau,checkLike,userCoinInt,userViewInt,userLikeInt,timeCook,checkLove);
//                DanhSachMonAn.add(monAn);
//
//            }
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

    private void test(List<String> mangquangcao) {


        ActionViewFlipper(mangquangcao);
    }

    private void ActionViewFlipper(List<String> mangquangcao) {


        List<String> data = mangquangcao;

        for (int i = 0; i < data.size();i++){
            ImageView imageView = new ImageView(requireContext());
            Glide.with(requireContext())
                    .load(data.get(i))
                    .transform(new RoundedCorners(30))
                    .into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(imageView);
        }
        viewFlipper.setFlipInterval(3000);
        viewFlipper.setAutoStart(true);
        Animation slide_in = AnimationUtils.loadAnimation(requireContext(),R.anim.slide_in);
        Animation slide_out = AnimationUtils.loadAnimation(requireContext(),R.anim.slide_out);
        viewFlipper.setInAnimation(slide_in);
        viewFlipper.setOutAnimation(slide_out);

    }



}