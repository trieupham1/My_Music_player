package com.tdtu.my_music_player.Playlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tdtu.my_music_player.R;
import com.tdtu.my_music_player.SearchSong.Song;

import java.util.List;

public class PlaylistAdapter extends BaseAdapter {

    private final List<Song> songs;
    private final Context context;

    public PlaylistAdapter(Context context, List<Song> songs) {
        this.context = context;
        this.songs = songs;
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return songs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_song, parent, false);
        }

        // Get the current song
        Song song = songs.get(position);

        // Find views in the layout
        TextView title = convertView.findViewById(R.id.song_title);
        TextView artist = convertView.findViewById(R.id.song_artist);
        ImageView albumCover = convertView.findViewById(R.id.album_cover);
        Button deleteButton = convertView.findViewById(R.id.delete_button);

// Bind song data to the views
        title.setText(song.getTitle());
        artist.setText(song.getArtist());
        albumCover.setImageResource(song.getAlbumCoverResource());

        // Set up the delete button click listener
        deleteButton.setOnClickListener(v -> {
            // Remove the song from the playlist
            PlaylistManager.getInstance().getPlaylist().remove(position);

            // Notify the adapter to refresh the list
            notifyDataSetChanged();

            // Show a Toast message
            Toast.makeText(context, song.getTitle() + " removed from playlist", Toast.LENGTH_SHORT).show();
        });

        return convertView;
    }
}
