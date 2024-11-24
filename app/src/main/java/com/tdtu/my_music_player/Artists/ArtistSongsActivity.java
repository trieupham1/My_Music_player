package com.tdtu.my_music_player.Artists;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tdtu.my_music_player.PlayerSet.MainActivity;
import com.tdtu.my_music_player.MediaManager.MediaPlayerManager;
import com.tdtu.my_music_player.R;
import com.tdtu.my_music_player.SearchSong.Song;
import com.tdtu.my_music_player.SearchSong.SongAdapter;

import java.util.List;

public class ArtistSongsActivity extends AppCompatActivity {

    private RecyclerView songsRecyclerView;
    private SongAdapter songAdapter;
    private MediaPlayerManager mediaPlayerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_songs);

        songsRecyclerView = findViewById(R.id.songs_recycler_view);
        songsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Retrieve the artist's name from the intent
        String artistName = getIntent().getStringExtra("artistName");

        // Fetch the songs by artist using MediaPlayerManager
        mediaPlayerManager = MediaPlayerManager.getInstance();
        List<Song> songList = mediaPlayerManager.getSongsByArtist(artistName);

        // Set up the adapter and song click listener
        songAdapter = new SongAdapter(this, songList, song -> {
            playSelectedSong(song);  // Play the selected song
        });
        songsRecyclerView.setAdapter(songAdapter);
    }

    // Function to play the selected song and send it to MainActivity
    private void playSelectedSong(Song song) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("playSong", true);
        intent.putExtra("songTitle", song.getTitle());
        intent.putExtra("artistName", song.getArtist());
        intent.putExtra("songResource", song.getResource());
        intent.putExtra("albumCoverResource", song.getAlbumCoverResource());
        startActivity(intent);  // Start MainActivity to play the song
    }
}
