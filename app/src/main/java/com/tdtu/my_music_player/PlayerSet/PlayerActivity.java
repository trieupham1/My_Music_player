package com.tdtu.my_music_player.PlayerSet;

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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;
import com.tdtu.my_music_player.Time.TimerDialogFragment;
import com.tdtu.my_music_player.MediaManager.MediaPlayerManager;
import com.tdtu.my_music_player.R;

public class PlayerActivity extends AppCompatActivity {
    private NotificationManager notificationManager;
    private TextView tvSongTitle, tvArtistName, tvCountdownTimer, tvCurrentTime, tvTotalDuration, playbackSpeedText;
    private ImageButton btnPlayPause, btnNext, btnPrevious;
    private Button btnAddToPlaylist, btnStartTimer;
    private SeekBar playbackBar, volumeBar, speedBar;
    private ImageView imgAlbumCover;
    private ScrollView scrollView;
    private MediaPlayerManager mediaPlayerManager;
    private AudioManager audioManager;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 0;
    private boolean isTimerRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        btnAddToPlaylist = findViewById(R.id.btn_add_to_playlist);
        btnStartTimer = findViewById(R.id.btn_start_timer);
        playbackBar = findViewById(R.id.playbackBar);
        volumeBar = findViewById(R.id.volumeBar);
        speedBar = findViewById(R.id.speedBar);
        imgAlbumCover = findViewById(R.id.img_album_cover);
        scrollView = findViewById(R.id.scrollView);

        // Initialize MediaPlayerManager
        mediaPlayerManager = MediaPlayerManager.getInstance();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // Set button click listeners
        btnPlayPause.setOnClickListener(v -> togglePlayPause());
        btnNext.setOnClickListener(v -> playNextSong());
        btnPrevious.setOnClickListener(v -> playPreviousSong());
        btnAddToPlaylist.setOnClickListener(v -> addToPlaylist());
        setupSleepTimer();

        setupPlaybackBar();
        setupVolumeControl();
        setupSpeedControl();

        updateUI();
        updateBackgroundColorFromAlbumCover();
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

    private void addToPlaylist() {
        mediaPlayerManager.addCurrentSongToPlaylist(this);
        Toast.makeText(this, "Added to playlist", Toast.LENGTH_SHORT).show();
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
            TimerDialogFragment timerDialog = new TimerDialogFragment(minutes -> {
                if (minutes == 0) {
                    cancelSleepTimer();
                } else {
                    timeLeftInMillis = minutes * 60 * 1000;
                    startSleepTimer();
                }
            });
            timerDialog.show(getSupportFragmentManager(), "TimerDialog");
        });
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
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setupSpeedControl() {
        speedBar.setMax(5); // Range from 0.5x to 2x playback speed
        speedBar.setProgress(2); // Default speed: 1x
        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float speed = 0.5f + progress * 0.25f; // Map progress to speed range
                mediaPlayerManager.setPlaybackSpeed(speed);
                playbackSpeedText.setText(String.format("Speed: %.1fx", speed));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void updateUI() {
        playbackBar.setMax(mediaPlayerManager.getTotalDuration());
        playbackBar.setProgress(mediaPlayerManager.getCurrentPosition());
        tvSongTitle.setText(mediaPlayerManager.getCurrentSongTitle());
        tvArtistName.setText(mediaPlayerManager.getCurrentArtistName());
        imgAlbumCover.setImageResource(mediaPlayerManager.getCurrentAlbumCoverResource());
        btnPlayPause.setImageResource(mediaPlayerManager.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play);
        tvTotalDuration.setText(formatTime(mediaPlayerManager.getTotalDuration()));
        tvCurrentTime.setText(formatTime(mediaPlayerManager.getCurrentPosition()));
    }

    private String formatTime(int timeInMillis) {
        int minutes = timeInMillis / 60000;
        int seconds = (timeInMillis % 60000) / 1000;
        return String.format("%02d:%02d", minutes, seconds);
    }
}