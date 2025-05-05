package com.example.letmecook.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.letmecook.Model.DanhSachMonAn;
import com.example.letmecook.Model.LoaiMonAn;
import com.example.letmecook.R;
import com.example.letmecook.adapter.BuocNauAdapter;
import com.example.letmecook.adapter.ListMonAnAdapter;
import com.example.letmecook.adapter.LoaiMonAnAdapter;
import com.example.letmecook.adapter.NguyenLieuAdapter;
import com.example.letmecook.adapter.TipsAdapter;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DetailItem extends AppCompatActivity {
    private RecyclerView recyclerViewBuocNau,recyclerViewNguyenLieu,recyclerViewTips;
    private FirebaseFirestore db;

    private ArrayList<String> danhSachNguyenLieu = new ArrayList<>();
    private ArrayList<String> danhsachbuocnau = new ArrayList<>();
    private ArrayList<String> danhsachTips = new ArrayList<>();
    private NguyenLieuAdapter adapter;
    private BuocNauAdapter buocNauAdapter;
    private TipsAdapter tipsAdapter;
    private List<DanhSachMonAn> DanhSachMonAn = new ArrayList<>();
    TextView tvTenMonAn, tvMoTa;
    ImageView imgMonAn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_item);
        String categories_id = "";
        tvTenMonAn = findViewById(R.id.tvTenMonAn);

        imgMonAn = findViewById(R.id.imgMonAn);

        recyclerViewNguyenLieu = findViewById(R.id.recyclerViewNguyenLieu);
        recyclerViewBuocNau = findViewById(R.id.recyclerViewBuocNau);
        recyclerViewTips = findViewById(R.id.recyclerViewTips);

        danhSachNguyenLieu = new ArrayList<>();
        adapter = new NguyenLieuAdapter(danhSachNguyenLieu);
        recyclerViewNguyenLieu.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewNguyenLieu.setAdapter(adapter);

        danhsachbuocnau = new ArrayList<>();
        buocNauAdapter = new BuocNauAdapter(danhsachbuocnau);
        recyclerViewBuocNau.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBuocNau.setAdapter(buocNauAdapter);

        danhsachTips = new ArrayList<>();
        tipsAdapter = new TipsAdapter(danhsachTips);
        recyclerViewTips.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTips.setAdapter(tipsAdapter);


        Intent intent = getIntent();
        if (intent != null) {
            String tenMonAn = intent.getStringExtra("TEN_MON_AN");
            String hinhAnh = intent.getStringExtra("HINH_ANH");
            categories_id =  intent.getStringExtra("ID");
            tvTenMonAn.setText(tenMonAn);

            Log.d("recipes","recipes" + categories_id);
            Glide.with(this)
                    .load(hinhAnh)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(imgMonAn);
        }

        loadMonAn(categories_id);

    }

    private void loadMonAn(String categories_id) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("recipes ")
                .whereEqualTo("id", categories_id)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);

                        // Lấy dữ liệu từ Firestore
                        List<String> nguyenlieu = (List<String>) document.get("ingredients");
                        List<String> buocnau = (List<String>) document.get("instructions");
                        List<String> tips = (List<String>) document.get("tips");

                        // Kiểm tra null
                        if (nguyenlieu == null) nguyenlieu = new ArrayList<>();
                        if (buocnau == null) buocnau = new ArrayList<>();
                        if (tips == null) tips = new ArrayList<>();

                        // Cập nhật danh sách
                        danhSachNguyenLieu.clear();
                        danhSachNguyenLieu.addAll(nguyenlieu);

                        danhsachbuocnau.clear();
                        danhsachbuocnau.addAll(buocnau);

                        danhsachTips.clear();
                        danhsachTips.addAll(tips);

                        // Cập nhật Adapter (gọi trong onSuccess)
                        tipsAdapter.notifyDataSetChanged();
                        buocNauAdapter.notifyDataSetChanged();
                        adapter.notifyDataSetChanged();

                        Log.d("Firestore", "Dữ liệu món ăn: " + danhSachNguyenLieu);
                    } else {
                        Log.d("Firestore", "Document không tồn tại!");
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Lỗi khi lấy dữ liệu", e));
    }

}