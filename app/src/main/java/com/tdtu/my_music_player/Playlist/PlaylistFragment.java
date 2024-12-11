package com.tdtu.my_music_player.Playlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tdtu.my_music_player.PlayerSet.MainActivity;
import com.tdtu.my_music_player.MediaManager.MediaPlayerManager;
import com.tdtu.my_music_player.R;
import com.tdtu.my_music_player.SearchSong.Song;

import java.util.List;

public class PlaylistFragment extends Fragment {

    private ListView playlistListView;
    private List<Song> playlist;
    private PlaylistAdapter playlistAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        playlistListView = view.findViewById(R.id.playlistListView);
        Button deleteAllButton = view.findViewById(R.id.delete_all_button);

        // Initialize the playlist
        playlist = PlaylistManager.getInstance().getPlaylist();

        // Set up the adapter
        playlistAdapter = new PlaylistAdapter(requireContext(), playlist);
        playlistListView.setAdapter(playlistAdapter);

        // Set the onItemClickListener to play the selected song
        playlistListView.setOnItemClickListener((parent, view1, position, id) -> {
            Song selectedSong = playlist.get(position); // Get the selected song

            // Play the selected song and update the mini-player
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.playSelectedSong(
                        selectedSong.getTitle(),
                        selectedSong.getArtist(),
                        selectedSong.getResource(),
                        selectedSong.getAlbumCoverResource()
                );
            }

            Toast.makeText(requireContext(), "Playing: " + selectedSong.getTitle(), Toast.LENGTH_SHORT).show();
        });

        // Handle "Delete All" button click
        deleteAllButton.setOnClickListener(v -> {
            if (playlist.isEmpty()) {
                Toast.makeText(requireContext(), "Playlist is already empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Clear the playlist
            PlaylistManager.getInstance().getPlaylist().clear();
            playlistAdapter.notifyDataSetChanged(); // Refresh the ListView
            Toast.makeText(requireContext(), "All songs deleted from the playlist.", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
