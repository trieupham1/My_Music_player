package com.tdtu.my_music_player.Playlist;

import android.graphics.Color;
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

import com.tdtu.my_music_player.PlayerSet.MainActivity;
import com.tdtu.my_music_player.R;
import com.tdtu.my_music_player.SearchSong.Song;

import java.util.List;

public class PlaylistFragment extends Fragment {

    private RecyclerView playlistListRecyclerView;
    private RecyclerView playlistRecyclerView;
    private PlaylistManager playlistManager;
    private PlaylistNameAdapter playlistNameAdapter;
    private PlaylistAdapter playlistAdapter;
    private Button addPlaylistButton;
    private Button deletePlaylistButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        playlistListRecyclerView = view.findViewById(R.id.playlistListRecyclerView);
        playlistRecyclerView = view.findViewById(R.id.playlistRecyclerView);
        addPlaylistButton = view.findViewById(R.id.add_playlist_button);
        deletePlaylistButton = view.findViewById(R.id.delete_playlist_button);

        playlistManager = PlaylistManager.getInstance(requireContext());

        // Initialize RecyclerView for Playlist Names
        setupPlaylistListRecyclerView();

        // Add Playlist Button
        addPlaylistButton.setOnClickListener(v -> showCreatePlaylistDialog());

        // Delete Playlist Button
        deletePlaylistButton.setOnClickListener(v -> showDeletePlaylistDialog());

        return view;
    }

    private void setupPlaylistListRecyclerView() {
        playlistNameAdapter = new PlaylistNameAdapter(getContext(), playlistManager.getAllPlaylists(), playlistName -> {
            // Load the songs in the selected playlist and display them
            displaySongsInPlaylist(playlistName);
        });

        playlistListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        playlistListRecyclerView.setAdapter(playlistNameAdapter);
    }

    private void displaySongsInPlaylist(String playlistName) {
        List<Song> songs = playlistManager.getPlaylist(playlistName); // Get songs for the selected playlist
        if (songs != null && !songs.isEmpty()) {
            playlistRecyclerView.setVisibility(View.VISIBLE);
            playlistAdapter = new PlaylistAdapter(getContext(), songs, (song, position) -> {
                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity != null) {
                    mainActivity.playSelectedSong(
                            song.getTitle(),
                            song.getArtist(),
                            song.getResource(),
                            song.getAlbumCoverResource()
                    );
                }
            });

            playlistRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            playlistRecyclerView.setAdapter(playlistAdapter);
        } else {
            Toast.makeText(getContext(), "No songs in this playlist", Toast.LENGTH_SHORT).show();
        }
    }

    private void showCreatePlaylistDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Create New Playlist");

        // Create an EditText with black text color
        final android.widget.EditText input = new android.widget.EditText(requireContext());
        input.setHint("Enter playlist name");
        input.setTextColor(Color.BLACK); // Set text color to black
        input.setHintTextColor(Color.GRAY); // Optional: Set hint color to gray
        builder.setView(input);

        // Set buttons with black text color
        builder.setPositiveButton("CREATE", (dialog, which) -> {
            String playlistName = input.getText().toString().trim();
            if (!playlistName.isEmpty()) {
                playlistManager.createPlaylist(playlistName);
                playlistNameAdapter.updatePlaylists(playlistManager.getAllPlaylists());
                Toast.makeText(getContext(), "Playlist created", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Playlist name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());

        // Create and show the dialog
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();

        // Customize button text colors
        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
    }

    private void showDeletePlaylistDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Delete Playlist");

        // Show list of playlists to select for deletion
        final String[] playlistNames = playlistManager.getAllPlaylists().toArray(new String[0]);
        builder.setItems(playlistNames, (dialog, which) -> {
            String playlistToDelete = playlistNames[which];
            if (playlistManager.removePlaylist(playlistToDelete)) {
                playlistNameAdapter.updatePlaylists(playlistManager.getAllPlaylists());
                Toast.makeText(getContext(), "Playlist deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to delete playlist", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
