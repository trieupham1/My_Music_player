package com.tdtu.my_music_player.SearchSong;

import java.util.Objects;

public class Song {
    private final String title; // Immutable property for title
    private final String artist; // Immutable property for artist
    private final int resource; // Resource ID for the song file
    private final int albumCoverResource; // Resource ID for the album cover image

    public Song(String title, String artist, int resource, int albumCoverResource) {
        this.title = title;
        this.artist = artist;
        this.resource = resource;
        this.albumCoverResource = albumCoverResource;
    }

    // Getter for song title
    public String getTitle() {
        return title;
    }

    // Getter for artist name
    public String getArtist() {
        return artist;
    }

    // Getter for music file resource ID
    public int getResource() {
        return resource;
    }

    // Getter for album cover resource ID
    public int getAlbumCoverResource() {
        return albumCoverResource;
    }


    @Override
    public String toString() {
        return title + " - " + artist; // Display format for debugging or UI
    }

    // Override equals() to compare songs based on title and artist
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Song song = (Song) obj;
        return title.equals(song.title) && artist.equals(song.artist);
    }

    // Override hashCode() to ensure consistency with equals()
    @Override
    public int hashCode() {
        return Objects.hash(title, artist);
    }
}
