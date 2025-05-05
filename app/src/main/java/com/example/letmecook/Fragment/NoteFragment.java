package com.example.letmecook.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.letmecook.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.security.AccessController;
import java.util.HashMap;
import java.util.Map;

public class NoteFragment extends Fragment {
    private EditText editTextNote;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private DocumentReference noteRef;
    private Handler handler = new Handler();
    private Runnable saveRunnable;

    private Button btnSave,btnDelete;
    private EditText edtContent;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_note, container, false);

        db = FirebaseFirestore.getInstance();
        btnSave = view.findViewById(R.id.btnSave);
        btnDelete = view.findViewById(R.id.btnDelete);
        edtContent = view.findViewById(R.id.edtContent);

        LoadContent();


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                String userId = user.getUid();
                String content = edtContent.getText().toString();
                Log.d("content","content :" + content);
                db.collection("user").document(userId).update("note", content).addOnSuccessListener(aVoid -> Toast.makeText(v.getContext(), "Ghi chú đã được Save!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e ->
                                Toast.makeText(v.getContext(), "Lỗi khi lưu ghi chú!", Toast.LENGTH_SHORT).show());
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                String userId = user.getUid();
                String content = "";
                db.collection("user").document(userId).update("note",content).addOnSuccessListener(aVoid -> Toast.makeText(v.getContext(), "Ghi chú đã được Delete!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e ->
                                Toast.makeText(v.getContext(), "Lỗi khi lưu ghi chú!", Toast.LENGTH_SHORT).show());
                LoadContent();
            }
        });

        return view;
    }

    private void LoadContent() {

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();

        db.collection("user").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String content = documentSnapshot.getString("note");

                        edtContent.setText(content);
                    } else {

                    }
                })
                .addOnFailureListener(e -> Log.e("Firebase", "Lỗi khi tải ghi chú!", e));
    }
}
