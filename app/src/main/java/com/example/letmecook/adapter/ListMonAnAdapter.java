package com.example.letmecook.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.letmecook.Activity.DetailItem;
import com.example.letmecook.Model.DanhSachMonAn;
import com.example.letmecook.R;
import com.example.letmecook.cache.AppDatabase;
import com.example.letmecook.cache.RecipeEntity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class ListMonAnAdapter extends RecyclerView.Adapter<ListMonAnAdapter.ViewHolder> {
    private Context context;
    private List<DanhSachMonAn> DanhSachMonAn;

    private OnItemClickListener listener;

private boolean  book = true;


    private boolean isActive = true;

    public void setActive(boolean active) {
        this.isActive = active;
        notifyDataSetChanged();
    }
    public interface OnItemClickListener {

        void onItemClick(DanhSachMonAn DanhSachMonAn,int position);

    }
    public void setOnItemClickListener(OnItemClickListener listener) {

        this.listener = listener;
    }

    // code cu
    public ListMonAnAdapter(Context context, List<DanhSachMonAn> DanhSachMonAn) {
        this.context = context;
        this.DanhSachMonAn = DanhSachMonAn;
    }
    public void setData(List<DanhSachMonAn> newList) {
        this.DanhSachMonAn = newList;
        notifyDataSetChanged();
    }

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mon_an, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (!isActive) {
            holder.itemView.setOnClickListener(null);
        }

        DanhSachMonAn monAn = DanhSachMonAn.get(position);



        Glide.with(context)
                .load(monAn.getHinhAnh())
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .transform(new RoundedCorners(30))
                .into(holder.hinhAnh);
        holder.bind(monAn, listener,position);
    }

    @Override
    public int getItemCount() {
        return DanhSachMonAn.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconLike;
        ImageView hinhAnh;
        ImageView iconLove;
        ImageView iconDown;
        TextView tenMonAn,tvView,tvCoin,tvLike,tvTimecook;



        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser  user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        private  String newFavorite = "";
        private boolean isLove = false;
        private boolean isLiked = false;
        public LinearLayout viewMonAn;
        private DanhSachMonAn currentMonAn;
        private int   valuetam = 1;
        public ViewHolder(View itemView) {
            super(itemView);
            hinhAnh = itemView.findViewById(R.id.imageMonAn);
            tenMonAn = itemView.findViewById(R.id.textTenMonAn);
            iconLike = itemView.findViewById(R.id.iconlike);
            iconLove = itemView.findViewById(R.id.iconlove);
            tvCoin = itemView.findViewById(R.id.tvCoin);
            tvLike = itemView.findViewById(R.id.tvLike);
            tvView = itemView.findViewById(R.id.tvView);
            tvTimecook = itemView.findViewById(R.id.tvTimeCook);
            viewMonAn = itemView.findViewById(R.id.viewMonAn);
            iconDown = itemView.findViewById(R.id.iconDown);
            iconDown.setOnClickListener(v -> saveOffline(currentMonAn));
            iconLike.setOnClickListener(v -> {
                isLiked = !isLiked;
                if (isLiked) {
                    String userId = user.getUid();
                    iconLike.setImageResource(R.drawable.bookmark);
                    db.collection("user").document(userId)
                            .update("favorites", FieldValue.arrayUnion(newFavorite))
                            .addOnSuccessListener(aVoid ->
                                    Toast.makeText(v.getContext(), "Đã lưu thành công!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(v.getContext(), "Lỗi khi lưu!", Toast.LENGTH_SHORT).show());

                } else {
                    iconLike.setImageResource(R.drawable.save);
                    String userId = user.getUid();

                    db.collection("user").document(userId)
                            .update("favorites", FieldValue.arrayRemove(newFavorite))
                            .addOnSuccessListener(aVoid ->
                                    Toast.makeText(v.getContext(), "Đã xoa thành công!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(v.getContext(), "Lỗi khi lưu!", Toast.LENGTH_SHORT).show());
                }


            });



            iconLove.setOnClickListener(v -> {
                isLove = !isLove;
                if (isLove) {

                    db.collection("recipes ")
                            .whereEqualTo("id", currentMonAn.getId())
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots ->{
                                if ( !queryDocumentSnapshots.isEmpty()){
                                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                                    String documentId = documentSnapshot.getId();

                                    db.collection("recipes ").document(documentId)
                                            .update("like", FieldValue.increment(1))
                                            .addOnSuccessListener(aVoid -> {


                                            });

                                }

                            });
                    String userId = user.getUid();
                    db.collection("user").document(userId).update("like",FieldValue.arrayUnion(newFavorite));
                   iconLove.setImageResource(R.drawable.like);
                    Toast.makeText(v.getContext(), "Đã Like ", Toast.LENGTH_SHORT).show();
                    valuetam = valuetam + 1;
                    tvLike.setText(String.valueOf(valuetam));

                } else {

                    db.collection("recipes ")
                            .whereEqualTo("id", currentMonAn.getId())
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots ->{
                                if ( !queryDocumentSnapshots.isEmpty()){
                                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                                    String documentId = documentSnapshot.getId();

                                    db.collection("recipes ").document(documentId)
                                            .update("like", FieldValue.increment(-1))
                                            .addOnSuccessListener(aVoid -> {


                                            });

                                }

                            });
                    String userId = user.getUid();
                    db.collection("user").document(userId)
                                    .update("like",FieldValue.arrayRemove(newFavorite));
                    valuetam = valuetam -1;
                    tvLike.setText(String.valueOf(valuetam));
                    iconLove.setImageResource(R.drawable.heart);
                }
            });
        }

        public  void actionView(final DanhSachMonAn monan ){

        }

        private void saveOffline(DanhSachMonAn monan) {
            Context context = itemView.getContext();
            AppDatabase db = AppDatabase.getInstance(context);

            RecipeEntity recipe = new RecipeEntity();
            recipe.id = monan.getId();
            recipe.title = monan.getTen();
            recipe.imageUrl = monan.getHinhAnh();
            recipe.categoryId = monan.getCategoriesId();
            recipe.cookingTime = monan.getTimecook();
            recipe.coin = monan.getCoinUser();
            recipe.rating = monan.getLikeUser();
            recipe.like = monan.getLikeUser();
            recipe.view = monan.getViewUser();
            recipe.ingredients = monan.getNguyenlieu();
            recipe.instructions = monan.getBuocnau();
            recipe.tips = monan.getTips();

            Completable.fromAction(() -> db.recipeDao().insert(recipe))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        Toast.makeText(context, "Đã lưu ngoại tuyến!", Toast.LENGTH_SHORT).show();
                    }, throwable -> {
                        Toast.makeText(context, "Lỗi lưu ngoại tuyến!", Toast.LENGTH_SHORT).show();
                    });
        }


        public void bind(final DanhSachMonAn monan, final OnItemClickListener listener,int position) {
            this.currentMonAn = monan;
            tenMonAn.setText(monan.getTen());
            newFavorite = monan.getId();

            tvView.setText(String.valueOf(monan.getViewUser()));
            tvLike.setText(String.valueOf(monan.getLikeUser()));
            tvCoin.setText(String.valueOf(monan.getCoinUser()));

            valuetam = monan.getLikeUser();

            tvTimecook.setText(monan.getTimecook());
            isLove = monan.isCheckLove();
            if (isLove){
                iconLove.setImageResource(R.drawable.like);
            }else {
                iconLove.setImageResource(R.drawable.heart);
            }
            isLiked = monan.isCheckLike();
            if (isLiked){
                iconLike.setImageResource(R.drawable.bookmark);
            } else {
                iconLike.setImageResource(R.drawable.save);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {

                    listener.onItemClick(monan, position);
                }
            });
        }


    }
}



