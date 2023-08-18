package com.booksHub.example.pdf.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.booksHub.example.pdf.R;
import com.booksHub.example.pdf.adapters.CategoriesAdapter;
import com.booksHub.example.pdf.models.Category;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private ProgressBar progressBar;
    private DatabaseReference dbCategories;

    private List<Category> categoryList;
    private RecyclerView rvCategories;

    private CategoriesAdapter categoriesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.fh_catProgress);
        progressBar.setVisibility(View.VISIBLE);

        rvCategories = view.findViewById(R.id.fh_catRecycler);
        rvCategories.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);

        rvCategories.setLayoutManager(layoutManager);

        categoryList = new ArrayList<>();
        categoriesAdapter = new CategoriesAdapter(getActivity(), categoryList);

        rvCategories.setAdapter(categoriesAdapter);

        //  Refer to the categories node in the DB
        dbCategories = FirebaseDatabase.getInstance("https://books-hub-3964a-default-rtdb.firebaseio.com/").getReference("categories");
        dbCategories.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    progressBar.setVisibility(View.GONE);
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String name = ds.getKey();
                        String desc = ds.child("desc").getValue(String.class);
                        String thumb = ds.child("thumbnail").getValue(String.class);

                        Category category = new Category(name, desc, thumb);
                        categoryList.add(category);

                        //  Add the categories inside the RecyclerView
                         //  Reload RecyclerViewca
                      //  Toast.makeText(getContext(), "s"+categoryList.indexOf(category), Toast.LENGTH_SHORT).show();
                   categoriesAdapter.notifyItemChanged(categoryList.indexOf(category));

                    }

                    //categoriesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


}
