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

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder> {

    private List<String> genreNames;
    private List<Integer> genreIcons;
    private Context context;

    public GenreAdapter(List<String> genreNames, List<Integer> genreIcons, Context context) {
        this.genreNames = genreNames;
        this.genreIcons = genreIcons;
        this.context = context;
    }

    @NonNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_genre, parent, false);
        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreViewHolder holder, int position) {
        String genre = genreNames.get(position);
        int icon = genreIcons.get(position);

        holder.genreName.setText(genre);
        holder.genreIcon.setImageResource(icon);

        // Handle click event to open GenreSongsActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, GenreSongsActivity.class);
            intent.putExtra("genreName", genre);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return genreNames.size();
    }

    public static class GenreViewHolder extends RecyclerView.ViewHolder {
        TextView genreName;
        ImageView genreIcon;

        public GenreViewHolder(@NonNull View itemView) {
            super(itemView);
            genreName = itemView.findViewById(R.id.genre_name);
            genreIcon = itemView.findViewById(R.id.genre_icon);
        }
    }
}
