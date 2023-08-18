package com.booksHub.example.pdf.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.booksHub.example.pdf.R;
import com.booksHub.example.pdf.adapters.The_Slide_items_Pager_Adapter;
import com.booksHub.example.pdf.fragments.FavouritesFragment;
import com.booksHub.example.pdf.fragments.HomeFragment;
import com.booksHub.example.pdf.fragments.SettingsFragment;
import com.booksHub.example.pdf.models.The_Slide_Items_Model_Class;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class HomeActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {
    private ActionBar actionBar;
    private Toolbar toolbar;
    BottomNavigationView bottomNavigationView;
    private List<The_Slide_Items_Model_Class> listItems;
    private ViewPager page;
    private TabLayout tabLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        MobileAds.initialize(this,
                "ca-app-pub-2307196974068839~5976127200");
        initToolbar();
        initNavigationMenu();

        page = findViewById(R.id.my_pager);
        tabLayout = findViewById(R.id.my_tablayout);

        bottomNavigationView = findViewById(R.id.home_BottomMenu);

        //  Implements the interface used below
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        //  Displays the HomeFragment by default
        displayFragment(new HomeFragment());


        // Make a copy of the slides you'll be presenting.
        listItems = new ArrayList<>();
        listItems.add(new The_Slide_Items_Model_Class(R.drawable.item1, "Slider 1 Title"));
        listItems.add(new The_Slide_Items_Model_Class(R.drawable.item1, "Slider 1 Title"));
        listItems.add(new The_Slide_Items_Model_Class(R.drawable.item1, "Slider 1 Title"));
        The_Slide_items_Pager_Adapter itemsPager_adapter = new The_Slide_items_Pager_Adapter(this, listItems);
        page.setAdapter(itemsPager_adapter);

        // The_slide_timer
        java.util.Timer timer = new java.util.Timer();
        timer.scheduleAtFixedRate(new The_slide_timer(), 2000, 3000);
        tabLayout.setupWithViewPager(page, true);

    }

    public class The_slide_timer extends TimerTask {
        @Override
        public void run() {

            HomeActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (page.getCurrentItem() < listItems.size() - 1) {
                        page.setCurrentItem(page.getCurrentItem() + 1);
                    } else
                        page.setCurrentItem(0);
                }
            });
        }
    }

    private void displayFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_area, fragment)
                .commit();
    }

    //  Called when an item from the navigation menu is selected
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment;
        switch (item.getItemId()) {
            case R.id.nav_home:
                page.setVisibility(View.VISIBLE);
                tabLayout.setVisibility(View.VISIBLE);
                fragment = new HomeFragment();
                break;
            case R.id.nav_fav:
                page.setVisibility(View.GONE);
                tabLayout.setVisibility(View.GONE);
                fragment = new FavouritesFragment();
                break;
            case R.id.nav_settings:
                page.setVisibility(View.GONE);
                tabLayout.setVisibility(View.GONE);
                fragment = new SettingsFragment();
                break;
            default:
                fragment = new HomeFragment();
        }
        displayFragment(fragment);
        return true;
    }

    private void initNavigationMenu() {
        NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem item) {
                if (item.getItemId() == R.id.nav_all_inbox) {
                    //  new Favfrag();
                    drawer.closeDrawers();
                }
                if (item.getItemId() == R.id.nav_theme) {

//

                }

                //actionBar.setTitle(item.getTitle());
                drawer.closeDrawers();
                return true;
            }
        });
        nav_view.setItemIconTintList(null);


    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Dictionary");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        }
    }
}