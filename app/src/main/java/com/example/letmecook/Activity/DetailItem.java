package com.example.letmecook.Activity;

import android.content.Intent;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


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
    private String categories_id;
    PlayerView playerView;
    ExoPlayer player;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_item);

        tvTenMonAn = findViewById(R.id.tvTenMonAn);

//        imgMonAn = findViewById(R.id.imgMonAn);

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
        playerView = findViewById(R.id.playerView);
        danhsachTips = new ArrayList<>();
        tipsAdapter = new TipsAdapter(danhsachTips);
        recyclerViewTips.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTips.setAdapter(tipsAdapter);
        String video ;
        Intent intent = getIntent();
        if (intent != null) {
            String tenMonAn = intent.getStringExtra("TEN_MON_AN");
            String hinhAnh = intent.getStringExtra("HINH_ANH");
            categories_id =  intent.getStringExtra("ID");
            video = intent.getStringExtra("VIDEO");
            tvTenMonAn.setText(tenMonAn);
            player = new ExoPlayer.Builder(this).build();
            playerView.setPlayer(player);
            Log.d("video categori ", video);
            com.google.android.exoplayer2.MediaItem mediaItem = com.google.android.exoplayer2.MediaItem.fromUri(video);

            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();
        }

        loadMonAn(categories_id);

    }

    private String extractVideoId(String url) {
        String videoId = "";
        if (url.contains("v=")) {
            videoId = url.substring(url.indexOf("v=") + 2);
            int ampersand = videoId.indexOf("&");
            if (ampersand != -1) {
                videoId = videoId.substring(0, ampersand);
            }
        }
        return videoId;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.pause();
        }
    }
    public  void onDestroy(){
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
        compositeDisposable.clear();
    }

    private Single<DocumentSnapshot> getMonAn (String categories_id) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        return Single.create(emitter -> {
            db.collection("recipes ")
                    .whereEqualTo("id", categories_id)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(querySnapshot  -> {
                        if (!querySnapshot.isEmpty()) {
                            if (!emitter.isDisposed()) {
                                emitter.onSuccess(querySnapshot.getDocuments().get(0));

                            }}
                    })
                    .addOnFailureListener(e -> {
                        if ( !emitter.isDisposed()){
                            emitter.onError(e);

                        }
                    });

        });
    }
    private void loadMonAn(String categories_id) {
        compositeDisposable.add(
                getMonAn(categories_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(document -> {
                            if (document.exists()) {
                                List<String> nguyenlieu = (List<String>) document.get("ingredients");
                                List<String> buocnau = (List<String>) document.get("instructions");
                                List<String> tips = (List<String>) document.get("tips");

                                if (nguyenlieu == null) nguyenlieu = new ArrayList<>();
                                if (buocnau == null) buocnau = new ArrayList<>();
                                if (tips == null) tips = new ArrayList<>();

                                danhSachNguyenLieu.clear();
                                danhSachNguyenLieu.addAll(nguyenlieu);

                                danhsachbuocnau.clear();
                                danhsachbuocnau.addAll(buocnau);

                                danhsachTips.clear();
                                danhsachTips.addAll(tips);

                                adapter.notifyDataSetChanged();
                                buocNauAdapter.notifyDataSetChanged();
                                tipsAdapter.notifyDataSetChanged();

                            }
                        }, throwable -> Log.e("firebase Detail","erro " , throwable))

        );

    }

}