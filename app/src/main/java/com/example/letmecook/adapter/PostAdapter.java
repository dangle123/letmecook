package com.example.letmecook.adapter;

import com.example.letmecook.Activity.CommentActivity;
import com.example.letmecook.Model.User;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.letmecook.Model.DanhSachPost;
import com.example.letmecook.R;
import com.example.letmecook.cache.CachedUserManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.security.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_CREATE_POST = 0;  // Cell "bạn đang nghĩ gì?"
    private static final int TYPE_POST = 1;         // Cell bài post

    private Context context;
    private ArrayList<DanhSachPost> danhsachPost;

    public PostAdapter(Context context, ArrayList<DanhSachPost> danhsachPost) {
        this.context = context;
        this.danhsachPost = danhsachPost;
    }

    @Override
    public int getItemViewType(int position) {
        // Vị trí đầu tiên là ô “bạn đang nghĩ gì?”
        if (position == 0) return TYPE_CREATE_POST;
        else return TYPE_POST;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_CREATE_POST) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_post_box, parent, false);
            return new CreatePostViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_post_new, parent, false);
            return new PostViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CreatePostViewHolder) {

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            String userId = currentUser.getUid();




            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("users").child(userId);
            ref.keepSynced(true); // giữ đồng bộ dữ liệu người dùng

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    // dữ liệu này sẽ lấy từ cache nếu offline
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w("Firebase", "Failed to read value.", error.toException());
                }
            });
            CreatePostViewHolder vh = (CreatePostViewHolder) holder;
            User cachedUser = CachedUserManager.getCurrentUser();
            String image = cachedUser.getAvata();
            Log.e("dataCache", "data image" + image);
            if (cachedUser != null) {
                String imageUserUrl = cachedUser.getAvata();

                if (imageUserUrl != null && !imageUserUrl.isEmpty()) {
                    Glide.with(context)
                            .load(imageUserUrl)
                            .placeholder(R.drawable.placeholder)
                            .circleCrop()
                            .into(vh.imageUserPost); // hoặc imageView khác tùy layout
                } else {
                    vh.imageUserPost.setImageResource(R.drawable.placeholder);
                }
            }

            vh.txtWhatsOnYourMind.setOnClickListener(v -> {
                // TODO: mở màn hình tạo bài post mới
                Intent intent = new Intent(context, CommentActivity.class);

                // Nếu bạn muốn truyền thêm thông tin (ví dụ id bài post hoặc user):
                intent.putExtra("userId", userId);

                context.startActivity(intent);
            });
        } else if (holder instanceof PostViewHolder) {
            DanhSachPost post = danhsachPost.get(position - 1); // trừ 1 vì ô đầu là “create post”

            PostViewHolder vh = (PostViewHolder) holder;

            vh.tvNamePost.setText(post.getUserName());
            vh.tvLike.setText( post.getLikes().size() + " Lượt thích");
            vh.tvComment.setText( post.getLikes().size()+" Bình luận");
            vh.tvContentPost.setText(post.getContent());

            Long timestamp = Long.valueOf(post.getTimestamp());
            if (timestamp != null) {
                Date date = new Date(timestamp);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
                String formattedDate = sdf.format(date);
                vh.tvTime.setText(formattedDate);
            } else {
                vh.tvTime.setText("Không xác định");
            }


            // Nếu có ảnh thì hiển thị bằng Glide
            if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
                vh.imgPost.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(post.getImageUrl())
                        .into(vh.imgPost);
            } else {
                vh.imgPost.setVisibility(View.GONE);
            }

            if (post.getImageUser() != null && !post.getImageUser().isEmpty()) {
                vh.imgUser.setVisibility(View.VISIBLE);
                Glide.with(vh.itemView.getContext())
                        .load(post.getImageUser())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .circleCrop() // làm tròn avatar
                        .into(vh.imgUser);
            } else {
                vh.imgUser.setImageResource(R.drawable.placeholder);
            }

        }
    }

    @Override
    public int getItemCount() {
        // +1 vì có thêm ô “Bạn đang nghĩ gì?”
        return danhsachPost.size() + 1;
    }

    // ViewHolder cho ô "Bạn đang nghĩ gì?"
    public static class CreatePostViewHolder extends RecyclerView.ViewHolder {
        TextView txtWhatsOnYourMind;
        ImageView imageUserPost;
        public CreatePostViewHolder(@NonNull View itemView) {
            super(itemView);
            txtWhatsOnYourMind = itemView.findViewById(R.id.edtPostBox);
            imageUserPost = itemView.findViewById(R.id.imageUserPost);
        }
    }

    // ViewHolder cho bài post
    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView tvContentPost, tvNamePost, tvTime, tvLike, tvComment;
        ImageView imgPost,imgUser;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContentPost = itemView.findViewById(R.id.tvContentPost);
            tvNamePost = itemView.findViewById(R.id.tvNamePost);
            tvTime = itemView.findViewById(R.id.tvTimePost);
            tvLike = itemView.findViewById(R.id.tvLikePost);
            tvComment = itemView.findViewById(R.id.tvCommemtPost);
            imgPost = itemView.findViewById(R.id.imageContentPost);
            imgUser = itemView.findViewById(R.id.imageUser);
        }
    }
}
