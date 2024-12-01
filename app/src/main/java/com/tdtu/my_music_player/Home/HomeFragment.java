package com.tdtu.my_music_player.Home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tdtu.my_music_player.Artists.ArtistAdapter;
import com.tdtu.my_music_player.Artists.ArtistSongsActivity;
import com.tdtu.my_music_player.CategoriesSet.CategoriesAdapter;
import com.tdtu.my_music_player.CategoriesSet.MusicCategoryActivity;
import com.tdtu.my_music_player.Genres.GenreSongsActivity;
import com.tdtu.my_music_player.PlayerSet.MainActivity;
import com.tdtu.my_music_player.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView categoriesRecyclerView, artistRecyclerView, genreRecyclerView;
    private CategoriesAdapter categoriesAdapter, genreAdapter;
    private ArtistAdapter artistAdapter;
    private List<String> categoriesList, artistList, genreList;
    private List<Integer> categoryIcons, artistIcons, genreIcons;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize category RecyclerView
        categoriesRecyclerView = view.findViewById(R.id.categories_recycler_view);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        categoriesList = new ArrayList<>();
        categoriesList.add("Kpop");
        categoriesList.add("US&UK");
        categoriesList.add("Vpop");
        categoriesList.add("Japanese songs");

        categoryIcons = new ArrayList<>();
        categoryIcons.add(R.drawable.kpop);
        categoryIcons.add(R.drawable.usuk);
        categoryIcons.add(R.drawable.vpop);
        categoryIcons.add(R.drawable.japan);

        categoriesAdapter = new CategoriesAdapter(categoriesList, categoryIcons, getContext(), category -> {
            Intent intent = new Intent(getContext(), MusicCategoryActivity.class);
            intent.putExtra("categoryName", category);
            startActivity(intent);
        });
        categoriesRecyclerView.setAdapter(categoriesAdapter);

        // Initialize artist RecyclerView
        artistRecyclerView = view.findViewById(R.id.artist_recycler_view);
        artistRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        artistList = new ArrayList<>();
        artistList.add("Travis Scott");
        artistList.add("Justin Bieber");
        artistList.add("Sơn Tùng M-TP");
        artistList.add("Vaundy");

        artistIcons = new ArrayList<>();
        artistIcons.add(R.drawable.travis);
        artistIcons.add(R.drawable.justin);
        artistIcons.add(R.drawable.son_tung);
        artistIcons.add(R.drawable.vaundy);

        artistAdapter = new ArtistAdapter(artistList, artistIcons, getContext(), artist -> {
            Intent intent = new Intent(getContext(), ArtistSongsActivity.class);
            intent.putExtra("artistName", artist);
            startActivity(intent);
        });
        artistRecyclerView.setAdapter(artistAdapter);

        // Initialize genre RecyclerView
        genreRecyclerView = view.findViewById(R.id.genre_recycler_view);
        genreRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        genreList = new ArrayList<>();
        genreList.add("Pop");
        genreList.add("Rock");
        genreList.add("R&B");
        genreList.add("Rap");

        genreIcons = new ArrayList<>();
        genreIcons.add(R.drawable.pop);
        genreIcons.add(R.drawable.rock);
        genreIcons.add(R.drawable.rnb);
        genreIcons.add(R.drawable.rap);

        genreAdapter = new CategoriesAdapter(genreList, genreIcons, getContext(), genre -> {
            Intent intent = new Intent(getContext(), GenreSongsActivity.class);
            intent.putExtra("genreName", genre);
            startActivity(intent);
        });
        genreRecyclerView.setAdapter(genreAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Update the mini-player UI
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateMiniPlayerUI();
        }

        if (categoriesRecyclerView != null) categoriesRecyclerView.requestFocus();
        if (artistRecyclerView != null) artistRecyclerView.requestFocus();
        if (genreRecyclerView != null) genreRecyclerView.requestFocus();
    }
}




