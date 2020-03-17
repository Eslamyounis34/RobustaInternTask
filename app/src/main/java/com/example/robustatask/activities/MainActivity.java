package com.example.robustatask.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.robustatask.R;
import com.example.robustatask.fragments.CameraFragment;
import com.example.robustatask.fragments.HomeFragment;
import com.example.robustatask.fragments.PhotosFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PhotosFragment photosFragment = new PhotosFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.containerlayout, photosFragment).commit();

        bottomNavigationView = findViewById(R.id.bottomnav);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.listicon) {
                    PhotosFragment homeFragment = new PhotosFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.containerlayout, homeFragment).commit();
                } else if (id == R.id.cameraicon) {
                    CameraFragment cameraFragment = new CameraFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.containerlayout, cameraFragment).commit();
                }
                else if (id == R.id.homeicon) {
                    HomeFragment homeFragment = new HomeFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.containerlayout, homeFragment).commit();
                }
                return true;
            }
        });


    }


}

