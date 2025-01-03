package com.tdtu.my_music_player.MediaManager;

import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;

import androidx.annotation.RequiresApi;

import android.support.v4.media.session.MediaSessionCompat;
import android.widget.Toast;

import com.tdtu.my_music_player.Playlist.PlaylistManager;
import com.tdtu.my_music_player.R;
import com.tdtu.my_music_player.SearchSong.Song;

import java.util.ArrayList;
import java.util.List;

public class MediaPlayerManager {

    private static MediaPlayerManager instance;
    private MediaPlayer mediaPlayer;
    private String currentSongTitle = "";
    private String currentArtistName = "";
    private int currentAlbumCoverResource;
    private int currentSongResource;
    private int currentSongIndex = 0;

    private MediaSessionCompat mediaSession;
    private NotificationManager notificationManager;

    private static final String CHANNEL_ID = "MUSIC_PLAYBACK_CHANNEL";

    private List<PlaybackStateListener> listeners = new ArrayList<>();

    private int[] songResources = {
            R.raw.kpop1, R.raw.kpop3, R.raw.usuk2, R.raw.usuk3,
            R.raw.vpop1, R.raw.vpop3, R.raw.jpop1, R.raw.jpop2,
            R.raw.travis, R.raw.fein, R.raw.justin, R.raw.justin2,
            R.raw.rock1, R.raw.rock2, R.raw.laudaitinhai, R.raw.khutaosong
    };

    private String[] songTitles = {
            "EYES, NOSE, LIPS", "OMG", "Blinding Lights", "Sunflower",
            "Đừng Làm Trái Tim Anh Đau", "Chạy Ngay Đi", "NIGHT DANCER", "Odoriko (踊り子)",
            "Highest in the Room", "Fein", "Yummy", "Peaches",
            "Numb", "Creep", "Lâu Đài Tình Ái", "Khu Tao Sống"
    };

    private String[] artistNames = {
            "TAEYANG", "NewJeans", "The Weeknd", "Post Malone",
            "Sơn Tùng M-TP", "Sơn Tùng M-TP", "Imase", "Vaundy",
            "Travis Scott", "Travis Scott", "Justin Bieber", "Justin Bieber",
            "Linkin Park", "Radiohead", "Đàm Vĩnh Hưng", "Wowy"
    };

    private int[] albumCoverResources = {
            R.drawable.kpop1, R.drawable.kpop3, R.drawable.usuk2, R.drawable.usuk3,
            R.drawable.vpop1, R.drawable.vpop3, R.drawable.japan1, R.drawable.japan2,
            R.drawable.travis, R.drawable.fein, R.drawable.justin2, R.drawable.justin3,
            R.drawable.rock1, R.drawable.rock2, R.drawable.laudaitinhai, R.drawable.khutaosong
    };
    private String[] genres = {
            "R&B", "Pop", "Pop", "Pop", "Pop", "Pop", "R&B", "Japanese",
            "Rap", "Rap", "Pop", "Pop", "Rock", "Rock", "Pop", "Rap"
    };


    private MediaPlayerManager() {
        // Private constructor to enforce singleton pattern
    }

    public static MediaPlayerManager getInstance() {
        if (instance == null) {
            instance = new MediaPlayerManager();
        }
        return instance;
    }

    public void playSong(Context context, int songResource, String songTitle, String artistName, int albumCoverResource) {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.reset();
                mediaPlayer.release();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }

        // Update the current song details
        currentSongTitle = songTitle;
        currentArtistName = artistName;
        currentAlbumCoverResource = albumCoverResource;
        currentSongResource = songResource;

        try {
            // Create and start the media player
            mediaPlayer = MediaPlayer.create(context, songResource);

            // Set what happens when the song finishes
            mediaPlayer.setOnCompletionListener(mp -> {
                // Automatically play the next song when the current song completes
                playNextSong(context);

                // Notify listeners of the state change (e.g., MediaPlayerService)
                notifyPlaybackStateChange();
            });

            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Notify listeners of playback state change (e.g., MediaPlayerService for notification updates)
        notifyPlaybackStateChange();
    }

