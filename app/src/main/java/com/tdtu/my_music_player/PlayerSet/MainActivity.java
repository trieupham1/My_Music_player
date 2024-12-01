// MainActivity.java
package com.tdtu.my_music_player.PlayerSet;

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
import com.tdtu.my_music_player.LoginRegister.AuthActivity;
import com.tdtu.my_music_player.MediaManager.MediaPlayerManager;
import com.tdtu.my_music_player.R;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout bottomPlayerPanel;
    private TextView tvSongTitle, tvArtistName;
    private ImageButton btnPlayPause, btnNext, btnPrevious;
    private ImageView imgAlbumCover;
    private MediaPlayerManager mediaPlayerManager;

    // Firebase Authentication
    private FirebaseAuth auth;

    // Listener to sync PlayFragment with the mini-player
    private OnPlayerStatusChangedListener onPlayerStatusChangedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, AuthActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Initialize Navigation Components
        initializeNavigation();

        // Initialize Media Player Panel Views
        initializePlayerViews();

        // Set up MediaPlayerManager
        mediaPlayerManager = MediaPlayerManager.getInstance();

        // Check for any song passed via intent to play
        handleIntent(getIntent());

        btnPlayPause.setOnClickListener(v -> {
            mediaPlayerManager.pauseOrResumeSong();
            updateMiniPlayerUI();
            notifyPlayFragment(); // Notify PlayFragment to stay in sync
        });

        btnNext.setOnClickListener(v -> {
            playNextSong();
            notifyPlayFragment(); // Notify PlayFragment to stay in sync
        });

        btnPrevious.setOnClickListener(v -> {
            playPreviousSong();
            notifyPlayFragment(); // Notify PlayFragment to stay in sync
        });

    }

    // Handle the intent passed from other activities (like playing a song)
    private void handleIntent(Intent intent) {
        if (intent != null && intent.getBooleanExtra("playSong", false)) {
            String songTitle = intent.getStringExtra("songTitle");
            String artistName = intent.getStringExtra("artistName");
            int songResource = intent.getIntExtra("songResource", -1);
            int albumCoverResource = intent.getIntExtra("albumCoverResource", -1);

            if (songResource != -1) {
                playSelectedSong(songTitle, artistName, songResource, albumCoverResource);
            }
        }
    }


    // Initialize Navigation Components
    private void initializeNavigation() {
        BottomNavigationView navView = findViewById(R.id.bottom_navigation);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home, R.id.navigation_play, R.id.navigation_search,
                    R.id.navigation_profile, R.id.navigation_playlist).build();
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navView, navController);
        }
    }

    // Initialize the media player views in the mini-player panel
    private void initializePlayerViews() {
        bottomPlayerPanel = findViewById(R.id.bottom_player_panel);
        tvSongTitle = findViewById(R.id.tv_song_title);
        tvArtistName = findViewById(R.id.tv_artist_name);
        btnPlayPause = findViewById(R.id.btn_play_pause);
        btnNext = findViewById(R.id.btn_next);
        btnPrevious = findViewById(R.id.btn_previous);
        imgAlbumCover = findViewById(R.id.img_album_cover);
    }

    // Play the selected song and update the UI
    public void playSelectedSong(String songTitle, String artistName, int songResource, int albumCoverResource) {
        mediaPlayerManager.playSong(this, songResource, songTitle, artistName, albumCoverResource);
        updateMiniPlayerUI();
        notifyPlayFragment();
    }

    // Play the next song
    private void playNextSong() {
        mediaPlayerManager.playNextSong(this);
        updateMiniPlayerUI();
        notifyPlayFragment();
    }

    // Play the previous song
    private void playPreviousSong() {
        mediaPlayerManager.playPreviousSong(this);
        updateMiniPlayerUI();
        notifyPlayFragment();
    }

    // Update the mini-player UI with the current song details
    public void updateMiniPlayerUI() {
        if (mediaPlayerManager.isPlaying()) {
            tvSongTitle.setText(mediaPlayerManager.getCurrentSongTitle());
            tvArtistName.setText(mediaPlayerManager.getCurrentArtistName());
            imgAlbumCover.setImageResource(mediaPlayerManager.getCurrentAlbumCoverResource());
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            bottomPlayerPanel.setVisibility(View.VISIBLE); // Make mini-player visible
        } else {
            btnPlayPause.setImageResource(R.drawable.ic_play);
        }
    }

    private void notifyPlayFragment() {
        if (onPlayerStatusChangedListener != null) {
            onPlayerStatusChangedListener.onPlayerStatusChanged(); // Notify PlayFragment
        }

        // Update the big player in the nav host when the mini-player is clicked
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            PlayFragment playFragment = (PlayFragment) navHostFragment.getChildFragmentManager()
                    .findFragmentById(R.id.navigation_play);
            if (playFragment != null) {
                playFragment.updateUI();
            }
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

    // Interface to notify the PlayFragment of player status changes
    public interface OnPlayerStatusChangedListener {
        void onPlayerStatusChanged();
    }
}
