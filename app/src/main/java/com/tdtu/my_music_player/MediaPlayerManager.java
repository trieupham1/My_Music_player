package com.tdtu.my_music_player;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import androidx.annotation.RequiresApi;

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

    // Song data arrays
    private int[] songResources = {
            R.raw.kpop1, R.raw.kpop3, R.raw.usuk2,
            R.raw.usuk3, R.raw.vpop1, R.raw.vpop3, R.raw.jpop1,
            R.raw.jpop2, R.raw.travis,R.raw.fein, R.raw.justin, R.raw.justin2,
            R.raw.rock1, R.raw.rock2,R.raw.laudaitinhai,R.raw.khutaosong
    };

    private String[] songTitles = {
            "EYES, NOSE, LIPS", "OMG", "Blinding Lights",
            "Sunflower", "Đừng Làm Trái Tim Anh Đau", "Chạy Ngay Đi",
            "NIGHT DANCER", "Odoriko (踊り子)",
            "Highest in the Room", "Fein","Yummy", "Peaches", "Numb", "Creep","Lâu Đài Tình Ái","Khu Tao Sống"
    };

    private String[] artistNames = {
            "TAEYANG", "NewJeans", "The Weeknd",
            "Post Malone", "Sơn Tùng M-TP", "Sơn Tùng M-TP", "Imase",
            "Vaundy", "Travis Scott","Travis Scott","Justin Bieber", "Justin Bieber",
            "Linkin Park", "Radiohead","Đàm Vĩnh Hưng","Wowy","Travis Scott"
    };

    private int[] albumCoverResources = {
            R.drawable.kpop1, R.drawable.kpop3, R.drawable.usuk2,
            R.drawable.usuk3, R.drawable.vpop1, R.drawable.vpop3, R.drawable.japan1,
            R.drawable.japan2, R.drawable.travis,R.drawable.fein, R.drawable.justin2,
            R.drawable.justin3, R.drawable.rock1, R.drawable.rock2,R.drawable.laudaitinhai,R.drawable.khutaosong
    };

    private String[] genres = {
            "R&B", "R&B","Pop","Pop", "Pop", "Pop", "Pop", "Pop", "Pop",
            "R&B","Japanese", "Japanese", "Rap","Rap", "Pop", "Pop", "Rock", "Rock","Pop","Rap"
    };



    private MediaPlayerManager() {
    }

    public static MediaPlayerManager getInstance() {
        if (instance == null) {
            instance = new MediaPlayerManager();
        }
        return instance;
    }
    public List<Song> getSongsByArtist(String artist) {
        List<Song> filteredSongs = new ArrayList<>();

        // Retrieve data arrays
        String[] songTitles = getSongTitles();
        String[] artistNames = getArtistNames();
        int[] songResources = getSongResources();
        int[] albumCoverResources = getAlbumCoverResources();

        int totalSongs = Math.min(
                Math.min(songTitles.length, artistNames.length),
                Math.min(songResources.length, albumCoverResources.length)
        );

        // Filter songs by the given artist
        for (int i = 0; i < totalSongs; i++) {
            if (artistNames[i].equalsIgnoreCase(artist)) {
                filteredSongs.add(new Song(
                        songTitles[i], artistNames[i], songResources[i], albumCoverResources[i]
                ));
            }
        }

        return filteredSongs;
    }
    public List<Song> getSongsByGenre(String genre) {
        List<Song> filteredSongs = new ArrayList<>();

        int totalSongs = Math.min(songTitles.length, Math.min(artistNames.length, songResources.length));

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

    public List<Song> getSongsByCategory(String category) {
        List<Song> filteredSongs = new ArrayList<>();

        String[] songTitles = getSongTitles();
        String[] artistNames = getArtistNames();
        int[] songResources = getSongResources();
        int[] albumCoverResources = getAlbumCoverResources();

        int totalSongs = Math.min(songTitles.length, Math.min(artistNames.length, songResources.length));

        for (int i = 0; i < totalSongs; i++) {
            if (isSongInCategory(category, songTitles[i])) {
                filteredSongs.add(new Song(songTitles[i], artistNames[i], songResources[i], albumCoverResources[i]));
            }
        }
        return filteredSongs;
    }

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

    private boolean isKpopSong(String title) {
        return title.equals("EYES, NOSE, LIPS") || title.equals("OMG");
    }

    private boolean isUSUKSong(String title) {
        return title.equals("Blinding Lights") || title.equals("Sunflower");
    }

    private boolean isVpopSong(String title) {
        return title.equals("Đừng Làm Trái Tim Anh Đau") || title.equals("Chạy Ngay Đi")||title.equals("Lâu Đài Tình Ái")||title.equals("Khu Tao Sống");
    }

    private boolean isJapaneseSong(String title) {
        return title.equals("NIGHT DANCER") || title.equals("Odoriko (踊り子)");
    }


    public void playSong(Context context, int songResource, String songTitle, String artistName, int albumCoverResource) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        currentSongTitle = songTitle;
        currentArtistName = artistName;
        currentAlbumCoverResource = albumCoverResource;
        currentSongResource = songResource;

        mediaPlayer = MediaPlayer.create(context, songResource);
        mediaPlayer.setOnCompletionListener(mp -> stopSong());  // Stop player when song ends
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
