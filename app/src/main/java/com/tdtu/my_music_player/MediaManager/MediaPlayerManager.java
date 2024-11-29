package com.tdtu.my_music_player.MediaManager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.widget.Toast;

import com.tdtu.my_music_player.Playlist.PlaylistManager;
import com.tdtu.my_music_player.R;
import com.tdtu.my_music_player.Notification.MediaNotificationReceiver;
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

    private static final String CHANNEL_ID = "MediaPlayerNotificationChannel";

    private List<PlaybackStateListener> listeners = new ArrayList<>();

    // Song data arrays
    private int[] songResources = {
            R.raw.kpop1, R.raw.kpop3, R.raw.usuk2, R.raw.usuk3, R.raw.vpop1, R.raw.vpop3,
            R.raw.jpop1, R.raw.jpop2, R.raw.travis, R.raw.fein, R.raw.justin, R.raw.justin2,
            R.raw.rock1, R.raw.rock2, R.raw.laudaitinhai, R.raw.khutaosong
    };

    private String[] songTitles = {
            "EYES, NOSE, LIPS", "OMG", "Blinding Lights", "Sunflower",
            "Đừng Làm Trái Tim Anh Đau", "Chạy Ngay Đi", "NIGHT DANCER", "Odoriko (踊り子)",
            "Highest in the Room", "Fein", "Yummy", "Peaches", "Numb", "Creep", "Lâu Đài Tình Ái", "Khu Tao Sống"
    };

    private String[] artistNames = {
            "TAEYANG", "NewJeans", "The Weeknd", "Post Malone", "Sơn Tùng M-TP", "Sơn Tùng M-TP",
            "Imase", "Vaundy", "Travis Scott", "Travis Scott", "Justin Bieber", "Justin Bieber",
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




    public static MediaPlayerManager getInstance() {
        if (instance == null) {
            instance = new MediaPlayerManager();
        }
        return instance;
    }

    public void registerPlaybackStateListener(PlaybackStateListener listener) {
        listeners.add(listener);
    }

    public void unregisterPlaybackStateListener(PlaybackStateListener listener) {
        listeners.remove(listener);
    }

    private void notifyPlaybackStateChange() {
        for (PlaybackStateListener listener : listeners) {
            listener.onPlaybackStateChanged();
        }
    }
    public int getCurrentSongResource() {
        return currentSongResource; // Return the resource ID of the currently playing song
    }
    public void addCurrentSongToPlaylist(Context context) {
        PlaylistManager playlistManager = PlaylistManager.getInstance();
        if (playlistManager.isSongInPlaylist(currentSongTitle, currentArtistName)) {
            Toast.makeText(context, "Song is already in the playlist", Toast.LENGTH_SHORT).show();
        } else {
            playlistManager.addSongToPlaylist(
                    currentSongTitle,
                    currentArtistName,
                    currentSongResource,
                    currentAlbumCoverResource
            );
            Toast.makeText(context, "Song added to playlist", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to seek to a specific position in the song
    public void seekTo(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
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

        currentSongTitle = songTitle;
        currentArtistName = artistName;
        currentAlbumCoverResource = albumCoverResource;
        currentSongResource = songResource;

        try {
            mediaPlayer = MediaPlayer.create(context, songResource);
            mediaPlayer.setOnCompletionListener(mp -> stopSong());
            mediaPlayer.start();
            updateNotification(context, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                notifyPlaybackStateChange(); // Notify listeners about the pause state
            }
        } catch (IllegalStateException e) {
            e.printStackTrace(); // Log any illegal state issues
        }
    }


    public void resumeSong() {
        try {
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                notifyPlaybackStateChange(); // Notify listeners about the play state
            }
        } catch (IllegalStateException e) {
            e.printStackTrace(); // Log any illegal state issues
        }
    }


    public void playNextSong(Context context) {
        currentSongIndex = (currentSongIndex + 1) % songResources.length;
        playSong(context, songResources[currentSongIndex], songTitles[currentSongIndex],
                artistNames[currentSongIndex], albumCoverResources[currentSongIndex]);
    }

    public void playPreviousSong(Context context) {
        currentSongIndex = (currentSongIndex - 1 + songResources.length) % songResources.length;
        playSong(context, songResources[currentSongIndex], songTitles[currentSongIndex],
                artistNames[currentSongIndex], albumCoverResources[currentSongIndex]);
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

    public MediaSessionCompat getMediaSession(Context context) {
        if (mediaSession == null) {
            mediaSession = new MediaSessionCompat(context, "MyMediaSession");
        }
        return mediaSession;
    }

    public void pauseOrResumeSong() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.start();
            }
            notifyPlaybackStateChange();
        }
    }

    public void stopSongDueToCountdown() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        notifyPlaybackStateChange();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setPlaybackSpeed(float speed) {
        if (mediaPlayer != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speed));
        }
    }

    private void updateNotification(Context context, boolean isPlaying) {
        if (notificationManager == null && context != null) {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        PendingIntent playPauseIntent = PendingIntent.getBroadcast(
                context, 0, new Intent(context, MediaNotificationReceiver.class)
                        .setAction(isPlaying ? "ACTION_PAUSE" : "ACTION_PLAY"), PendingIntent.FLAG_UPDATE_CURRENT
        );

        PendingIntent nextIntent = PendingIntent.getBroadcast(
                context, 0, new Intent(context, MediaNotificationReceiver.class)
                        .setAction("ACTION_NEXT"), PendingIntent.FLAG_UPDATE_CURRENT
        );

        PendingIntent previousIntent = PendingIntent.getBroadcast(
                context, 0, new Intent(context, MediaNotificationReceiver.class)
                        .setAction("ACTION_PREVIOUS"), PendingIntent.FLAG_UPDATE_CURRENT
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Music Player Notifications",
                    NotificationManager.IMPORTANCE_LOW
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.musiclogo)
                .setContentTitle(currentSongTitle)
                .setContentText(currentArtistName)
                .addAction(R.drawable.ic_previous, "Previous", previousIntent)
                .addAction(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play, "Play/Pause", playPauseIntent)
                .addAction(R.drawable.ic_next, "Next", nextIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(isPlaying)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle());

        notificationManager.notify(1, builder.build());
    }

    public interface PlaybackStateListener {
        void onPlaybackStateChanged();
    }
}
