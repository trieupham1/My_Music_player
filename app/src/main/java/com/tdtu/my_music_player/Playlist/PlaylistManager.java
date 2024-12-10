package com.tdtu.my_music_player.Playlist;

import com.tdtu.my_music_player.SearchSong.Song;

import java.util.ArrayList;
import java.util.List;

public class PlaylistManager {
    private static PlaylistManager instance;
    private List<Song> playlist;

    private PlaylistManager() {
        playlist = new ArrayList<>();
    }

    public static PlaylistManager getInstance() {
        if (instance == null) {
            instance = new PlaylistManager();
        }
        return instance;
    }

    public List<Song> getPlaylist() {
        return playlist;
    }

    public void addSongToPlaylist(String title, String artist, int resource, int coverResource) {
        playlist.add(new Song(title, artist, resource, coverResource));
    }

    public boolean isSongInPlaylist(String title, String artist) {
        for (Song song : playlist) {
            if (song.getTitle().equalsIgnoreCase(title) && song.getArtist().equalsIgnoreCase(artist)) {
                return true; // Song already exists in the playlist
            }
        }
        return false; // Song not found in the playlist
    }
}
