package com.example.letmecook.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letmecook.R;

import java.util.ArrayList;

public class BuocNauAdapter extends RecyclerView.Adapter<BuocNauAdapter.ViewHolder> {

    private ArrayList<String> danhsachbuocnau;

    public BuocNauAdapter(ArrayList<String> danhsachbuocnau) {
        this.danhsachbuocnau = danhsachbuocnau;
    }

    @NonNull
    @Override
    public BuocNauAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_buoc_nau, parent, false);
        return new BuocNauAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        if (danhsachbuocnau != null && position < danhsachbuocnau.size()) {
            String buocnau = danhsachbuocnau.get(position);

            holder.tvbuoc.setText("Bước " + (position + 1) );
            holder.tvbuocnau.setText(buocnau);
        }
    }



    @Override
    public int getItemCount() {
        return danhsachbuocnau.size();
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
