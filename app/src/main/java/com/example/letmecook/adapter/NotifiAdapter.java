package com.example.letmecook.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letmecook.Model.DanhSachMonAn;
import com.example.letmecook.Model.danhSachNotifi;
import com.example.letmecook.R;

import java.util.ArrayList;
import java.util.List;

public class NotifiAdapter extends RecyclerView.Adapter<NotifiAdapter.ViewHolder>{

    private List<danhSachNotifi> danhsachNotifi;
    private Context context;
    public NotifiAdapter(Context context, List<danhSachNotifi> danhsachNotifi) {
        this.context = context;
        this.danhsachNotifi = danhsachNotifi;

    }

    @NonNull
    @Override
    public NotifiAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notifi, parent, false);
        return new NotifiAdapter.ViewHolder(view);
    }
    public void setData(List<danhSachNotifi>newList) {
        this.danhsachNotifi = newList;
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(@NonNull NotifiAdapter.ViewHolder holder, int position) {
        danhSachNotifi noti = danhsachNotifi.get(position);

        if ( noti.type.equals("0")){
            holder.tvType.setText("Yêu cầu món mới");
        } else {
            holder.tvType.setText("Yêu cầu nạp xu ");
        }
        Log.d("checkstatus","status" + noti.status);
        switch (noti.status) {
            case "0":
                holder.tvCho.setBackgroundColor(Color.parseColor("#0071BB"));
                holder.tvDa.setBackgroundColor(Color.parseColor("#E7E8EB"));
                holder.tvTu.setBackgroundColor(Color.parseColor("#E7E8EB"));
                break;

            case "1" :
                holder.tvCho.setBackgroundColor(Color.parseColor("#E7E8EB"));
                holder.tvDa.setBackgroundColor(Color.parseColor("#00A534"));
                holder.tvTu.setBackgroundColor(Color.parseColor("#E7E8EB"));
            break;
            default: holder.tvCho.setBackgroundColor(Color.parseColor("#E7E8EB"));
                holder.tvDa.setBackgroundColor(Color.parseColor("#E7E8EB"));
                holder.tvTu.setBackgroundColor(Color.parseColor("#E8002E"));
        }

        holder.tvNote.setText(noti.note);
        holder.tvTitle.setText(noti.title);
        holder.tvDirc.setText(noti.decrible);
    }

    @Override
    public int getItemCount() {
        return danhsachNotifi.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle,tvDirc,tvNote,tvType,tvDa,tvTu,tvCho;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDirc = itemView.findViewById(R.id.TvDercible);
            tvNote = itemView.findViewById(R.id.tvNote);
            tvType = itemView.findViewById(R.id.tvTypeTitle);
            tvCho = itemView.findViewById(R.id.tvCho);
            tvDa = itemView.findViewById(R.id.tvDa);
            tvTu = itemView.findViewById(R.id.tvTu);
        }
    }


}
