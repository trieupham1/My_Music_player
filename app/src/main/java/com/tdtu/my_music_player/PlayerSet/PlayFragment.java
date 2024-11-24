package com.tdtu.my_music_player.PlayerSet;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
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

import com.tdtu.my_music_player.MediaManager.MediaPlayerManager;
import com.tdtu.my_music_player.Playlist.PlaylistManager;
import com.tdtu.my_music_player.R;

public class PlayFragment extends Fragment {

    private TextView tvSongTitle, tvArtistName, playbackSpeedText, tvCurrentTime, tvTotalDuration;
    private ImageButton btnPlayPause, btnNext, btnPrevious;
    private SeekBar volumeBar, speedBar, playbackBar;
    private ImageView imgAlbumCover;
    private MediaPlayerManager mediaPlayerManager;
    private AudioManager audioManager;
    private Button btnAddToPlaylist;

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
        playbackBar = view.findViewById(R.id.playbackBar); // Playback progress bar
        tvCurrentTime = view.findViewById(R.id.tv_current_time); // Current time text
        tvTotalDuration = view.findViewById(R.id.tv_total_duration); // Total duration text
        playbackSpeedText = view.findViewById(R.id.playback_speed);
        imgAlbumCover = view.findViewById(R.id.img_album_cover);
        btnAddToPlaylist = view.findViewById(R.id.btn_add_to_playlist);

        // Initialize MediaPlayerManager and AudioManager
        mediaPlayerManager = MediaPlayerManager.getInstance();
        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);

        // Set up volume control
        setupVolumeControl();

        // Set up playback speed control
        setupPlaybackSpeedControl();

        // Set up playback progress control
        setupPlaybackControl();

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

        // Update UI to match the current state
        updateUI();

        return view;
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
                    mediaPlayerManager.seekTo(progress); // Seek to selected position
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
}
