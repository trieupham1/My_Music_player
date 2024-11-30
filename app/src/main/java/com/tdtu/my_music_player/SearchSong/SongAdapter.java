package com.tdtu.my_music_player.SearchSong;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tdtu.my_music_player.R;

import java.util.List;

public class SongAdapter extends BaseAdapter {

    private Context context;
    private List<Song> songs;
    private OnSongClickListener listener;

    public interface OnSongClickListener {
        void onSongClick(Song song);
    }

    public SongAdapter(Context context, List<Song> songs, OnSongClickListener listener) {
        this.context = context;
        this.songs = songs;
        this.listener = listener;
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
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder();
            holder.albumCover = convertView.findViewById(R.id.album_cover);
            holder.songTitle = convertView.findViewById(R.id.song_title);
            holder.songArtist = convertView.findViewById(R.id.song_artist);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Song song = songs.get(position);
        holder.songTitle.setText(song.getTitle());
        holder.songArtist.setText(song.getArtist());
        holder.albumCover.setImageResource(song.getAlbumCoverResource());

        convertView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSongClick(song);
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        ImageView albumCover;
        TextView songTitle;
        TextView songArtist;
    }
}