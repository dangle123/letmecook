package com.example.letmecook.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.letmecook.R;
import com.example.letmecook.cache.AppDatabase;
import com.example.letmecook.cache.RecipeEntity;

import java.util.List;

public class OfflineRecipeAdapter extends RecyclerView.Adapter<OfflineRecipeAdapter.ViewHolder> {

    private Context context;
    private List<RecipeEntity> recipes;
    private AppDatabase db;
    private OnItemClickListener listener;

    public OfflineRecipeAdapter(Context context, List<RecipeEntity> recipes, AppDatabase db) {
        this.context = context;
        this.recipes = recipes;
        this.db = db;
    }
    public interface OnItemClickListener {
        void onItemClick(RecipeEntity recipe);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_save_offline, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecipeEntity recipe = recipes.get(position);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(recipe);
            }
        });

        holder.txtName.setText(recipe.title);
        holder.txtTime.setText("⏱ " + recipe.cookingTime);
        Glide.with(holder.imageRecipe.getContext())
                .load(recipe.imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.imageRecipe);
        holder.imageDelete.setOnClickListener(v -> {
            new Thread(() -> {

                db.recipeDao().deleteRecipeById(recipe.id);

                ((Activity) v.getContext()).runOnUiThread(() -> {
                    recipes.remove(holder.getAdapterPosition());
                    notifyItemRemoved(holder.getAdapterPosition());
                    Toast.makeText(v.getContext(), "Đã xóa công thức", Toast.LENGTH_SHORT).show();
                });
            }).start();
        });

    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }
    public void setData(List<RecipeEntity> newList) {
        this.recipes.clear();
        this.recipes.addAll(newList);
        notifyDataSetChanged();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtTime;
        ImageView imageDelete,imageRecipe;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.tvTitle);
            txtTime = itemView.findViewById(R.id.tvTime);
            imageDelete = itemView.findViewById(R.id.imageDelete);
            imageRecipe = itemView.findViewById(R.id.ivRecipe);
        }
    }
}
