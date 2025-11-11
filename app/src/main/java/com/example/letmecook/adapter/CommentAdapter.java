package com.example.letmecook.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.letmecook.Model.DanhSachBinhLuan;
import com.example.letmecook.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<DanhSachBinhLuan> commentList;
    private Context context;

    public CommentAdapter(List<DanhSachBinhLuan> commentList, Context context) {
        this.commentList = commentList;
        this.context = context;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment_post, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        DanhSachBinhLuan comment = commentList.get(position);
        holder.tvNameUserComment.setText(comment.getNameUser());
        holder.tvContentComment.setText(comment.getComment());
        Log.d("danhsachcomment","danh sach " + comment.getComment());

        try {
            long timestamp = Long.parseLong(comment.getTimestamp());
            Date date = new Date(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
            holder.tvTimeComment.setText(sdf.format(date));
        } catch (Exception e) {
            holder.tvTimeComment.setText("");
        }


        if (comment.getAvtUser() != null && !comment.getAvtUser().isEmpty()) {
            Glide.with(context)
                    .load(comment.getAvtUser())
                    .placeholder(R.drawable.placeholder)
                    .circleCrop()
                    .into(holder.imgAvtUserComment);
        } else {
            holder.imgAvtUserComment.setImageResource(R.drawable.placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvtUserComment;
        TextView tvNameUserComment, tvContentComment, tvTimeComment;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvtUserComment = itemView.findViewById(R.id.imageUsercomment);
            tvNameUserComment = itemView.findViewById(R.id.tvNameUserComment);
            tvContentComment = itemView.findViewById(R.id.tvComment);
            tvTimeComment = itemView.findViewById(R.id.tvTimeComment);
        }
    }
}
