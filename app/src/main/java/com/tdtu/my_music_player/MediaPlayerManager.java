package com.tdtu.my_music_player;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class MediaPlayerManager {

    private static MediaPlayerManager instance;
    private MediaPlayer mediaPlayer;
    private String currentSongTitle = "";
    private String currentArtistName = "";
    private int currentAlbumCoverResource;
    private int currentSongResource;  // Store the current song's resource ID
    private int currentSongIndex = 0;

    private int[] songResources = {R.raw.first, R.raw.second, R.raw.fein, R.raw.khutaosong, R.raw.laudaitinhai};
    private String[] songTitles = {"First Song", "Second Song", "Fein Song", "Khu tao sống", "Lâu Đài tình ái"};
    private String[] artistNames = {"Artist One", "Artist Two", "Travis Scott", "Wowy", "Đàm Vĩnh Hưng"};
    private int[] albumCoverResources = {
            R.drawable.first,
            R.drawable.second,
            R.drawable.fein,
            R.drawable.khutaosong,
            R.drawable.damvinhhung
    };

    private MediaPlayerManager() {
    }

    public static MediaPlayerManager getInstance() {
        if (instance == null) {
            instance = new MediaPlayerManager();
        }
        return instance;
    }

    public void playSong(Context context, int songResource, String songTitle, String artistName, int albumCoverResource) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        currentSongTitle = songTitle;
        currentArtistName = artistName;
        currentAlbumCoverResource = albumCoverResource;
        currentSongResource = songResource;  // Update the current song resource

        mediaPlayer = MediaPlayer.create(context, songResource);
        mediaPlayer.start();
    }

    public int getCurrentAlbumCoverResource() {
        return currentAlbumCoverResource;
    }

    public void playNextSong(Context context) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        currentSongIndex = (currentSongIndex + 1) % songResources.length;
        currentSongTitle = songTitles[currentSongIndex];
        currentArtistName = artistNames[currentSongIndex];
        currentAlbumCoverResource = albumCoverResources[currentSongIndex];
        currentSongResource = songResources[currentSongIndex];  // Update current song resource

        mediaPlayer = MediaPlayer.create(context, currentSongResource);
        mediaPlayer.start();
    }

    public void playPreviousSong(Context context) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        currentSongIndex = (currentSongIndex - 1 + songResources.length) % songResources.length;
        currentSongTitle = songTitles[currentSongIndex];
        currentArtistName = artistNames[currentSongIndex];
        currentAlbumCoverResource = albumCoverResources[currentSongIndex];
        currentSongResource = songResources[currentSongIndex];  // Update current song resource

        mediaPlayer = MediaPlayer.create(context, currentSongResource);
        mediaPlayer.start();
    }

    public void pauseOrResumeSong() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.start();
            }
        }
    }

    public void stopSong() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
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

    // Added Methods

    public int[] getSongResources() {
        return songResources;
    }

    public String[] getSongTitles() {
        return songTitles;
    }

    public String[] getArtistNames() {
        return artistNames;
    }

    public int[] getAlbumCoverResources() {
        return albumCoverResources;
    }

    public int getCurrentSongResource() {
        return currentSongResource;  // Return the resource ID of the current song
    }

    public int findSongIndexByTitle(String songTitle) {
        for (int i = 0; i < songTitles.length; i++) {
            if (songTitles[i].equalsIgnoreCase(songTitle)) {
                return i;
            }
        }
        return -1; // Returns -1 if not found
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setPlaybackSpeed(float speed) {
        if (mediaPlayer != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speed));
        }
    }
}
