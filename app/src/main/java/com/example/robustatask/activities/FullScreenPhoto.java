package com.example.robustatask.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.robustatask.R;

public class FullScreenPhoto extends AppCompatActivity {
    ImageView fullImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_photo);
        fullImage=findViewById(R.id.fullscreenimg);

        Intent i =getIntent();
        String getImagePath=i.getExtras().getString("SendPath");
        Bitmap bitmap= BitmapFactory.decodeFile(getImagePath);
        fullImage.setImageBitmap(bitmap);


    }
}
