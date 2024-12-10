package com.tdtu.my_music_player.Playlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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

        // Bind song data to the views
        title.setText(song.getTitle());
        artist.setText(song.getArtist());
        albumCover.setImageResource(song.getAlbumCoverResource());

        return convertView;
    }
}
