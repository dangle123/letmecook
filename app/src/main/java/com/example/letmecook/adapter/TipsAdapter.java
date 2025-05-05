package com.example.letmecook.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letmecook.R;

import java.util.ArrayList;

public class TipsAdapter extends RecyclerView.Adapter<TipsAdapter.ViewHolder> {

    private ArrayList<String> danhsachTips;

    public TipsAdapter(ArrayList<String> danhsachTips) {
        this.danhsachTips = danhsachTips;
    }

    @NonNull
    @Override
    public TipsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_buoc_nau, parent, false);
        return new TipsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (danhsachTips != null && position < danhsachTips.size()) {
            String buocnau = danhsachTips.get(position);

            holder.tvbuoc.setText("Tips " + (position + 1) );
            holder.tvbuocnau.setText(buocnau);
        }

    }


    @Override
    public int getItemCount() {
        return danhsachTips.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvbuocnau,tvbuoc;

        public ViewHolder(View itemView) {
            super(itemView);
            tvbuocnau = itemView.findViewById(R.id.tvNoiDung);
            tvbuoc = itemView.findViewById(R.id.tvBuoc);
        }
    }


}
