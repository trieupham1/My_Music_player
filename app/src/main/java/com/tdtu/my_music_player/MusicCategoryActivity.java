package com.tdtu.my_music_player;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MusicCategoryActivity extends AppCompatActivity implements SongAdapter.OnSongClickListener {

    private RecyclerView songsRecyclerView;
    private SongAdapter songAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_category);

        songsRecyclerView = findViewById(R.id.songs_recycler_view);
        songsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Retrieve the category name from the intent
        String category = getIntent().getStringExtra("categoryName");

        // Load songs based on the category
        List<Song> songList = getSongsByCategory(category);

        // Set up the adapter with the correct parameters
        songAdapter = new SongAdapter(this, songList, this); // Pass the context and listener
        songsRecyclerView.setAdapter(songAdapter);
    }

    @Override
    public void onSongClick(Song song) {
        // Redirect the song click to MainActivity for playback
        Intent intent = new Intent(MusicCategoryActivity.this, MainActivity.class);
        intent.putExtra("songTitle", song.getTitle());
        intent.putExtra("artistName", song.getArtist());
        intent.putExtra("songResource", song.getResource());
        intent.putExtra("albumCoverResource", song.getAlbumCoverResource());
        intent.putExtra("playSong", true); // Add a flag to indicate song playback is needed
        startActivity(intent);
    }

    private List<Song> getSongsByCategory(String category) {
        List<Song> filteredSongs = new ArrayList<>();
        MediaPlayerManager mediaPlayerManager = MediaPlayerManager.getInstance();
        String[] songTitles = mediaPlayerManager.getSongTitles();
        String[] artistNames = mediaPlayerManager.getArtistNames();
        int[] songResources = mediaPlayerManager.getSongResources();
        int[] albumCoverResources = mediaPlayerManager.getAlbumCoverResources();

        // Filter songs based on category
        for (int i = 0; i < songTitles.length; i++) {
            if ((category.equals("Kpop") && songTitles[i].equals("Fein Song")) ||
                    (category.equals("Vpop") && (songTitles[i].equals("Khu tao sống") || songTitles[i].equals("Lâu Đài tình ái"))) ||
                    (category.equals("Japanese") && songTitles[i].equals("Second Song")) ||
                    (category.equals("US&UK") && songTitles[i].equals("Fein Song"))) {
                filteredSongs.add(new Song(songTitles[i], artistNames[i], songResources[i], albumCoverResources[i]));
            }
        }

        return filteredSongs;
    }
}