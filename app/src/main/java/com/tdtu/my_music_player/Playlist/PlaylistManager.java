package com.tdtu.my_music_player.Playlist;

import android.content.Context;

import com.tdtu.my_music_player.SearchSong.Song;

import java.util.ArrayList;
import java.util.List;

public class PlaylistManager {
    private static PlaylistManager instance;
    private final List<Song> playlist;
    private Song currentSong;

    private Context context; // Store the context if needed

    private PlaylistManager(Context context) {
        this.context = context.getApplicationContext(); // Use application context to avoid leaks
        playlist = new ArrayList<>();
    }

    public static PlaylistManager getInstance(Context context) {
        if (instance == null) {
            instance = new PlaylistManager(context); // Pass the context here
        }
        return instance;
    }

    public void setCurrentSong(Song song) {
        this.currentSong = song;
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public void addSongToPlaylist(String title, String artist, int resource, int coverResource) {
        playlist.add(new Song(title, artist, resource, coverResource));
    }

    public List<Song> getPlaylist() {
        return playlist;
    }

    public boolean isSongInPlaylist(String title) {
        for (Song song : playlist) {
            if (song.getTitle().equalsIgnoreCase(title)) {
                return true;
            }
        }
        return false;
    }

    public boolean isSongInPlaylist(String title, String artist) {
        for (Song song : playlist) {
            if (song.getTitle().equalsIgnoreCase(title) && song.getArtist().equalsIgnoreCase(artist)) {
                return true;
            }
        }
        return false;
    }

    public boolean removeSongFromPlaylist(Song song) {
        return playlist.remove(song);
    }
}