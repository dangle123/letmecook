package com.example.letmecook.Activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letmecook.Model.UpCoin;
import com.example.letmecook.R;
import com.example.letmecook.adapter.UpCoinAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class UpCoinActivity extends AppCompatActivity {
    public FirebaseFirestore db;
    public UpCoinAdapter adapter;
    public List<UpCoin> UpCoinData = new ArrayList<>();
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_up_coin);

        recyclerView = findViewById(R.id.recyclerviewUpcoin);
        db = FirebaseFirestore.getInstance();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        LinearLayoutManager layoutManager= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        adapter = new UpCoinAdapter(this,UpCoinData);
        recyclerView.setAdapter(adapter);
        loadUpCoin();



    }

    private void loadUpCoin() {
        db.collection("coin").get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()){

                UpCoinData.clear();

                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    if (doc.exists()){
                       String value = doc.getString("value");
                       String coin =  doc.getString("coin");
                       UpCoin upCoin = new UpCoin(value,coin);
                        UpCoinData.add(upCoin);

                    }
                    adapter.notifyDataSetChanged();
                }
            }
        }).addOnFailureListener(e -> {

            e.printStackTrace();
        });

    }
}