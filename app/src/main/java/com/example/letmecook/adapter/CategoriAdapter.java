package com.example.letmecook.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.letmecook.Model.LoaiMonAn;
import com.example.letmecook.R;

import java.util.List;

public class CategoriAdapter extends RecyclerView.Adapter<CategoriAdapter.ViewHolder> {
    private Context context;

    private int selectedPosition = RecyclerView.NO_POSITION;
    private List<LoaiMonAn> loaiMonAnList;
    private OnItemClickListener listener;
    private int selectedCategoryId = -1;

    public CategoriAdapter(Context context, List<LoaiMonAn> loaiMonAnList,int selectedCategoryId) {
        this.context = context;
        this.loaiMonAnList = loaiMonAnList;
        this.selectedCategoryId = selectedCategoryId;
    }

    public void setData(List<LoaiMonAn> newList) {
        this.loaiMonAnList = newList;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int categoryId);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_categori, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LoaiMonAn loaiMonAn = loaiMonAnList.get(position);
        holder.tenLoaiTextView.setText(loaiMonAn.getTen());
        int categoryId = -1;
        try {
            categoryId = Integer.parseInt(loaiMonAn.getId());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }



        if (selectedCategoryId == position) {
                selectedPosition = position;
                holder.tenLoaiTextView.setTextColor( Color.WHITE);
                holder.linearLayout.setBackgroundColor(Color.parseColor("#FFA233"));
            selectedCategoryId = -1;

        } else if(selectedPosition == position ) {
            holder.tenLoaiTextView.setTextColor( Color.WHITE);
            holder.linearLayout.setBackgroundColor(Color.parseColor("#FFA233"));

        } else {
            holder.tenLoaiTextView.setTextColor( Color.BLACK);
            holder.linearLayout.setBackgroundColor(Color.parseColor("#ECEBE6"));
        }


        Glide.with(context)
                .load(loaiMonAn.getUrl())
                .placeholder(R.drawable.placeholder)
                .transform(new RoundedCorners(30))
                .into(holder.imageView);

        int finalCategoryId = categoryId;
        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            if (previousSelected != RecyclerView.NO_POSITION) {
                notifyItemChanged(previousSelected);
            }
            notifyItemChanged(selectedPosition);


            if (listener != null) {
                listener.onItemClick(finalCategoryId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return loaiMonAnList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tenLoaiTextView;
        ImageView imageView;
        LinearLayout linearLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tenLoaiTextView = itemView.findViewById(R.id.tenCategori);
            imageView = itemView.findViewById(R.id.imageCategori);
            linearLayout = itemView.findViewById(R.id.bachgroidCategori);
        }
    }
}
