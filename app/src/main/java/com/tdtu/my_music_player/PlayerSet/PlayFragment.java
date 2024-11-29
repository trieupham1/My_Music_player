package com.tdtu.my_music_player.PlayerSet;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.tdtu.my_music_player.MediaManager.MediaPlayerManager;
import com.tdtu.my_music_player.Playlist.PlaylistManager;
import com.tdtu.my_music_player.R;

public class PlayFragment extends Fragment {

    private TextView tvSongTitle, tvArtistName, playbackSpeedText, tvCurrentTime, tvTotalDuration;
    private TextView tvCountdownTimer; // Added for countdown display
    private ImageButton btnPlayPause, btnNext, btnPrevious;
    private SeekBar volumeBar, speedBar, playbackBar;
    private ImageView imgAlbumCover;
    private MediaPlayerManager mediaPlayerManager;
    private AudioManager audioManager;
    private Button btnAddToPlaylist, btnStartTimer; // Added for countdown start/stop
    private CountDownTimer countDownTimer; // Added Countdown Timer
    private boolean isTimerRunning = false; // Tracks if the timer is running
    private long timeLeftInMillis = 60000; // Default countdown time: 1 minute (60,000 ms)

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play, container, false);

        // Initialize Views
        tvSongTitle = view.findViewById(R.id.tv_song_title);
        tvArtistName = view.findViewById(R.id.tv_artist_name);
        btnPlayPause = view.findViewById(R.id.btn_play_pause);
        btnNext = view.findViewById(R.id.btn_next);
        btnPrevious = view.findViewById(R.id.btn_previous);
        volumeBar = view.findViewById(R.id.volumeBar);
        speedBar = view.findViewById(R.id.speedBar);
        playbackBar = view.findViewById(R.id.playbackBar);
        tvCurrentTime = view.findViewById(R.id.tv_current_time);
        tvTotalDuration = view.findViewById(R.id.tv_total_duration);
        playbackSpeedText = view.findViewById(R.id.playback_speed);
        imgAlbumCover = view.findViewById(R.id.img_album_cover);
        btnAddToPlaylist = view.findViewById(R.id.btn_add_to_playlist);
        tvCountdownTimer = view.findViewById(R.id.tv_countdown_timer); // Added for countdown
        btnStartTimer = view.findViewById(R.id.btn_start_timer); // Added for countdown

        // Initialize MediaPlayerManager and AudioManager
        mediaPlayerManager = MediaPlayerManager.getInstance();
        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);

        // Set up volume control
        setupVolumeControl();

        // Set up playback speed control
        setupPlaybackSpeedControl();

        // Set up playback progress control
        setupPlaybackControl();

        // Register as playback listener
        mediaPlayerManager.registerPlaybackStateListener(this::updateUI);

        // Play/Pause Button
        btnPlayPause.setOnClickListener(v -> {
            mediaPlayerManager.pauseOrResumeSong();
            updateUI();
        });

        // Next Button
        btnNext.setOnClickListener(v -> {
            playNextSong();
        });

        // Previous Button
        btnPrevious.setOnClickListener(v -> {
            playPreviousSong();
        });

        // "Add to Playlist" Button
        btnAddToPlaylist.setOnClickListener(v -> {
            addToPlaylist();
        });

        // Countdown Timer Button
        btnStartTimer.setOnClickListener(v -> {
            if (isTimerRunning) {
                stopTimer();
            } else {
                showTimerDialog(); // Show dialog to select countdown time
            }
        });

        // Update UI to match the current state
        updateUI();

        return view;
    }

    private void showTimerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Set Countdown Time (minutes)");

        // Add an input field for the user to set the time
        final EditText input = new EditText(requireContext());
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        input.setHint("Enter time in minutes");
        input.setTextColor(getResources().getColor(android.R.color.black)); // Set text color to black
        input.setHintTextColor(getResources().getColor(android.R.color.darker_gray)); // Optional: Set hint color
        builder.setView(input);

        builder.setPositiveButton("Start", (dialog, which) -> {
            String inputTime = input.getText().toString();
            if (!inputTime.isEmpty()) {
                int minutes = Integer.parseInt(inputTime);
                if (minutes > 0) {
                    timeLeftInMillis = minutes * 60 * 1000; // Convert minutes to milliseconds
                    startTimer();
                } else {
                    Toast.makeText(getContext(), "Please enter a valid time!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }


    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountdownText();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                stopMusic();
                btnStartTimer.setText("Start Countdown");
                tvCountdownTimer.setText("Countdown: 00:00");
            }
        }.start();

        isTimerRunning = true;
        btnStartTimer.setText("Stop Countdown");
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isTimerRunning = false;
        btnStartTimer.setText("Start Countdown");
    }

    private void updateCountdownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        tvCountdownTimer.setText(String.format("Countdown: %02d:%02d", minutes, seconds));
    }

    private void stopMusic() {
        if (mediaPlayerManager != null && mediaPlayerManager.isPlaying()) {
            mediaPlayerManager.stopSong();
            Toast.makeText(getContext(), "Music stopped", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupVolumeControl() {
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volumeBar.setMax(maxVolume);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumeBar.setProgress(currentVolume);

        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void setupPlaybackSpeedControl() {
        speedBar.setMax(20); // Speed range: 0.5x to 2.0x (steps of 0.1)
        speedBar.setProgress(10); // Default: 1x

        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float speed = 0.5f + (progress / 10.0f); // Calculate speed between 0.5x and 2.0x
                playbackSpeedText.setText(String.format("Speed: %.1fx", speed));
                mediaPlayerManager.setPlaybackSpeed(speed);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void setupPlaybackControl() {
        playbackBar.setMax(mediaPlayerManager.getTotalDuration());
        playbackBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayerManager.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        updatePlaybackProgress();
    }

    private void updatePlaybackProgress() {
        int currentPosition = mediaPlayerManager.getCurrentPosition();
        int totalDuration = mediaPlayerManager.getTotalDuration();

        tvCurrentTime.setText(formatTime(currentPosition));
        tvTotalDuration.setText(formatTime(totalDuration));
        playbackBar.setMax(totalDuration);
        playbackBar.setProgress(currentPosition);

        // Schedule periodic updates
        playbackBar.postDelayed(this::updatePlaybackProgress, 1000);
    }

    private String formatTime(int milliseconds) {
        int minutes = (milliseconds / 1000) / 60;
        int seconds = (milliseconds / 1000) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    private void playNextSong() {
        if (mediaPlayerManager != null) {
            mediaPlayerManager.playNextSong(getContext());
            updateUI();
        }
    }

    private void playPreviousSong() {
        if (mediaPlayerManager != null) {
            mediaPlayerManager.playPreviousSong(getContext());
            updateUI();
        }
    }

    private void addToPlaylist() {
        String currentSongTitle = mediaPlayerManager.getCurrentSongTitle();
        String currentArtistName = mediaPlayerManager.getCurrentArtistName();
        int currentSongResource = mediaPlayerManager.getCurrentSongResource();
        int currentAlbumCoverResource = mediaPlayerManager.getCurrentAlbumCoverResource();

        PlaylistManager playlistManager = PlaylistManager.getInstance();

        if (playlistManager.isSongInPlaylist(currentSongTitle, currentArtistName)) {
            Toast.makeText(getContext(), currentSongTitle + " is already in the playlist", Toast.LENGTH_SHORT).show();
        } else {
            playlistManager.addSongToPlaylist(currentSongTitle, currentArtistName, currentSongResource, currentAlbumCoverResource);
            Toast.makeText(getContext(), currentSongTitle + " added to playlist", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI() {
        tvSongTitle.setText(mediaPlayerManager.getCurrentSongTitle());
        tvArtistName.setText(mediaPlayerManager.getCurrentArtistName());
        btnPlayPause.setImageResource(mediaPlayerManager.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play);
        imgAlbumCover.setImageResource(mediaPlayerManager.getCurrentAlbumCoverResource());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        mediaPlayerManager.unregisterPlaybackStateListener(this::updateUI);
    }
}
