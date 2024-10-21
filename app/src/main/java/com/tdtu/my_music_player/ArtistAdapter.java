package com.tdtu.my_music_player;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {

    private final List<String> artistNames;
    private final List<Integer> artistIcons;
    private final Context context;

    public interface OnArtistClickListener {
        void onArtistClick(String artist);
    }

    private final OnArtistClickListener listener;

    public ArtistAdapter(List<String> artistNames, List<Integer> artistIcons, Context context, OnArtistClickListener listener) {
        this.artistNames = artistNames;
        this.artistIcons = artistIcons;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_artist, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        String artist = artistNames.get(position);
        int icon = artistIcons.get(position);

        holder.artistName.setText(artist);
        holder.artistIcon.setImageResource(icon);

        holder.itemView.setOnClickListener(v -> listener.onArtistClick(artist));
    }

    @Override
    public int getItemCount() {
        return artistNames.size();
    }

    public static class ArtistViewHolder extends RecyclerView.ViewHolder {
        TextView artistName;
        ImageView artistIcon;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            artistName = itemView.findViewById(R.id.artist_name);
            artistIcon = itemView.findViewById(R.id.artist_icon);
        }
    }
}
