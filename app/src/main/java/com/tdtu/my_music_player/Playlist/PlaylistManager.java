package com.tdtu.my_music_player.Playlist;

import android.content.Context;
import com.tdtu.my_music_player.SearchSong.Song;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlaylistManager {
    private static PlaylistManager instance;
    private final HashMap<String, List<Song>> playlists; // Store multiple playlists
    private String currentPlaylist; // Tracks the active playlist

    private PlaylistManager(Context context) {
        playlists = new HashMap<>();
        createPlaylist("Favorites"); // Create a default playlist
        currentPlaylist = "Favorites"; // Set the default playlist as active
    }
    public boolean removePlaylist(String playlistName) {
        if (playlists.containsKey(playlistName)) {
            playlists.remove(playlistName);
            return true;
        }
        return false;
    }


    public static PlaylistManager getInstance(Context context) {
        if (instance == null) {
            instance = new PlaylistManager(context.getApplicationContext());
        }
        return instance;
    }

    // Create a new playlist
    public void createPlaylist(String playlistName) {
        if (!playlists.containsKey(playlistName)) {
            playlists.put(playlistName, new ArrayList<>());
        }
    }

    // Retrieve a playlist by name
    public List<Song> getPlaylist(String playlistName) {
        return playlists.getOrDefault(playlistName, new ArrayList<>());
    }

    // Set the current active playlist
    public void setCurrentPlaylist(String playlistName) {
        if (playlists.containsKey(playlistName)) {
            currentPlaylist = playlistName;
        }
    }

    public String getCurrentPlaylist() {
        return currentPlaylist;
    }

    // Add a song to a playlist
    public void addSongToPlaylist(String playlistName, Song song) {
        playlists.get(playlistName).add(song);
    }

    // Check if a song exists in a playlist
    public boolean isSongInPlaylist(String playlistName, String title, String artist) {
        List<Song> playlist = playlists.get(playlistName);
        if (playlist != null) {
            for (Song song : playlist) {
                if (song.getTitle().equalsIgnoreCase(title) && song.getArtist().equalsIgnoreCase(artist)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Add song by properties
    public void addSongToPlaylist(String playlistName, String title, String artist, int resource, int coverResource) {
        List<Song> playlist = playlists.get(playlistName);
        if (playlist != null) {
            playlist.add(new Song(title, artist, resource, coverResource));
        }
    }

    // Remove a song from a playlist
    public boolean removeSongFromPlaylist(String playlistName, Song song) {
        List<Song> playlist = playlists.get(playlistName);
        if (playlist != null) {
            return playlist.remove(song);
        }
        return false;
    }

    // Get all playlist names
    public List<String> getAllPlaylists() {
        return new ArrayList<>(playlists.keySet());
    }
}


