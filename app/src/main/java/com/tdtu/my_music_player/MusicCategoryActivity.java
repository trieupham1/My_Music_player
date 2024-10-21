// MusicCategoryActivity.java
package com.tdtu.my_music_player;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MusicCategoryActivity extends AppCompatActivity {

    private RecyclerView songsRecyclerView;
    private SongAdapter songAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_category);

        songsRecyclerView = findViewById(R.id.songs_recycler_view);
        songsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        String categoryName = getIntent().getStringExtra("categoryName");
        List<Song> songList = MediaPlayerManager.getInstance().getSongsByCategory(categoryName);

        songAdapter = new SongAdapter(this, songList, song -> {
            // Play the selected song and update mini-player via MainActivity
            Intent intent = new Intent(MusicCategoryActivity.this, MainActivity.class);
            intent.putExtra("songTitle", song.getTitle());
            intent.putExtra("artistName", song.getArtist());
            intent.putExtra("songResource", song.getResource());
            intent.putExtra("albumCoverResource", song.getAlbumCoverResource());
            intent.putExtra("playSong", true); // Trigger play action
            startActivity(intent);
        });

        songsRecyclerView.setAdapter(songAdapter);
    }
}
