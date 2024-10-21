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

    private String[] songTitles = {
            "EYES, NOSE, LIPS", "Still With You", "OMG",
            "Die With A Smile", "Blinding Lights", "Sunflower",
            "Đừng Làm Trái Tim Anh Đau", "HÃY TRAO CHO ANH", "Chạy Ngay Đi",
            "NIGHT DANCER", "Odoriko (踊り子)", "Tokyo Flash (東京フラッシュ)",
            "Highest in the Room", "Fein","Yummy", "Peaches",
            "Numb", "Creep",
            "Lâu Đài Tình Ái",
            "Khu Tao Sống"

    };
    private String[] artistNames = {
            "Taeyang", "Jungkook", "NewJeans",
            "Bruno Mars & Lady Gaga", "The Weeknd", "Post Malone",
            "Sơn Tùng M-TP", "Sơn Tùng M-TP ft. Snoop Dogg", "Sơn Tùng M-TP",
            "Imase", "Vaundy", "Vaundy",
            "Travis Scott", "Travis Scott","Justin Bieber", "Justin Bieber",
            "Linkin Park", "Radiohead",
            "Đàm Vĩnh Hưng",
            "Wowy"
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
            R.drawable.kpop1, R.drawable.kpop3,
            R.drawable.usuk2, R.drawable.usuk3,
            R.drawable.vpop1, R.drawable.vpop3,
            R.drawable.japan, R.drawable.japan2,
            R.drawable.travis, R.drawable.fein,R.drawable.justin2, R.drawable.justin3,
            R.drawable.rock1, R.drawable.rock2,
            R.drawable.laudaitinhai,
            R.drawable.khutaosong
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