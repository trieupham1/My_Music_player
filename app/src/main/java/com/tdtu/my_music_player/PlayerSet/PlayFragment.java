package com.tdtu.my_music_player.PlayerSet;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.tdtu.my_music_player.MediaManager.MediaPlayerManager;
import com.tdtu.my_music_player.R;

public class PlayFragment extends Fragment {

    private TextView tvSongTitle, tvArtistName, tvCountdownTimer, tvCurrentTime, tvTotalDuration, playbackSpeedText;
    private ImageButton btnPlayPause, btnNext, btnPrevious;
    private Button btnAddToPlaylist, btnStartTimer;
    private SeekBar playbackBar, volumeBar, speedBar;
    private ImageView imgAlbumCover;
    private MediaPlayerManager mediaPlayerManager;
    private AudioManager audioManager;
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private long timeLeftInMillis = 60000; // Default: 1 minute

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

        // Initialize MediaPlayerManager
        mediaPlayerManager = MediaPlayerManager.getInstance();
        audioManager = (AudioManager) requireContext().getSystemService(Context.AUDIO_SERVICE);

        // Set up button listeners
        btnPlayPause.setOnClickListener(v -> togglePlayPause());
        btnNext.setOnClickListener(v -> playNextSong());
        btnPrevious.setOnClickListener(v -> playPreviousSong());
        btnAddToPlaylist.setOnClickListener(v -> addToPlaylist());
        btnStartTimer.setOnClickListener(v -> showCountdownDialog());

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

    private void showCountdownDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Set Countdown Time");

        final android.widget.EditText input = new android.widget.EditText(requireContext());
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        input.setTextColor(getResources().getColor(android.R.color.black)); // Set text color to black
        builder.setView(input);

        builder.setPositiveButton("Set", (dialog, which) -> {
            String inputTime = input.getText().toString();
            if (!inputTime.isEmpty()) {
                int minutes = Integer.parseInt(inputTime);
                if (minutes > 0) {
                    timeLeftInMillis = minutes * 60 * 1000; // Convert to milliseconds
                    startCountdown();
                } else {
                    Toast.makeText(requireContext(), "Enter a valid time!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void startCountdown() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountdownText();
            }

            @Override
            public void onFinish() {
                mediaPlayerManager.stopSong();
                isTimerRunning = false;
                tvCountdownTimer.setTextColor(getResources().getColor(android.R.color.black)); // Set text color to black
                tvCountdownTimer.setText("Countdown: 00:00");
            }
        }.start();
        isTimerRunning = true;
    }

    private void updateCountdownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        tvCountdownTimer.setTextColor(getResources().getColor(android.R.color.black)); // Set text color to black
        tvCountdownTimer.setText(String.format("Countdown: %02d:%02d", minutes, seconds));
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
}
