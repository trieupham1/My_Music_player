package com.tdtu.my_music_player.Playlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tdtu.my_music_player.R;

import java.util.List;

public class PlaylistNameAdapter extends RecyclerView.Adapter<PlaylistNameAdapter.PlaylistViewHolder> {

    private final Context context;
    private List<String> playlistNames;
    private final OnPlaylistClickListener listener;

    // Interface for click listener
    public interface OnPlaylistClickListener {
        void onPlaylistClick(String playlistName);
    }

    public PlaylistNameAdapter(Context context, List<String> playlistNames, OnPlaylistClickListener listener) {
        this.context = context;
        this.playlistNames = playlistNames;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_playlist_name, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        String playlistName = playlistNames.get(position);
        holder.playlistNameTextView.setText(playlistName);

        holder.itemView.setOnClickListener(v -> listener.onPlaylistClick(playlistName));
    }

    @Override
    public int getItemCount() {
        return playlistNames != null ? playlistNames.size() : 0;
    }

    // Method to update the list of playlists
    public void updatePlaylists(List<String> newPlaylists) {
        this.playlistNames = newPlaylists;
        notifyDataSetChanged(); // Notify adapter to refresh the RecyclerView
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        TextView playlistNameTextView;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistNameTextView = itemView.findViewById(R.id.tv_playlist_name);
        }
    }
}
