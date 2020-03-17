package com.example.robustatask.fragments;


import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.robustatask.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * A simple {@link Fragment} subclass.
 */
public class CameraFragment extends Fragment {

    static final int RESULT_CAMERA_IMAGE=2000;
    ImageView capturedPhoto;
    Button share;
    Bitmap photo;
    BitmapDrawable drawable;
    Bitmap bitmap;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      View v=  inflater.inflate(R.layout.fragment_camera, container, false);
        share=v.findViewById(R.id.share);
        capturedPhoto=v.findViewById(R.id.capturedImage);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage();

            }
        });




        if (ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, RESULT_CAMERA_IMAGE);
        }else {
            requestCamPermission();
        }
        return v;
    }

    private void requestCamPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.CAMERA))
        {}
        else
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},RESULT_CAMERA_IMAGE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {

        super.onActivityResult(requestCode, resultCode, data);
        switch ( requestCode) {
            case RESULT_CAMERA_IMAGE:
                Toast.makeText(getContext(), "DONE", Toast.LENGTH_SHORT).show();
                Bundle extras=data.getExtras();
                photo = (Bitmap) extras.get("data");
                capturedPhoto.setImageBitmap(photo);





        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case RESULT_CAMERA_IMAGE:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                    Toast.makeText(getContext(), "OK", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getContext(), "Permission Denied ", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void openCamera() {
        Intent photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(photo, RESULT_CAMERA_IMAGE);
    }
    public void saveImage()
    {
        drawable=(BitmapDrawable)capturedPhoto.getDrawable();
        bitmap=drawable.getBitmap();

        FileOutputStream outputStream =null;

        File sdCArd=Environment.getExternalStorageDirectory();

        File directory=new File(sdCArd.getAbsolutePath()+"/RobustaTaskImages");
        directory.mkdir();

        String fileName=String.format("%d.jpg",System.currentTimeMillis());
        File outFile=new File(directory,fileName);

        try {
            outputStream =new FileOutputStream(outFile);
            Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

            Canvas canvas = new Canvas(mutableBitmap);

            Paint paint = new Paint();
            paint.setColor(Color.WHITE); // Text Color
            paint.setTextSize(12); // Text Size
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));

            canvas.drawBitmap(mutableBitmap, 0, 0, paint);
            canvas.drawText("Testing...", 25, 25, paint);

            mutableBitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
            outputStream.flush();
            outputStream.close();
            capturedPhoto.setImageBitmap(mutableBitmap);
            Uri  photo_uri= FileProvider.getUriForFile(getContext(),getActivity().getPackageName()+".provider",outFile);



            Intent intent=new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(outFile));
            getActivity().sendBroadcast(intent);

            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/*");
            share.putExtra(Intent.EXTRA_STREAM, photo_uri);
            startActivity(Intent.createChooser(share, "Share via"));

        }catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
