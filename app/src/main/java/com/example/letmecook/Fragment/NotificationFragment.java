package com.example.letmecook.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letmecook.Model.DanhSachMonAn;
import com.example.letmecook.Model.danhSachNotifi;
import com.example.letmecook.R;
import com.example.letmecook.adapter.ListMonAnAdapter;
import com.example.letmecook.adapter.NotifiAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {
    private NotifiAdapter notiAdapter;
    private RecyclerView recyclerviewNotifi;
    private FirebaseFirestore db;
    private List<Long> favorites = new ArrayList<>();
    private List<danhSachNotifi> danhSachNoti = new ArrayList<>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_notification, container, false);

        recyclerviewNotifi = view.findViewById(R.id.recyclerviewNotifi);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        recyclerviewNotifi.setLayoutManager(layoutManager);

        recyclerviewNotifi.setLayoutManager(new LinearLayoutManager(getContext()));
        LinearLayoutManager layoutLoadMonAnManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        notiAdapter = new NotifiAdapter(getContext(), danhSachNoti);
        recyclerviewNotifi.setAdapter(notiAdapter);

        String userId = user.getUid();
        getFavoriteDishes(userId);
        return view;
    }

    private void favorites(String userId) {
        db = FirebaseFirestore.getInstance();

        db.collection("user").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        favorites = (List<Long>) documentSnapshot.get("notifi");
                        Log.d("datanotifire", "datanotifire với ID: " + favorites);
                        Log.d("datanotifire", "datanotifire với ID: " + userId);
                        if (favorites != null) {
                            getFavoriteDishes(userId);

                        } else {

                        }
                    } else {

                    }
                })
                .addOnFailureListener(e -> Log.e("UserFavorites", "Lỗi khi lấy dữ liệu", e));
    }

    private void getFavoriteDishes(String userId) {

        db = FirebaseFirestore.getInstance();
        db.collection("notification").whereEqualTo("userid", userId).orderBy("timestamp", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {

                    if (!queryDocumentSnapshots.isEmpty()) {
                        danhSachNoti.clear();

                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            if (document.exists()) {
                                    String title = document.getString("title") != null ? document.getString("title") : "No Title";
                                    String type = document.getString("type") != null ? document.getString("type") : "No Type";
                                    String note = document.getString("note") != null ? document.getString("note") : " no note" ;
                                    String decrible = document.getString("decrible") != null ? document.getString("decrible") : "No Description";
                                String status = document.getString("status") != null ? document.getString("status") : "No Status";
                                    Log.e("UserFavorites", "!" + title + type + status);
                                    danhSachNotifi monAn2 = new danhSachNotifi(title,type,decrible,note,status);
                                    danhSachNoti.add(monAn2);
                                notiAdapter.notifyDataSetChanged();
                            }
                        }

                    } else {

                    }

                    notiAdapter.notifyDataSetChanged();


                }).addOnFailureListener(e -> Log.e("Firebase", "Lỗi tải dữ liệu", e));
    }


}
