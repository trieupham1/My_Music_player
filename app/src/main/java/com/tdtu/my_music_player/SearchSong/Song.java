package com.tdtu.my_music_player.SearchSong;

import java.util.Objects;

public class Song {
    private String title;
    private String artist;
    private int resource;
    private int albumCoverResource;

    public Song(String title, String artist, int resource, int albumCoverResource) {
        this.title = title;
        this.artist = artist;
        this.resource = resource;
        this.albumCoverResource = albumCoverResource;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public int getResource() {
        return resource;
    }

    public int getAlbumCoverResource() {
        return albumCoverResource;
    }

    @Override
    public String toString() {
        return title + " - " + artist; // Display format in the ListView
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
