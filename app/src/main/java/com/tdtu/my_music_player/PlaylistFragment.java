package com.tdtu.my_music_player;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

public class PlaylistFragment extends Fragment {

    private ListView playlistListView;
    private List<Song> playlist;
    private ArrayAdapter<Song> playlistAdapter;
    private MediaPlayerManager mediaPlayerManager;
    private Button removeButton;
    private int selectedPosition = -1;  // Holds the selected song position for removal

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        playlistListView = view.findViewById(R.id.playlistListView);
        removeButton = view.findViewById(R.id.remove_button);

        // Initialize the playlist using PlaylistManager
        playlist = PlaylistManager.getInstance().getPlaylist();

        // Set up the ListView adapter with a custom adapter
        playlistAdapter = new ArrayAdapter<>(getContext(), R.layout.list_item, playlist);
        playlistListView.setAdapter(playlistAdapter);

        // Initialize MediaPlayerManager
        mediaPlayerManager = MediaPlayerManager.getInstance();

        // Play song on item click in the playlist
        playlistListView.setOnItemClickListener((parent, view1, position, id) -> {
            Song selectedSong = playlist.get(position);
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.playSelectedSong(
                        selectedSong.getTitle(),
                        selectedSong.getArtist(),
                        selectedSong.getResource(),
                        selectedSong.getAlbumCoverResource()
                );
            }
            // Store the selected position to use for removing
            selectedPosition = position;
        });

        // Set up Remove Button to remove the selected song
        removeButton.setOnClickListener(v -> removeSelectedSong());

        return view;
    }

    // Function to add the current song to the playlist
    public void addCurrentSongToPlaylist() {
        if (mediaPlayerManager == null) {
            Toast.makeText(getContext(), "No song is currently playing", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentSongTitle = mediaPlayerManager.getCurrentSongTitle();
        String currentArtistName = mediaPlayerManager.getCurrentArtistName();
        int currentSongResource = mediaPlayerManager.getCurrentSongResource();
        int currentAlbumCoverResource = mediaPlayerManager.getCurrentAlbumCoverResource();

        PlaylistManager playlistManager = PlaylistManager.getInstance();
        if (playlistManager.isSongInPlaylist(currentSongTitle, currentArtistName)) {
            Toast.makeText(getContext(), "Song is already in the playlist", Toast.LENGTH_SHORT).show();
        } else {
            playlistManager.addSongToPlaylist(
                    currentSongTitle,
                    currentArtistName,
                    currentSongResource,
                    currentAlbumCoverResource
            );
            playlistAdapter.notifyDataSetChanged(); // Update the playlist display
            Toast.makeText(getContext(), "Song added to playlist", Toast.LENGTH_SHORT).show();
        }
    }

    // Function to remove the selected song from the playlist
    private void removeSelectedSong() {
        if (selectedPosition != -1) {
            Song songToRemove = playlist.get(selectedPosition);
            PlaylistManager playlistManager = PlaylistManager.getInstance();
            if (playlistManager.removeSongFromPlaylist(songToRemove)) {
                Toast.makeText(getContext(), "Song removed from playlist", Toast.LENGTH_SHORT).show();
                playlistAdapter.notifyDataSetChanged(); // Update the playlist display
                selectedPosition = -1;  // Reset the selected position
            } else {
                Toast.makeText(getContext(), "Error removing song from playlist", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Please select a song to remove", Toast.LENGTH_SHORT).show();
        }
    }
}
