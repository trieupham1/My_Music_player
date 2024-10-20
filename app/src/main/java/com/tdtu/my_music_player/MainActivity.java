// MainActivity.java
package com.tdtu.my_music_player;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout bottomPlayerPanel;
    private TextView tvSongTitle, tvArtistName;
    private ImageButton btnPlayPause, btnNext, btnPrevious;
    private ImageView imgAlbumCover;
    private MediaPlayerManager mediaPlayerManager;

    // Firebase Authentication
    private FirebaseAuth auth;

    // Listener to sync PlayFragment with mini-player
    private OnPlayerStatusChangedListener onPlayerStatusChangedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // If user is not logged in, redirect to authentication view
        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, AuthActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Initialize Navigation components
        BottomNavigationView navView = findViewById(R.id.bottom_navigation);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home, R.id.navigation_play, R.id.navigation_search,
                    R.id.navigation_profile, R.id.navigation_playlist)
                    .build();
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navView, navController);
        }

        // Initialize Media Player Panel Views
        initializePlayerViews();

        // Set up MediaPlayerManager
        mediaPlayerManager = MediaPlayerManager.getInstance();

        // Play/Pause Button functionality for the mini-player
        btnPlayPause.setOnClickListener(v -> {
            mediaPlayerManager.pauseOrResumeSong();
            updateMiniPlayerUI();
            notifyPlayFragment();
        });

        // Next Button functionality
        btnNext.setOnClickListener(v -> {
            playNextSong();
            notifyPlayFragment();
        });

        // Previous Button functionality
        btnPrevious.setOnClickListener(v -> {
            playPreviousSong();
            notifyPlayFragment();
        });
    }

    // Function to initialize the media player views
    private void initializePlayerViews() {
        bottomPlayerPanel = findViewById(R.id.bottom_player_panel);
        tvSongTitle = findViewById(R.id.tv_song_title);
        tvArtistName = findViewById(R.id.tv_artist_name);
        btnPlayPause = findViewById(R.id.btn_play_pause);
        btnNext = findViewById(R.id.btn_next);
        btnPrevious = findViewById(R.id.btn_previous);
        imgAlbumCover = findViewById(R.id.img_album_cover);
    }

    public void playSelectedSong(String songTitle, String artistName, int songResource, int albumCoverResource) {
        mediaPlayerManager.playSong(this, songResource, songTitle, artistName, albumCoverResource);
        updateMiniPlayerUI();
        notifyPlayFragment();
    }

    private void playNextSong() {
        mediaPlayerManager.playNextSong(this);
        updateMiniPlayerUI();
        notifyPlayFragment();
    }

    private void playPreviousSong() {
        mediaPlayerManager.playPreviousSong(this);
        updateMiniPlayerUI();
        notifyPlayFragment();
    }

    public void updateMiniPlayerUI() {
        if (mediaPlayerManager.isPlaying()) {
            tvSongTitle.setText(mediaPlayerManager.getCurrentSongTitle());
            tvArtistName.setText(mediaPlayerManager.getCurrentArtistName());
            imgAlbumCover.setImageResource(mediaPlayerManager.getCurrentAlbumCoverResource());
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            bottomPlayerPanel.setVisibility(View.VISIBLE);
        } else {
            btnPlayPause.setImageResource(R.drawable.ic_play);
        }
    }

    // Notify the PlayFragment to update its UI
    private void notifyPlayFragment() {
        if (onPlayerStatusChangedListener != null) {
            onPlayerStatusChangedListener.onPlayerStatusChanged();
        }
    }

    public void setOnPlayerStatusChangedListener(OnPlayerStatusChangedListener listener) {
        this.onPlayerStatusChangedListener = listener;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayerManager != null) {
            mediaPlayerManager.stopSong();
        }
    }

    // Interface to notify the PlayFragment
    public interface OnPlayerStatusChangedListener {
        void onPlayerStatusChanged();
    }
}
