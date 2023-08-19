package com.booksHub.example.pdf.activities;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

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

public class WallpapersActivity extends AppCompatActivity {

    private List<Wallpaper> wallpaperList, wallpaperFavList;
    private RecyclerView rvWallpaper;
    private WallpapersAdapter wallpapersAdapter;

    private DatabaseReference dbWallpapers, dbFavourites;
    private ProgressBar wallProgress;
    ImageView cat_Image;
    TextView cat_txt;
    String themkey, themMode;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpapers);

        sharedpreferences = getApplicationContext().getSharedPreferences(getPackageName(), MODE_PRIVATE);


        Bundle extras = getIntent().getExtras();
        byte[] byteArray = extras.getByteArray("picture");
        final String category = extras.getString("category");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        cat_Image = findViewById(R.id.cat_Image);
        cat_Image.setImageBitmap(bmp);

        cat_txt = findViewById(R.id.cat_txt);
        cat_txt.setText(category);

        Toolbar toolbar = findViewById(R.id.wallpaper_toolbar);
        toolbar.setTitle(category);
        setSupportActionBar(toolbar);


        wallpaperList = new ArrayList<>();
        wallpaperFavList = new ArrayList<>();

        rvWallpaper = findViewById(R.id.wall_wallRecycler);
        rvWallpaper.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(WallpapersActivity.this, 2);

        rvWallpaper.setLayoutManager(layoutManager);

        wallpapersAdapter = new WallpapersAdapter(this, wallpaperList);

        rvWallpaper.setAdapter(wallpapersAdapter);

        wallProgress = findViewById(R.id.wall_wallProgress);


        dbWallpapers = FirebaseDatabase.getInstance().getReference("images").child(category);


        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            dbFavourites = FirebaseDatabase.getInstance().getReference("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("favourites")
                    .child(category);
            fetchFavouriteWallpapers(category);
        } else {
            fetchWallpapers(category);
        }

    }

    //  Fetch users favourite wallpapers from category
    private void fetchFavouriteWallpapers(final String category) {
        wallProgress.setVisibility(View.VISIBLE);
        dbFavourites.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                wallProgress.setVisibility(View.GONE);
                if (dataSnapshot.exists()) {
                    for (DataSnapshot wallpaperSnapshot : dataSnapshot.getChildren()) {

                        String id = wallpaperSnapshot.getKey();
                        String title = wallpaperSnapshot.child("title").getValue(String.class);
                        String desc = wallpaperSnapshot.child("desc").getValue(String.class);
                        String url = wallpaperSnapshot.child("url").getValue(String.class);
                        String pdf = wallpaperSnapshot.child("pdf").getValue(String.class);
                        Wallpaper w = new Wallpaper(id, title, desc, url, category, pdf);


                        wallpaperFavList.add(w);

                    }
                }
                fetchWallpapers(category);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //  Fetch all the wallpapers of the category
    private void fetchWallpapers(final String category) {
        wallProgress.setVisibility(View.VISIBLE);
        dbWallpapers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                wallProgress.setVisibility(View.GONE);
                if (dataSnapshot.exists()) {
                    for (DataSnapshot wallpaperSnapshot : dataSnapshot.getChildren()) {

                        String id = wallpaperSnapshot.getKey();
                        String title = wallpaperSnapshot.child("title").getValue(String.class);
                        String desc = wallpaperSnapshot.child("desc").getValue(String.class);
                        String url = wallpaperSnapshot.child("url").getValue(String.class);
                        String pdf = wallpaperSnapshot.child("pdf").getValue(String.class);
                        Wallpaper w = new Wallpaper(id, title, desc, url, category, pdf);

                        //  Check if wallpaper is favorite
                        if (isFavourite(w)) {
                            w.isFavourite = true;
                        }

                        wallpaperList.add(w);

                    }
                    wallpapersAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean isFavourite(Wallpaper wallpaper) {
        for (Wallpaper f : wallpaperFavList) {
            if (f.id.equals(wallpaper.id)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pdf_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_day_night) {

            Dialog dialog = new Dialog(WallpapersActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialogbox_them);

            dialog.getWindow().setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup_them);
            TextView textViewOk = dialog.findViewById(R.id.textView_ok_them);
            TextView textViewCancel = dialog.findViewById(R.id.textView_cancel_them);


            themkey = sharedpreferences.getString("key", "");
            switch (themkey) {
                case "light":
                    radioGroup.check(radioGroup.getChildAt(0).getId());
                    break;
                case "dark":
                    radioGroup.check(radioGroup.getChildAt(1).getId());
                    break;
            }

            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                RadioButton rb = group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    switch (checkedId) {
                        case R.id.radioButton_light_them:
                            themMode = "light";

                            break;
                        case R.id.radioButton_dark_them:
                            themMode = "dark";
                            break;
                        default:
                            break;
                    }
                }
            });

            textViewOk.setOnClickListener(vOk -> {
                editor = sharedpreferences.edit();
                editor.putString("key", themMode);
                editor.commit();

                dialog.dismiss();

            });

            textViewCancel.setOnClickListener(vCancel -> dialog.dismiss());

            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
