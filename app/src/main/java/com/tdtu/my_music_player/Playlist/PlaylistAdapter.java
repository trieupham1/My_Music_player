package com.tdtu.my_music_player.Playlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tdtu.my_music_player.R;
import com.tdtu.my_music_player.SearchSong.Song;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private final Context context;
    private final List<Song> playlist;
    private final OnItemClickListener onItemClickListener;

    // Interface to handle item clicks
    public interface OnItemClickListener {
        void onItemClick(Song song, int position);
    }

    public PlaylistAdapter(Context context, List<Song> playlist, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.playlist = playlist;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        // Get the song at the current position
        Song song = playlist.get(position);

        // Bind song data to the views
        holder.songTitleTextView.setText(song.getTitle());
        holder.artistTextView.setText(song.getArtist());

        // Bind the album cover resource if available
        if (song.getAlbumCoverResource() != 0) {
            holder.albumCoverImageView.setImageResource(song.getAlbumCoverResource());
        } else {
            // Use a placeholder if no album cover resource is provided
            holder.albumCoverImageView.setImageResource(R.drawable.mice);
        }

        // Set up the item click listener
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(song, position));
    }

    @Override
    public int getItemCount() {
        return playlist == null ? 0 : playlist.size();
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        TextView songTitleTextView;
        TextView artistTextView;
        ImageView albumCoverImageView;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitleTextView = itemView.findViewById(R.id.song_title);
            artistTextView = itemView.findViewById(R.id.song_artist);
            albumCoverImageView = itemView.findViewById(R.id.album_cover); // Bind album cover
        }
    }
}