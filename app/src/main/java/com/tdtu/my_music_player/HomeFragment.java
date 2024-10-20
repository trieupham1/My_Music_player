package com.tdtu.my_music_player;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView categoriesRecyclerView, artistRecyclerView, genreRecyclerView;
    private CategoriesAdapter categoriesAdapter, artistAdapter, genreAdapter;
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

        // Add icons for each category
        categoryIcons = new ArrayList<>();
        categoryIcons.add(R.drawable.kpop);
        categoryIcons.add(R.drawable.usuk);
        categoryIcons.add(R.drawable.vpop);
        categoryIcons.add(R.drawable.japan);

        categoriesAdapter = new CategoriesAdapter(categoriesList, categoryIcons, getContext());
        categoriesRecyclerView.setAdapter(categoriesAdapter);

        // Initialize artist RecyclerView
        artistRecyclerView = view.findViewById(R.id.artist_recycler_view);
        artistRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        artistList = new ArrayList<>();
        artistList.add("Travis Scott");
        artistList.add("Justin Bieber");
        artistList.add("Sơn Tùng M-TP");
        artistList.add("Vaundy");

        // Add icons for each artist
        artistIcons = new ArrayList<>();
        artistIcons.add(R.drawable.travis);
        artistIcons.add(R.drawable.justin);
        artistIcons.add(R.drawable.son_tung);
        artistIcons.add(R.drawable.vaundy);

        artistAdapter = new CategoriesAdapter(artistList, artistIcons, getContext());
        artistRecyclerView.setAdapter(artistAdapter);

        // Initialize genre RecyclerView
        genreRecyclerView = view.findViewById(R.id.genre_recycler_view);
        genreRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        genreList = new ArrayList<>();
        genreList.add("Pop");
        genreList.add("Rock");
        genreList.add("R&B");
        genreList.add("Rap");

        // Add icons for each genre
        genreIcons = new ArrayList<>();
        genreIcons.add(R.drawable.pop);
        genreIcons.add(R.drawable.rock);
        genreIcons.add(R.drawable.rnb);
        genreIcons.add(R.drawable.rap);

        genreAdapter = new CategoriesAdapter(genreList, genreIcons, getContext());
        genreRecyclerView.setAdapter(genreAdapter);

        return view;
    }
}