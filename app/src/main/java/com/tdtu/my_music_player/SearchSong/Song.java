package com.tdtu.my_music_player.SearchSong;

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
}

