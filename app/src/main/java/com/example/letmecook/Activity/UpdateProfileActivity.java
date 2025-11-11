package com.example.letmecook.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.letmecook.Model.User;
import com.example.letmecook.R;
import com.example.letmecook.cache.CachedUserManager;
import com.example.letmecook.utils.CloudinaryManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class UpdateProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imgUser, imgChange;
    private EditText edtName, edtEmail, edtBirth;
    private Uri imageUri;
    private String userId;
    private TextView btnUpdate;
    private boolean checkUpdateAvata = false;
    private String avata; // link ảnh cũ
    private  Integer type = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_profile);

        imgUser = findViewById(R.id.imgUserUpdate);
        imgChange = findViewById(R.id.imgChaneUpdateProfile);
        edtName = findViewById(R.id.edtNameUpdateProfile);
        edtEmail = findViewById(R.id.edtMailUpdateProfile);
        edtBirth = findViewById(R.id.edtBirthUpdateProfile);
        btnUpdate = findViewById(R.id.btnUpdateProfile);

        Intent intent = getIntent();
        edtName.setText(intent.getStringExtra("Name"));
        edtBirth.setText(intent.getStringExtra("BIRTH"));
        edtEmail.setText(intent.getStringExtra("MAIL"));
        userId = intent.getStringExtra("ID");
        avata = intent.getStringExtra("AVATA");
        type = intent.getIntExtra("TYPE", 0);

        Log.d("getStringExtra", intent.getStringExtra("NAME") + "/" +
                intent.getStringExtra("BIRTH") + "/" +
                intent.getStringExtra("MAIL") + "/" +
                avata);

        Glide.with(this)
                .load(avata)
                .placeholder(R.drawable.placeholder)
                .circleCrop()
                .into(imgUser);

        imgChange.setOnClickListener(v -> {
            openFileChooser();
            checkUpdateAvata = true;
        });

        btnUpdate.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String birth = edtBirth.getText().toString().trim();

            // Nếu user không đổi ảnh, dùng link cũ
            if (!checkUpdateAvata || imageUri == null) {
                UpdateToFirestore(name, email, birth, userId, avata);
            } else {
                uploadToCloudinary(imageUri, imageUrl ->
                        runOnUiThread(() ->
                                UpdateToFirestore(name, email, birth, userId, imageUrl)));
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this)
                    .load(imageUri)
                    .circleCrop()
                    .into(imgUser);
        }
    }
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGE_REQUEST);
    }



    public void uploadToCloudinary(Uri imageUri, OnUploadSuccessListener listener) {
        new Thread(() -> {
            try {
                Cloudinary cloudinary = CloudinaryManager.getInstance();
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Map uploadResult = cloudinary.uploader().upload(inputStream, ObjectUtils.emptyMap());
                String imageUrl = (String) uploadResult.get("secure_url");
                listener.onSuccess(imageUrl);
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Lỗi upload ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    public interface OnUploadSuccessListener {
        void onSuccess(String imageUrl);
    }

    private void UpdateToFirestore(String name, String email, String birth, String userId, String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        HashMap<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("email", email);
        updates.put("birth", birth);
        updates.put("avata", imageUrl);

        db.collection("user")
                .document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();

                    // Cập nhật cache
                    User currentUser = CachedUserManager.getCurrentUser(this);
                    if (currentUser != null) {
                        currentUser.setName(name);
                        currentUser.setEmail(email);
                        currentUser.setBirth(birth);
                        currentUser.setAvata(imageUrl);
                        CachedUserManager.saveUser(this, currentUser);
                    }

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("userUpdated", true);
                    setResult(RESULT_OK, resultIntent);
                    if ( type  == 1 ) {
                        Intent intent = new Intent(this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        finish();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "erro : " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}