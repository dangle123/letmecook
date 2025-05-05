package com.example.letmecook.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.letmecook.Model.DanhSachMonAn;
import com.example.letmecook.R;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.letmecook.Model.DanhSachMonAn;
import com.example.letmecook.R;

public class NguyenLieuAdapter extends RecyclerView.Adapter<NguyenLieuAdapter.ViewHolder> {
    private ArrayList<String> danhSachNguyenLieu;

    public NguyenLieuAdapter(ArrayList<String> danhSachNguyenLieu) {
        this.danhSachNguyenLieu = danhSachNguyenLieu;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nguyen_lieu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String nguyenLieu = danhSachNguyenLieu.get(position);
        holder.txtNguyenLieu.setText(nguyenLieu);
    }

    @Override
    public int getItemCount() {
        return danhSachNguyenLieu.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNguyenLieu;

        public ViewHolder(View itemView) {
            super(itemView);
            txtNguyenLieu = itemView.findViewById(R.id.tvTenNguyenLieu);
        }
    }
}
