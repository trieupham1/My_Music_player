package com.tdtu.my_music_player;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {

    private List<String> categoriesList;
    private List<Integer> categoryIcons;
    private Context context;

    public CategoriesAdapter(List<String> categoriesList, List<Integer> categoryIcons, Context context) {
        this.categoriesList = categoriesList;
        this.categoryIcons = categoryIcons;
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = categoriesList.get(position);
        int icon = categoryIcons.get(position);

        holder.categoryName.setText(category);
        holder.categoryIcon.setImageResource(icon);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MusicCategoryActivity.class);
            intent.putExtra("categoryName", category);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        ImageView categoryIcon;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.category_name);
            categoryIcon = itemView.findViewById(R.id.category_icon);
        }
    }
}
