package com.tdtu.my_music_player.Playlist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tdtu.my_music_player.PlayerSet.MainActivity;
import com.tdtu.my_music_player.R;
import com.tdtu.my_music_player.SearchSong.Song;

import java.util.List;

public class PlaylistDetailsActivity extends AppCompatActivity {

    private RecyclerView songsRecyclerView;
    private PlaylistManager playlistManager;
    private PlaylistAdapter playlistAdapter;
    private String playlistName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_details);

        songsRecyclerView = findViewById(R.id.playlistRecyclerView);
        playlistManager = PlaylistManager.getInstance(this);

        // Retrieve the playlist name from intent
        playlistName = getIntent().getStringExtra("playlist_name");

        // Display the songs in the playlist
        displaySongsInPlaylist();
    }

    private void displaySongsInPlaylist() {
        List<Song> songs = playlistManager.getPlaylist(playlistName);
        if (songs != null && !songs.isEmpty()) {
            playlistAdapter = new PlaylistAdapter(this, songs, (song, position) -> {
                // Play the selected song
                MainActivity mainActivity = (MainActivity) getApplicationContext();
                mainActivity.playSelectedSong(song.getTitle(), song.getArtist(), song.getResource(), song.getAlbumCoverResource());
            });
            songsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            songsRecyclerView.setAdapter(playlistAdapter);
        } else {
            Toast.makeText(this, "No songs in this playlist", Toast.LENGTH_SHORT).show();
        }
    }
}
