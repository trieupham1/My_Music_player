package com.tdtu.my_music_player.SearchSong;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tdtu.my_music_player.PlayerSet.MainActivity;
import com.tdtu.my_music_player.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchFragment extends Fragment {

    private EditText searchEditText;
    private RecyclerView searchResultsRecyclerView;

    private String[] songTitles = {
            "EYES, NOSE, LIPS", "OMG", "Blinding Lights",
            "Sunflower", "Đừng Làm Trái Tim Anh Đau", "Chạy Ngay Đi",
            "NIGHT DANCER", "Odoriko (踊り子)",
            "Highest in the Room", "Fein","Yummy", "Peaches", "Numb", "Creep","Lâu Đài Tình Ái","Khu Tao Sống"
    };

    private String[] artistNames = {
            "TAEYANG", "NewJeans", "The Weekend",
            "Post Malone", "Sơn Tùng M-TP", "Sơn Tùng M-TP", "Imase",
            "Vaundy", "Travis Scott","Travis Scott","Justin Bieber", "Justin Bieber",
            "Linkin Park", "Radiohead","Đàm Vĩnh Hưng","Wowy","Travis Scott"
    };

    private int[] songResources = {
            R.raw.kpop1, R.raw.kpop3,
            R.raw.usuk2, R.raw.usuk3,
            R.raw.vpop1, R.raw.vpop3,
            R.raw.jpop1, R.raw.jpop2,
            R.raw.travis,R.raw.fein ,R.raw.justin, R.raw.justin2,
            R.raw.rock1, R.raw.rock2,
            R.raw.laudaitinhai,
            R.raw.khutaosong
    };

    private int[] albumCoverResources = {
            R.drawable.kpop1, R.drawable.kpop3, R.drawable.usuk2,
            R.drawable.usuk3, R.drawable.vpop1, R.drawable.vpop3, R.drawable.japan1,
            R.drawable.japan2, R.drawable.travis,R.drawable.fein, R.drawable.justin2,
            R.drawable.justin3, R.drawable.rock1, R.drawable.rock2,R.drawable.laudaitinhai,R.drawable.khutaosong
    };

    private List<Song> searchResults;
    private SongAdapter searchAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchEditText = view.findViewById(R.id.searchEditText);
        searchResultsRecyclerView = view.findViewById(R.id.searchResultsRecyclerView);

        searchResults = new ArrayList<>();
        searchAdapter = new SongAdapter(getContext(), searchResults, song -> {
            // Handle song click
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.playSelectedSong(song.getTitle(), song.getArtist(), song.getResource(), song.getAlbumCoverResource());
            }
        });
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchResultsRecyclerView.setAdapter(searchAdapter);

        // Example of setting initial songs in the list
        List<Song> allSongs = getAllSongs();
        for (Song song : allSongs) {
            searchResults.add(song);
        }

        // Listen for changes in search query
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSongs(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void filterSongs(String query) {
        searchResults.clear();
        if (TextUtils.isEmpty(query)) {
            searchAdapter.notifyDataSetChanged();
            return;
        }

        // Filter songs based on search query
        for (Song song : getAllSongs()) {
            if (song.getTitle().toLowerCase().contains(query.toLowerCase())) {
                searchResults.add(song);
            }
        }
        searchAdapter.notifyDataSetChanged();
    }

    private List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();
        for (int i = 0; i < songTitles.length; i++) {
            songs.add(new Song(songTitles[i], artistNames[i], songResources[i], albumCoverResources[i]));
        }
        return songs;
    }
}