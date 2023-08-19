package com.booksHub.example.pdf.fragments;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.booksHub.example.pdf.R;
import com.booksHub.example.pdf.adapters.WallpapersAdapter;
import com.booksHub.example.pdf.models.Wallpaper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavouritesFragment extends Fragment {

    private List<Wallpaper> favWallpaperList;
    private RecyclerView rvFavourites;
    private ProgressBar pbFavourites;

    private WallpapersAdapter wallpapersAdapter;

    private DatabaseReference dbFavouriteWallpapers;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourites, container, false);
    }

    //  Users can view this screen when they are logged in
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        favWallpaperList = new ArrayList<>();
        rvFavourites = view.findViewById(R.id.ff_favRecycler);
        pbFavourites = view.findViewById(R.id.ff_favProgress);
        wallpapersAdapter = new WallpapersAdapter(getActivity(), favWallpaperList);

        rvFavourites.setHasFixedSize(true);
        rvFavourites.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvFavourites.setAdapter(wallpapersAdapter);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            //  User is not logged in --> View login Screen in Settings
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_area, new SettingsFragment()).commit();

            return;
        }

        dbFavouriteWallpapers = FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("favourites");

        pbFavourites.setVisibility(View.VISIBLE);

        dbFavouriteWallpapers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                pbFavourites.setVisibility(View.GONE);

                //  read all favourite nodes ie. children
                for (DataSnapshot category : dataSnapshot.getChildren()) {

                    //  read all wallpaper nodes inside the specified category in the above loop
                    for (DataSnapshot wallpaperSnapshot : category.getChildren()) {

                        String id = wallpaperSnapshot.getKey();
                        String title = wallpaperSnapshot.child("title").getValue(String.class);
                        String desc = wallpaperSnapshot.child("desc").getValue(String.class);
                        String url = wallpaperSnapshot.child("url").getValue(String.class);
                        String pdf = wallpaperSnapshot.child("pdf").getValue(String.class);

                        Wallpaper w = new Wallpaper(id, title, desc, url, category.getKey(),pdf);
                        w.isFavourite = true;

                        favWallpaperList.add(w);
                    }
                }
                wallpapersAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}
