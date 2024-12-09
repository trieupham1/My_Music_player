package com.tdtu.my_music_player.Playlist;

import android.content.Context;
import com.tdtu.my_music_player.SearchSong.Song;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlaylistManager {

    private static PlaylistManager instance;
    private final HashMap<String, List<Song>> playlists;
    private String currentPlaylist;

    private PlaylistManager(Context context) {
        playlists = new HashMap<>();
        createPlaylist("Favorites");
        currentPlaylist = "Favorites";
    }

    public static PlaylistManager getInstance(Context context) {
        if (instance == null) {
            instance = new PlaylistManager(context.getApplicationContext());
        }
        return instance;
    }

    public void createPlaylist(String playlistName) {
        if (!playlists.containsKey(playlistName)) {
            playlists.put(playlistName, new ArrayList<>());
        }
    }

    public List<Song> getPlaylist(String playlistName) {
        return playlists.getOrDefault(playlistName, new ArrayList<>());
    }

    public void addSongToPlaylist(String playlistName, Song song) {
        playlists.get(playlistName).add(song);
    }

    public boolean removeSongFromPlaylist(String playlistName, Song song) {
        return playlists.get(playlistName).remove(song);
    }

    public boolean removePlaylist(String playlistName) {
        if (playlists.containsKey(playlistName)) {
            playlists.remove(playlistName);
            return true;
        }
        return false;
    }

    public List<String> getAllPlaylists() {
        return new ArrayList<>(playlists.keySet());
    }

    public boolean isSongInPlaylist(String playlistName, String songTitle, String songArtist) {
        for (Song song : playlists.get(playlistName)) {
            if (song.getTitle().equalsIgnoreCase(songTitle) && song.getArtist().equalsIgnoreCase(songArtist)) {
                return true;
            }
        }
        return false;
    }
}
