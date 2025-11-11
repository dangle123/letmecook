package com.example.letmecook.adapter;



import static androidx.core.app.ActivityCompat.startActivityForResult;
import static androidx.core.content.ContextCompat.startActivity;

import com.example.letmecook.Activity.CreatPostActivity;
import com.example.letmecook.Activity.MainActivity;
import com.example.letmecook.Model.DanhSachBinhLuan;
import com.example.letmecook.Model.User;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.letmecook.Model.DanhSachPost;
import com.example.letmecook.R;
import com.example.letmecook.cache.CachedUserManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


import org.w3c.dom.Comment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_CREATE_POST = 0;
    private static final int TYPE_POST = 1;

    private Context context;
    private ArrayList<DanhSachPost> danhsachPost;

    public PostAdapter(Context context, ArrayList<DanhSachPost> danhsachPost) {
        this.context = context;
        this.danhsachPost = danhsachPost;
    }

    @Override
    public int getItemViewType(int position) {

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
    private void loadComments(CollectionReference commentsRef,
                              List<DanhSachBinhLuan> commentList,
                              CommentAdapter CommentAdapter) {

        commentsRef
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    commentList.clear();

                    for (DocumentSnapshot doc : querySnapshot) {
                        DanhSachBinhLuan comment = doc.toObject(DanhSachBinhLuan.class);
                        if (comment != null) {
                            commentList.add(comment);

                        }
                    }

                    CommentAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Lỗi tải comment: ", e));
    }

    public interface OnPostBoxClickListener {
        void onCreatePostClicked();
    }

    private OnPostBoxClickListener listener;

    public void setOnPostBoxClickListener(OnPostBoxClickListener listener) {
        this.listener = listener;
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CreatePostViewHolder) {

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            String userId = currentUser.getUid();




            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("users").child(userId);
            ref.keepSynced(true);

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w("Firebase", "Failed to read value.", error.toException());
                }
            });
            CreatePostViewHolder vh = (CreatePostViewHolder) holder;
            User cachedUser = CachedUserManager.getCurrentUser(context);
            String image = cachedUser.getAvata();
            Log.e("dataCache", "data image" + image);
            if (cachedUser != null) {
                String imageUserUrl = cachedUser.getAvata();

                if (imageUserUrl != null && !imageUserUrl.isEmpty()) {
                    Glide.with(context)
                            .load(imageUserUrl)
                            .placeholder(R.drawable.placeholder)
                            .circleCrop()
                            .into(vh.imageUserPost);
                } else {
                    vh.imageUserPost.setImageResource(R.drawable.placeholder);
                }
            }

            vh.viewPostBox.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCreatePostClicked();
                }

            });

        } else if (holder instanceof PostViewHolder) {
            DanhSachPost post = danhsachPost.get(position - 1);
            User currentUser = CachedUserManager.getCurrentUser(context);
            String userId = currentUser.getUserId();
            PostViewHolder vh = (PostViewHolder) holder;
            String postId = post.getPostId();


             DocumentSnapshot lastVisible = null;
            boolean isLoading = false;
             List<DanhSachBinhLuan> commentList = new ArrayList<>();
            CommentAdapter adapter;



            vh.viewComment.setOnClickListener(v -> {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
               CollectionReference commentsRef = db.collection("post")
                        .document(postId)
                        .collection("comments");

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
                View view = LayoutInflater.from(context).inflate(R.layout.item_comment_detail, null);
                bottomSheetDialog.setContentView(view);
                bottomSheetDialog.getWindow()
                        .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


                RecyclerView recyclerViewComment = view.findViewById(R.id.recyclerViewComment);
                TextView tvUserPost = view.findViewById(R.id.tvUserPost);
                ImageView imgSent = view.findViewById(R.id.imgSentComment);
                EditText edtComment = view.findViewById(R.id.edtCommentPost);


                List<DanhSachBinhLuan> danhsachComment = new ArrayList<>();
                CommentAdapter adapterComment = new CommentAdapter(danhsachComment, context);
                recyclerViewComment.setLayoutManager(new LinearLayoutManager(context));
                recyclerViewComment.setAdapter(adapterComment);
                recyclerViewComment.setAdapter(adapterComment);
                loadComments(commentsRef, danhsachComment, adapterComment);

                tvUserPost.setText("Bài viết của " + post.getUserName());
                edtComment.addTextChangedListener(new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.toString().trim().length() > 0) {
                            imgSent.setImageResource(R.drawable.sen2);
                        } else {
                            imgSent.setImageResource(R.drawable.sent1);
                        }
                    }
                    @Override public void afterTextChanged(Editable s) {}
                });

                imgSent.setOnClickListener(v1 -> {
                    String content = edtComment.getText().toString().trim();
                    if (content.isEmpty()) return;

                    DanhSachBinhLuan comment = new DanhSachBinhLuan(
                            userId,
                            currentUser.getName(),
                            content,
                            String.valueOf(System.currentTimeMillis()),
                            currentUser.getAvata()
                    );

                    commentsRef.add(comment)
                            .addOnSuccessListener(docRef -> {
                                edtComment.setText("");
                                loadComments(commentsRef, danhsachComment, adapterComment);
                            })
                            .addOnFailureListener(e -> Log.e("Firestore", "Error adding comment", e));
                });

                bottomSheetDialog.show();
            });



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

        return danhsachPost.size() + 1;
    }


    public static class CreatePostViewHolder extends RecyclerView.ViewHolder {
        TextView txtWhatsOnYourMind;
        View viewPostBox;
        ImageView imageUserPost;
        public CreatePostViewHolder(@NonNull View itemView) {
            super(itemView);
            viewPostBox = itemView.findViewById(R.id.viewPostBox);
            txtWhatsOnYourMind = itemView.findViewById(R.id.edtPostBox);
            imageUserPost = itemView.findViewById(R.id.imageUserPost);
        }
    }


    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView tvContentPost, tvNamePost, tvTime, tvLike, tvComment;
        ImageView imgPost,imgUser;

        LinearLayout viewComment, viewLike;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContentPost = itemView.findViewById(R.id.tvContentPost);
            tvNamePost = itemView.findViewById(R.id.tvNamePost);
            tvTime = itemView.findViewById(R.id.tvTimePost);
            tvLike = itemView.findViewById(R.id.tvLikePost);
            tvComment = itemView.findViewById(R.id.tvCommemtPost);
            imgPost = itemView.findViewById(R.id.imageContentPost);
            imgUser = itemView.findViewById(R.id.imageUser);
            viewComment = itemView.findViewById(R.id.viewComment);
            viewLike = itemView.findViewById(R.id.viewLike);
        }
    }
}
