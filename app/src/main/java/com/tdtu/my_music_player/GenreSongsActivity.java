package com.tdtu.my_music_player;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GenreSongsActivity extends AppCompatActivity {

    private RecyclerView songsRecyclerView;
    private SongAdapter songAdapter;
    private MediaPlayerManager mediaPlayerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre_songs); // Ensure this layout exists and is correct

        // Initialize RecyclerView
        songsRecyclerView = findViewById(R.id.songs_recycler_view);
        songsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Retrieve the genre name from the intent
        String genreName = getIntent().getStringExtra("genreName");

        // Fetch the songs by genre using MediaPlayerManager
        mediaPlayerManager = MediaPlayerManager.getInstance();
        List<Song> songList = mediaPlayerManager.getSongsByGenre(genreName);

        // Set up the adapter and song click listener
        songAdapter = new SongAdapter(this, songList, song -> playSelectedSong(song));
        songsRecyclerView.setAdapter(songAdapter);
    }

    private void playSelectedSong(Song song) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("playSong", true);
        intent.putExtra("songTitle", song.getTitle());
        intent.putExtra("artistName", song.getArtist());
        intent.putExtra("songResource", song.getResource());
        intent.putExtra("albumCoverResource", song.getAlbumCoverResource());
        startActivity(intent); // Start MainActivity to play the song
    }
}
