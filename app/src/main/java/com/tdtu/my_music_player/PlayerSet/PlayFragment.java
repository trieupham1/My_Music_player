package com.tdtu.my_music_player.PlayerSet;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.tdtu.my_music_player.MediaManager.MediaPlayerManager;
import com.tdtu.my_music_player.MediaManager.MediaPlayerService;
import com.tdtu.my_music_player.Notification.NotificationPanel;
import com.tdtu.my_music_player.R;
import com.tdtu.my_music_player.Time.TimerDialogFragment;

public class PlayFragment extends Fragment {

    private TextView tvSongTitle, tvArtistName, tvCountdownTimer, tvCurrentTime, tvTotalDuration, playbackSpeedText;
    private ImageButton btnPlayPause, btnNext, btnPrevious;
    private Button btnAddToPlaylist, btnStartTimer;
    private SeekBar playbackBar, volumeBar, speedBar;
    private ImageView imgAlbumCover;
    private MediaPlayerManager mediaPlayerManager;
    private AudioManager audioManager;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 0; // Remaining time in milliseconds
    private boolean isTimerRunning = false;
    private static final String TIMER_PREF = "timerPref";
    private static final String TIMER_LEFT = "timeLeft";
    private static final String TIMER_RUNNING = "isRunning";



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play, container, false);

        // Initialize Views
        tvSongTitle = view.findViewById(R.id.tv_song_title);
        tvArtistName = view.findViewById(R.id.tv_artist_name);
        tvCountdownTimer = view.findViewById(R.id.tv_countdown_timer);
        tvCurrentTime = view.findViewById(R.id.tv_current_time);
        tvTotalDuration = view.findViewById(R.id.tv_total_duration);
        playbackSpeedText = view.findViewById(R.id.playback_speed);
        btnPlayPause = view.findViewById(R.id.btn_play_pause);
        btnNext = view.findViewById(R.id.btn_next);
        btnPrevious = view.findViewById(R.id.btn_previous);
        btnAddToPlaylist = view.findViewById(R.id.btn_add_to_playlist);
        btnStartTimer = view.findViewById(R.id.btn_start_timer);
        playbackBar = view.findViewById(R.id.playbackBar);
        volumeBar = view.findViewById(R.id.volumeBar);
        speedBar = view.findViewById(R.id.speedBar);
        imgAlbumCover = view.findViewById(R.id.img_album_cover);
        Button btnShare = view.findViewById(R.id.btn_share);
        btnShare.setOnClickListener(v -> shareCurrentSong());

        // Initialize MediaPlayerManager
        mediaPlayerManager = MediaPlayerManager.getInstance();
        audioManager = (AudioManager) requireContext().getSystemService(Context.AUDIO_SERVICE);

        // Set up button listeners
        tvCountdownTimer = view.findViewById(R.id.tv_countdown_timer);
        btnStartTimer = view.findViewById(R.id.btn_start_timer);

        btnPlayPause.setOnClickListener(v -> togglePlayPause());
        btnNext.setOnClickListener(v -> playNextSong());
        btnPrevious.setOnClickListener(v -> playPreviousSong());
        btnAddToPlaylist.setOnClickListener(v -> addToPlaylist());
        setupSleepTimer();

        setupPlaybackBar();
        setupVolumeControl();
        setupSpeedControl();

        updateUI();
        return view;
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
            e.printStackTrace(); // Log any exceptions
        }
    }
    // Example: Starting the MediaPlayerService when a new song is played
    private void playSong(int songResource, String songTitle, String artistName, int albumCoverResource) {
        // Update the MediaPlayerManager with the new song details
        MediaPlayerManager mediaPlayerManager = MediaPlayerManager.getInstance();
        mediaPlayerManager.playSong(requireContext(), songResource, songTitle, artistName, albumCoverResource);

        // Start the MediaPlayerService
        startMediaPlayerService(requireContext());
    }





    private void playNextSong() {
        mediaPlayerManager.playNextSong(requireContext());
        updateUI();
    }

    private void playPreviousSong() {
        mediaPlayerManager.playPreviousSong(requireContext());
        updateUI();
    }

    private void addToPlaylist() {
        mediaPlayerManager.addCurrentSongToPlaylist(requireContext());
        Toast.makeText(requireContext(), "Added to playlist", Toast.LENGTH_SHORT).show();
    }


    private void setupPlaybackBar() {
        // Set the maximum value of the SeekBar to the total duration of the current song
        playbackBar.setMax(mediaPlayerManager.getTotalDuration());

        // Listener for user interactions with the SeekBar
        playbackBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // If the user is dragging the SeekBar, update the playback position
                if (fromUser) {
                    mediaPlayerManager.seekTo(progress);
                }
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Optionally pause updates while user is interacting
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Optionally resume updates when user stops interacting
            }
        });

        // Use a Handler to update the SeekBar as the song plays
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable updateSeekBarRunnable = new Runnable() {
            @Override
            public void run() {
                // Continuously update the SeekBar and the current time text if the song is playing
                if (mediaPlayerManager.isPlaying()) {
                    int currentPosition = mediaPlayerManager.getCurrentPosition();
                    playbackBar.setProgress(currentPosition); // Update the SeekBar position
                    tvCurrentTime.setText(formatTime(currentPosition)); // Update the current time text
                }

                // Schedule the next update after 1 second
                handler.postDelayed(this, 1000);
            }
        };

        // Start the periodic updates
        handler.post(updateSeekBarRunnable);
    }



    private void setupSleepTimer() {
        btnStartTimer.setOnClickListener(v -> {
            TimerDialogFragment timerDialog = new TimerDialogFragment(minutes -> {
                if (minutes == 0) {
                    cancelSleepTimer();
                } else {
                    timeLeftInMillis = minutes * 60 * 1000; // Convert minutes to milliseconds
                    startSleepTimer();
                }
            });
            timerDialog.show(getParentFragmentManager(), "TimerDialog");
        });
    }

    private void startSleepTimer() {
        long startTime = System.currentTimeMillis();
        saveTimerState(timeLeftInMillis, true, startTime);

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountdownText();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                saveTimerState(0, false, 0); // Clear the timer state
                tvCountdownTimer.setText("Music Stop: 00:00");
                if (mediaPlayerManager != null) {
                    mediaPlayerManager.stopSong();
                }
                Toast.makeText(requireContext(), "Music stopped", Toast.LENGTH_SHORT).show();
            }
        }.start();

        isTimerRunning = true;
        Toast.makeText(requireContext(), "Sleep timer started", Toast.LENGTH_SHORT).show();
    }

    private void cancelSleepTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isTimerRunning = false;
        saveTimerState(0, false, 0); // Clear the timer state
        tvCountdownTimer.setText("Music Stop: 00:00");
        Toast.makeText(requireContext(), "Sleep timer canceled", Toast.LENGTH_SHORT).show();
    }

    private void updateCountdownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        tvCountdownTimer.setText(String.format("Music Stop: %02d:%02d", minutes, seconds));
    }

    private void saveTimerState(long timeLeft, boolean isRunning, long startTime) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("TimerPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("timeLeft", timeLeft);
        editor.putBoolean("isRunning", isRunning);
        editor.putLong("startTime", startTime);
        editor.apply();
    }
    private void navigateToPlayFragment() {
        NavHostFragment navHostFragment = (NavHostFragment) requireActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            navController.navigate(R.id.navigation_play); // Navigate to PlayFragment
        }
    }



    private void restoreTimerState() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("TimerPrefs", Context.MODE_PRIVATE);
        timeLeftInMillis = sharedPreferences.getLong("timeLeft", 0);
        isTimerRunning = sharedPreferences.getBoolean("isRunning", false);
        long startTime = sharedPreferences.getLong("startTime", 0);

        if (isTimerRunning) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            timeLeftInMillis -= elapsedTime;

            if (timeLeftInMillis > 0) {
                startSleepTimer(); // Resume the timer if there is still time left
            } else {
                isTimerRunning = false;
                tvCountdownTimer.setText("Music Stop: 00:00");
                saveTimerState(0, false, 0); // Clear the timer state
            }
        } else {
            updateCountdownText(); // Update UI with the last known timer state
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        restoreTimerState(); // Restore the timer state when returning to the fragment
    }
    private void startMediaPlayerService(Context context) {
        // Create an intent for the MediaPlayerService
        Intent intent = new Intent(context, MediaPlayerService.class);
        // Start the service
        context.startService(intent);
    }




    private void setupVolumeControl() {
        volumeBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumeBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
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
        speedBar.setMax(20); // 0.5x to 2.0x speed
        speedBar.setProgress(10); // Default speed: 1.0x
        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float speed = 0.5f + (progress / 10.0f);
                mediaPlayerManager.setPlaybackSpeed(speed);
                playbackSpeedText.setText(String.format("Speed: %.1fx", speed));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    void updateUI() {
        playbackBar.setMax(mediaPlayerManager.getTotalDuration());
        playbackBar.setProgress(mediaPlayerManager.getCurrentPosition());
        tvSongTitle.setText(mediaPlayerManager.getCurrentSongTitle());
        tvArtistName.setText(mediaPlayerManager.getCurrentArtistName());
        imgAlbumCover.setImageResource(mediaPlayerManager.getCurrentAlbumCoverResource());
        btnPlayPause.setImageResource(mediaPlayerManager.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play);
        tvTotalDuration.setText(formatTime(mediaPlayerManager.getTotalDuration()));
        tvCurrentTime.setText(formatTime(mediaPlayerManager.getCurrentPosition()));
    }


    private String formatTime(int milliseconds) {
        int minutes = (milliseconds / 1000) / 60;
        int seconds = (milliseconds / 1000) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
    private void shareCurrentSong() {
        String songTitle = mediaPlayerManager.getCurrentSongTitle(); // Get song title
        String artistName = mediaPlayerManager.getCurrentArtistName(); // Get artist name
        String songLink = "https://example.com/song/" + songTitle.replaceAll(" ", "_"); // Example link

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareMessage = "🎶 I'm listening to \"" + songTitle + "\" by " + artistName + "! Check it out: " + songLink;
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }
}



