package com.tdtu.my_music_player.PlayerSet;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.tdtu.my_music_player.LoginRegister.AuthActivity;
import com.tdtu.my_music_player.MediaManager.MediaPlayerManager;
import com.tdtu.my_music_player.MediaManager.MediaPlayerService;
import com.tdtu.my_music_player.R;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout bottomPlayerPanel;
    private TextView tvSongTitle, tvArtistName;
    private ImageButton btnPlayPause, btnNext, btnPrevious;
    private ImageView imgAlbumCover;
    private MediaPlayerManager mediaPlayerManager;

    private static final int POST_NOTIFICATIONS_PERMISSION_REQUEST_CODE = 101;

    private FirebaseAuth auth;

    private OnPlayerStatusChangedListener onPlayerStatusChangedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check notification permission (Android 13 and above)
        checkNotificationPermission();

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

        // Start the MediaPlayerService
        startMediaService();

        // Handle intent to navigate to PlayFragment
        handleIntent(getIntent());

        btnPlayPause.setOnClickListener(v -> {
            mediaPlayerManager.pauseOrResumeSong();
            updateMiniPlayerUI();
            notifyPlayFragment();
        });

        btnNext.setOnClickListener(v -> {
            playNextSong();
            updateMiniPlayerUI();
            notifyPlayFragment();
        });

        btnPrevious.setOnClickListener(v -> {
            playPreviousSong();
            updateMiniPlayerUI();
            notifyPlayFragment();
        });

        // Open full player activity when the mini-player is clicked
        bottomPlayerPanel.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
            startActivity(intent);  // Launch PlayerActivity
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null) {
            // Remove the navigation logic for "navigateTo"
            if (intent.getBooleanExtra("playSong", false)) {
                String songTitle = intent.getStringExtra("songTitle");
                String artistName = intent.getStringExtra("artistName");
                int songResource = intent.getIntExtra("songResource", -1);
                int albumCoverResource = intent.getIntExtra("albumCoverResource", -1);

                if (songResource != -1) {
                    playSelectedSong(songTitle, artistName, songResource, albumCoverResource);
                }
            }
        }
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        POST_NOTIFICATIONS_PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void startMediaService() {
        Intent intent = new Intent(this, MediaPlayerService.class);
        startService(intent);
    }

    private void stopMediaService() {
        Intent intent = new Intent(this, MediaPlayerService.class);
        stopService(intent);
    }

    private void initializeNavigation() {
        BottomNavigationView navView = findViewById(R.id.bottom_navigation);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home, R.id.navigation_search,
                    R.id.navigation_profile, R.id.navigation_playlist).build();
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navView, navController);
        }
    }

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

    private void notifyPlayFragment() {
        if (onPlayerStatusChangedListener != null) {
            onPlayerStatusChangedListener.onPlayerStatusChanged();
        }
    }

    public void setOnPlayerStatusChangedListener(OnPlayerStatusChangedListener listener) {
        this.onPlayerStatusChangedListener = listener;
    }

    @Override
    public void onBackPressed() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            if (!navController.popBackStack()) {
                super.onBackPressed(); // Close the app if no fragments in back stack
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetFragmentInteractivity();
    }

    private void resetFragmentInteractivity() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            for (Fragment fragment : navHostFragment.getChildFragmentManager().getFragments()) {
                if (fragment != null && fragment.getView() != null) {
                    fragment.getView().setFocusableInTouchMode(true);
                    fragment.getView().requestFocus();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMediaService();
        if (mediaPlayerManager != null) {
            mediaPlayerManager.stopSong();
        }
    }

    public interface OnPlayerStatusChangedListener {
        void onPlayerStatusChanged();
    }
}