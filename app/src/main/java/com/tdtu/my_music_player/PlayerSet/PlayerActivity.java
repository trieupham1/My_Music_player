package com.tdtu.my_music_player.PlayerSet;

import static androidx.core.content.ContentProviderCompat.requireContext;
import static java.security.AccessController.getContext;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import com.tdtu.my_music_player.Playlist.PlaylistManager;
import com.tdtu.my_music_player.SearchSong.Song;
import com.tdtu.my_music_player.MediaManager.MediaPlayerManager;
import com.tdtu.my_music_player.R;

import java.util.ArrayList;
import java.util.List;

public class PlayerActivity extends AppCompatActivity {
    private TextView tvSongTitle, tvArtistName, tvCountdownTimer, tvCurrentTime, tvTotalDuration, playbackSpeedText;
    private ImageButton btnPlayPause, btnNext, btnPrevious;
    private Button btnAddToPlaylist, btnStartTimer;
    private SeekBar playbackBar, volumeBar, speedBar;
    private ImageView imgAlbumCover;
    private ScrollView scrollView;
    private MediaPlayerManager mediaPlayerManager;
    private PlaylistManager playlistManager;
    private AudioManager audioManager;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 0;
    private boolean isTimerRunning = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_player);

        // Initialize Views
        tvSongTitle = findViewById(R.id.tv_song_title);
        tvArtistName = findViewById(R.id.tv_artist_name);
        tvCountdownTimer = findViewById(R.id.tv_countdown_timer);
        tvCurrentTime = findViewById(R.id.tv_current_time);
        tvTotalDuration = findViewById(R.id.tv_total_duration);
        playbackSpeedText = findViewById(R.id.playback_speed);
        btnPlayPause = findViewById(R.id.btn_play_pause);
        btnNext = findViewById(R.id.btn_next);
        btnPrevious = findViewById(R.id.btn_previous);
        Button addToPlaylistButton = findViewById(R.id.btn_add_to_playlist);
        btnStartTimer = findViewById(R.id.btn_start_timer);
        playbackBar = findViewById(R.id.playbackBar);
        volumeBar = findViewById(R.id.volumeBar);
        speedBar = findViewById(R.id.speedBar);
        imgAlbumCover = findViewById(R.id.img_album_cover);
        scrollView = findViewById(R.id.scrollView);


        // Initialize Managers
        mediaPlayerManager = MediaPlayerManager.getInstance();

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // Set button click listeners
        btnPlayPause.setOnClickListener(v -> togglePlayPause());
        btnNext.setOnClickListener(v -> playNextSong());
        btnPrevious.setOnClickListener(v -> playPreviousSong());
        addToPlaylistButton.setOnClickListener(v -> addToPlaylist());



        setupSleepTimer();
        setupPlaybackBar();
        setupVolumeControl();
        setupSpeedControl();

        updateUI();
        updateBackgroundColorFromAlbumCover();
    }

    private final List<Song> playlistSongs = new ArrayList<>(); // The playlist to store songs


    private void updateUI() {
        // Update playback bar with the song's total duration and current position
        playbackBar.setMax(mediaPlayerManager.getTotalDuration());
        playbackBar.setProgress(mediaPlayerManager.getCurrentPosition());

        // Update the song title, artist name, and album cover
        tvSongTitle.setText(mediaPlayerManager.getCurrentSongTitle());
        tvArtistName.setText(mediaPlayerManager.getCurrentArtistName());
        imgAlbumCover.setImageResource(mediaPlayerManager.getCurrentAlbumCoverResource());

        // Update play/pause button state
        btnPlayPause.setImageResource(mediaPlayerManager.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play);

        // Update total duration and current time of the song
        tvTotalDuration.setText(formatTime(mediaPlayerManager.getTotalDuration()));
        tvCurrentTime.setText(formatTime(mediaPlayerManager.getCurrentPosition()));
    }

    private void togglePlayPause() {
        try {
            if (mediaPlayerManager.isPlaying()) {
                mediaPlayerManager.pauseSong();
            } else {
                mediaPlayerManager.resumeSong();
            }
            updateUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addToPlaylist() {
        MediaPlayerManager mediaPlayerManager = MediaPlayerManager.getInstance();

        if (mediaPlayerManager == null) {
            Toast.makeText(this, "No song is currently playing", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve details of the currently playing song
        String currentSongTitle = mediaPlayerManager.getCurrentSongTitle();
        String currentArtistName = mediaPlayerManager.getCurrentArtistName();
        int currentSongResource = mediaPlayerManager.getCurrentSongResource();
        int currentAlbumCoverResource = mediaPlayerManager.getCurrentAlbumCoverResource();

        PlaylistManager playlistManager = PlaylistManager.getInstance();

        // Check if the song is already in the playlist
        if (playlistManager.isSongInPlaylist(currentSongTitle, currentArtistName)) {
            Toast.makeText(this, currentSongTitle + " is already in the playlist", Toast.LENGTH_SHORT).show();
        } else {
            // Add the song to the playlist
            playlistManager.addSongToPlaylist(currentSongTitle, currentArtistName, currentSongResource, currentAlbumCoverResource);
            Toast.makeText(this, currentSongTitle + " added to playlist", Toast.LENGTH_SHORT).show();
        }
    }


    private void playNextSong() {
        mediaPlayerManager.playNextSong(this);
        updateUI();
        updateBackgroundColorFromAlbumCover();
    }

    private void playPreviousSong() {
        mediaPlayerManager.playPreviousSong(this);
        updateUI();
        updateBackgroundColorFromAlbumCover();
    }

    private void setupPlaybackBar() {
        playbackBar.setMax(mediaPlayerManager.getTotalDuration());
        playbackBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayerManager.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable updateSeekBarRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayerManager.isPlaying()) {
                    int currentPosition = mediaPlayerManager.getCurrentPosition();
                    playbackBar.setProgress(currentPosition);
                    tvCurrentTime.setText(formatTime(currentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updateSeekBarRunnable);
    }

    private void setupSleepTimer() {
        btnStartTimer.setOnClickListener(v -> {
            String[] timerOptions = {"Off", "5 minutes", "10 minutes", "15 minutes", "20 minutes", "30 minutes", "1 hour"};
            new AlertDialog.Builder(this)
                    .setTitle("Set Sleep Timer")
                    .setItems(timerOptions, (dialog, which) -> {
                        if (which == 0) {
                            cancelSleepTimer();
                        } else {
                            timeLeftInMillis = which * 5 * 60 * 1000;
                            startSleepTimer();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void updateBackgroundColorFromAlbumCover() {
        // Get the bitmap from the ImageView containing the album cover
        Bitmap albumBitmap = ((BitmapDrawable) imgAlbumCover.getDrawable()).getBitmap();

        // Extract the dominant color using Palette
        Palette.from(albumBitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                // Get the dominant color
                int dominantColor = palette.getDominantColor(Color.BLACK);

                // Check if the color is too bright
                if (isColorBright(dominantColor)) {
                    dominantColor = darkenColor(dominantColor); // Darken the color
                }

                // Set the background color of the ScrollView
                scrollView.setBackgroundColor(dominantColor);

                // Adjust text and icon colors for contrast
                adjustTextAndIconColors(dominantColor);
            }
        });
    }

    // Utility to check if a color is bright
    private boolean isColorBright(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        double brightness = (0.299 * red + 0.587 * green + 0.114 * blue);
        return brightness > 200;
    }

    // Utility to darken a color
    private int darkenColor(int color) {
        float factor = 0.7f; // Adjust by 30% darker
        int red = (int) (Color.red(color) * factor);
        int green = (int) (Color.green(color) * factor);
        int blue = (int) (Color.blue(color) * factor);
        return Color.rgb(red, green, blue);
    }

    // Adjust text and icon colors based on the background color
    private void adjustTextAndIconColors(int backgroundColor) {
        // If background is dark, use light text/icons
        boolean isDarkBackground = !isColorBright(backgroundColor);
        int textColor = isDarkBackground ? Color.WHITE : Color.BLACK;

        tvSongTitle.setTextColor(textColor);
        tvArtistName.setTextColor(textColor);
        tvCountdownTimer.setTextColor(textColor);
        tvCurrentTime.setTextColor(textColor);
        tvTotalDuration.setTextColor(textColor);
        playbackSpeedText.setTextColor(textColor);

        btnPlayPause.setColorFilter(textColor);
        btnNext.setColorFilter(textColor);
        btnPrevious.setColorFilter(textColor);
    }

    private void startSleepTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountdownText();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                tvCountdownTimer.setText("Music Stop: 00:00");
                if (mediaPlayerManager != null) {
                    mediaPlayerManager.stopSong();
                }
                Toast.makeText(PlayerActivity.this, "Music stopped", Toast.LENGTH_SHORT).show();
            }
        }.start();
        isTimerRunning = true;
        Toast.makeText(PlayerActivity.this, "Sleep timer started", Toast.LENGTH_SHORT).show();
    }

    private void cancelSleepTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isTimerRunning = false;
        tvCountdownTimer.setText("Music Stop: 00:00");
        Toast.makeText(PlayerActivity.this, "Sleep timer canceled", Toast.LENGTH_SHORT).show();
    }

    private void updateCountdownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        tvCountdownTimer.setText(String.format("Music Stop: %02d:%02d", minutes, seconds));
    }

    private void setupVolumeControl() {
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumeBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumeBar.setProgress(currentVolume);
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setupSpeedControl() {
        speedBar.setMax(300);
        speedBar.setProgress(130);
        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float speed = 0.5f + (progress / 200f);
                playbackSpeedText.setText("Speed: " + speed + "x");
                mediaPlayerManager.setPlaybackSpeed(speed);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private String formatTime(int timeInMillis) {
        int minutes = timeInMillis / 1000 / 60;
        int seconds = timeInMillis / 1000 % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}