    public void stopSong() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (notificationManager != null) {
            notificationManager.cancel(1); // Dismiss notification
        }
        notifyPlaybackStateChange();
    }

    public void pauseSong() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.pause(); // Pause playback
                notifyPlaybackStateChange(); // Notify listeners
            } catch (Exception e) {
                e.printStackTrace(); // Log any exceptions
            }
        }
    }

    public void resumeSong() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.start(); // Resume playback
                notifyPlaybackStateChange(); // Notify listeners
            } catch (Exception e) {
                e.printStackTrace(); // Log any exceptions
            }
        }
    }

    public void playNextSong(Context context) {
        currentSongIndex = (currentSongIndex + 1) % songResources.length;
        playSong(context, songResources[currentSongIndex], songTitles[currentSongIndex],
                artistNames[currentSongIndex], albumCoverResources[currentSongIndex]);
        notifyPlaybackStateChange(); // Notify state change
    }

    public void playPreviousSong(Context context) {
        currentSongIndex = (currentSongIndex - 1 + songResources.length) % songResources.length;
        playSong(context, songResources[currentSongIndex], songTitles[currentSongIndex],
                artistNames[currentSongIndex], albumCoverResources[currentSongIndex]);
        notifyPlaybackStateChange(); // Notify state change
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public String getCurrentSongTitle() {
        return currentSongTitle;
    }

    public String getCurrentArtistName() {
        return currentArtistName;
    }

    public int getCurrentAlbumCoverResource() {
        return currentAlbumCoverResource;
    }

    private void notifyPlaybackStateChange() {
        for (PlaybackStateListener listener : listeners) {
            listener.onPlaybackStateChanged();
        }
    }
    public void pauseOrResumeSong() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause(); // Pause the song if it's currently playing
            } else {
                mediaPlayer.start(); // Resume the song if it's paused
            }
            notifyPlaybackStateChange(); // Notify listeners about the playback state change
        }
    }


    public void seekTo(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position); // Move playback to the specified position
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setPlaybackSpeed(float speed) {
        if (mediaPlayer != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speed));
        }
    }
    public int getCurrentSongResource() {
        return currentSongResource; // Return the resource ID of the currently playing song
    }




    // Method to get songs by artist
    public List<Song> getSongsByArtist(String artist) {
        List<Song> filteredSongs = new ArrayList<>();
        int totalSongs = Math.min(songTitles.length, artistNames.length);

        for (int i = 0; i < totalSongs; i++) {
            if (artistNames[i].equalsIgnoreCase(artist)) {
                filteredSongs.add(new Song(
                        songTitles[i],
                        artistNames[i],
                        songResources[i],
                        albumCoverResources[i]
                ));
            }
        }
        return filteredSongs;
    }

    // Method to get songs by genre
    public List<Song> getSongsByGenre(String genre) {
        List<Song> filteredSongs = new ArrayList<>();
        int totalSongs = Math.min(songTitles.length, genres.length);

        for (int i = 0; i < totalSongs; i++) {
            if (genres[i].equalsIgnoreCase(genre)) {
                filteredSongs.add(new Song(
                        songTitles[i],
                        artistNames[i],
                        songResources[i],
                        albumCoverResources[i]
                ));
            }
        }
        return filteredSongs;
    }

    // Method to get songs by category
    public List<Song> getSongsByCategory(String category) {
        List<Song> filteredSongs = new ArrayList<>();
        int totalSongs = Math.min(songTitles.length, songResources.length);

        for (int i = 0; i < totalSongs; i++) {
            if (isSongInCategory(category, songTitles[i])) {
                filteredSongs.add(new Song(
                        songTitles[i],
                        artistNames[i],
                        songResources[i],
                        albumCoverResources[i]
                ));
            }
        }
        return filteredSongs;
    }

    // Helper method to check if a song belongs to a category
    private boolean isSongInCategory(String category, String songTitle) {
        switch (category) {
            case "Kpop":
                return isKpopSong(songTitle);
            case "US&UK":
                return isUSUKSong(songTitle);
            case "Vpop":
                return isVpopSong(songTitle);
            case "Japanese songs":
                return isJapaneseSong(songTitle);
            default:
                return false;
        }
    }

    // Helper methods for categories
    private boolean isKpopSong(String title) {
        return title.equals("EYES, NOSE, LIPS") || title.equals("OMG");
    }

    private boolean isUSUKSong(String title) {
        return title.equals("Blinding Lights") || title.equals("Sunflower");
    }

    private boolean isVpopSong(String title) {
        return title.equals("Đừng Làm Trái Tim Anh Đau") || title.equals("Chạy Ngay Đi")
                || title.equals("Lâu Đài Tình Ái") || title.equals("Khu Tao Sống");
    }

    private boolean isJapaneseSong(String title) {
        return title.equals("NIGHT DANCER") || title.equals("Odoriko (踊り子)");
    }
    // Method to get the total duration of the current song
    public int getTotalDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration(); // Returns the total duration in milliseconds
        }
        return 0; // Return 0 if no song is currently playing
    }

    // Method to get the current playback position
    public int getCurrentPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition(); // Returns the current position in milliseconds
        }
        return 0; // Return 0 if no song is currently playing
    }

    public interface PlaybackStateListener {
        void onPlaybackStateChanged();
    }
    public void addPlaybackStateListener(PlaybackStateListener listener) {
        listeners.add(listener);
    }
}
