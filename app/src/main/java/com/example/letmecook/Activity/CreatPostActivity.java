package com.example.letmecook.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.letmecook.Model.User;
import com.example.letmecook.R;
import com.example.letmecook.cache.CachedUserManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreatPostActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    private ImageView imageCreatePost;
    private ImageView imageSentCreatePost;
    private ImageView imageChose;
    private ImageView imageUser;
    private TextView tvNameUser;
    private EditText edtCreatPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_creat_post);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageCreatePost = findViewById(R.id.imageCreatePost);
        imageSentCreatePost = findViewById(R.id.imageSentCreatePost);
        imageUser = findViewById(R.id.imageUserCreatePost);
        edtCreatPost = findViewById(R.id.edtCreatePost);
        tvNameUser = findViewById(R.id.tvNameUserCreatePost);
        imageChose = findViewById(R.id.imageChose);

        User cacheUSer = CachedUserManager.getCurrentUser(this);
        String imageUser = cacheUSer.getAvata();
        String nameUser = cacheUSer.getName();


        if (imageUser != null && !imageUser.isEmpty()) {
            Glide.with(this)
                    .load(imageUser)
                    .placeholder(R.drawable.placeholder)
                    .circleCrop()
                    .into(this.imageUser);
        } else {
            this.imageUser.setImageResource(R.drawable.placeholder);
        }
        tvNameUser.setText(nameUser);


        imageChose.setOnClickListener(v -> openFileChooser());
        imageSentCreatePost.setOnClickListener(v -> {
            uploadToCloudinary(imageUri,new OnUploadSuccessListener() {
                @Override
                public void onSuccess(String imageUrl) {
                    String content = edtCreatPost.getText().toString().trim();
                    runOnUiThread(() -> savePostToFirestore(content, imageUrl));
                }
            });
        });

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGE_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageCreatePost.setImageURI(imageUri);
            imageCreatePost.setVisibility(View.VISIBLE);
        }
    }

    public void uploadToCloudinary(Uri imageUri, OnUploadSuccessListener listener) {
        new Thread(() -> {
            try {
                Cloudinary cloudinary = com.example.letmecook.utils.CloudinaryManager.getInstance();

                // Lấy InputStream từ Uri
                InputStream inputStream = getContentResolver().openInputStream(imageUri);

                // Upload trực tiếp từ InputStream (không cần tạo File tạm)
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


    private void savePostToFirestore(String content, String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        User currentUser = CachedUserManager.getCurrentUser(this);

        String postId = db.collection("post").document().getId(); // tạo ID duy nhất
        HashMap<String, Object> post = new HashMap<>();
        post.put("userId", currentUser.getUserId());
        post.put("userName", currentUser.getName());
        post.put("imageUser", currentUser.getAvata());
        post.put("postId", postId);
        post.put("comment", new ArrayList<String>());
        post.put("likes", new ArrayList<String>());
        post.put("content", content);
        post.put("timestamp", String.valueOf(System.currentTimeMillis()));
        post.put("imageUrl", imageUrl);

        db.collection("post").document(postId) // DÙNG document(postId)
                .set(post)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Đăng bài thành công!", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("postCreated", true);
                    setResult(Activity.RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi đăng bài: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
