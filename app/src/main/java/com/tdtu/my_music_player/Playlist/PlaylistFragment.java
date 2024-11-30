package com.tdtu.my_music_player.Playlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tdtu.my_music_player.MediaManager.MediaPlayerManager;
import com.tdtu.my_music_player.PlayerSet.MainActivity;
import com.tdtu.my_music_player.R;
import com.tdtu.my_music_player.SearchSong.Song;

import java.util.List;

public class PlaylistFragment extends Fragment {

    private RecyclerView playlistRecyclerView;
    private List<Song> playlist;
    private PlaylistAdapter playlistAdapter;
    private MediaPlayerManager mediaPlayerManager;
    private Button removeButton;
    private int selectedPosition = -1;  // Holds the selected song position for removal

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        playlistRecyclerView = view.findViewById(R.id.playlistRecyclerView);
        removeButton = view.findViewById(R.id.remove_button);

        // Initialize the playlist using PlaylistManager
        PlaylistManager playlistManager = PlaylistManager.getInstance(requireContext());
        playlist = playlistManager.getPlaylist();

        // Set up the RecyclerView and adapter
        playlistAdapter = new PlaylistAdapter(getContext(), playlist, (song, position) -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.playSelectedSong(
                        song.getTitle(),
                        song.getArtist(),
                        song.getResource(),
                        song.getAlbumCoverResource()
                );
            }
            selectedPosition = position;
        });
        playlistRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        playlistRecyclerView.setAdapter(playlistAdapter);

        // Initialize MediaPlayerManager
        mediaPlayerManager = MediaPlayerManager.getInstance();

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

        PlaylistManager playlistManager = PlaylistManager.getInstance(requireContext());
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
            PlaylistManager playlistManager = PlaylistManager.getInstance(requireContext());
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