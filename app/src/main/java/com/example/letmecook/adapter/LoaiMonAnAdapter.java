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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.letmecook.Model.DanhSachMonAn;
import com.example.letmecook.Model.DanhSachNguyenLieu;
import com.example.letmecook.Model.LoaiMonAn;
import com.example.letmecook.R;

import java.util.List;

public class LoaiMonAnAdapter extends RecyclerView.Adapter<LoaiMonAnAdapter.ViewHolder> {
    private Context context;
    private List<LoaiMonAn> loaiMonAnList;
    private OnItemClickListener listener;
    public LoaiMonAnAdapter(Context context, List<LoaiMonAn> loaiMonAnList) {
        this.context = context;
        this.loaiMonAnList = loaiMonAnList;
    }
    public void setData(List<LoaiMonAn> newList) {
        this.loaiMonAnList = newList;
        notifyDataSetChanged();
    }
    public interface OnItemClickListener {

        void onItemClick(LoaiMonAn LoaiMonAn);

    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        Log.d("listenner","checkHam" );
        this.listener = listener;
    }

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_loai_mon_an, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LoaiMonAn loaiMonAn = loaiMonAnList.get(position);
        holder.tenLoaiTextView.setText(loaiMonAn.getTen());


        Glide.with(context)
                .load(loaiMonAn.getUrl())
                .placeholder(R.drawable.placeholder)
                .transform(new RoundedCorners(30))
                .into(holder.imageView);


        holder.bind(loaiMonAn, listener);
    }

    @Override
    public int getItemCount() {
        return loaiMonAnList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tenLoaiTextView;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tenLoaiTextView = itemView.findViewById(R.id.tenLoaiTextView);
            imageView = itemView.findViewById(R.id.imageViewLoaiMonAn);


        }

        public void bind(final LoaiMonAn LoaiMonAn, final OnItemClickListener listener) {


            itemView.setOnClickListener(v -> {
                if (listener != null) {

                    listener.onItemClick(LoaiMonAn);
                }
            });
        }
    }
}

