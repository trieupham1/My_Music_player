package com.tdtu.my_music_player;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchFragment extends Fragment {

    private EditText searchEditText;
    private ListView listView;
    private ListView searchResultsListView;

    private String[] songTitles = {"First Song", "Second Song", "Fein Song", "Khu tao sống", "Lâu Đài tình ái"};
    private String[] artistNames = {"Artist One", "Artist Two", "Travis Scott", "Wowy", "Đàm Vĩnh Hưng"};
    private int[] songResources = {R.raw.first, R.raw.second, R.raw.fein, R.raw.khutaosong, R.raw.laudaitinhai};
    private int[] albumCoverResources = {
            R.drawable.first,
            R.drawable.second,
            R.drawable.fein,
            R.drawable.khutaosong,
            R.drawable.damvinhhung
    };

    private List<String> searchResults;
    private ArrayAdapter<String> searchResultsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchEditText = view.findViewById(R.id.searchEditText);
        listView = view.findViewById(R.id.listView);
        searchResultsListView = view.findViewById(R.id.searchResultsListView);

        searchResults = new ArrayList<>();
        searchResultsAdapter = new ArrayAdapter<>(getContext(), R.layout.list_item, R.id.itemTextView, searchResults);
        searchResultsListView.setAdapter(searchResultsAdapter);
        searchResultsListView.setVisibility(View.GONE);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.list_item, R.id.itemTextView, songTitles);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.playSelectedSong(songTitles[position], artistNames[position], songResources[position], albumCoverResources[position]);
            }
        });

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

        searchResultsListView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedSong = searchResults.get(position);
            int index = Arrays.asList(songTitles).indexOf(selectedSong);
            if (index >= 0) {
                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity != null) {
                    mainActivity.playSelectedSong(songTitles[index], artistNames[index], songResources[index], albumCoverResources[index]);
                }
            }
        });

        return view;
    }

    private void filterSongs(String query) {
        searchResults.clear();
        if (TextUtils.isEmpty(query)) {
            searchResultsListView.setVisibility(View.GONE);
        } else {
            for (String songTitle : songTitles) {
                if (songTitle.toLowerCase().contains(query.toLowerCase())) {
                    searchResults.add(songTitle);
                }
            }
            if (searchResults.isEmpty()) {
                searchResultsListView.setVisibility(View.GONE);
            } else {
                searchResultsListView.setVisibility(View.VISIBLE);
                searchResultsAdapter.notifyDataSetChanged();
            }
        }
    }
}