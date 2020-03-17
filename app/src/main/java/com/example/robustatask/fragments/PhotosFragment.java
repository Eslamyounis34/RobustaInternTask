package com.example.robustatask.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.robustatask.activities.FullScreenPhoto;
import com.example.robustatask.adapters.PhotosAdapter;
import com.example.robustatask.R;

import java.io.File;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PhotosFragment extends Fragment {

    ArrayList<String> arrayList;
    ArrayList<String> pathList;
    ListView photosList;
    PhotosAdapter adapter;

    private static final int PERMISSION_REQUEST_CODE = 2;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v= inflater.inflate(R.layout.fragment_photos, container, false);
        photosList=v.findViewById(R.id.photosls);
        arrayList = new ArrayList<>();
        pathList = new ArrayList<>();
        adapter = new PhotosAdapter(arrayList, pathList, getActivity());

        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkPermission()) {
                File dir = new File(Environment.getExternalStorageDirectory() + "/RobustaTaskImages");
                if (dir.exists()) {
                    Log.d("path", dir.toString());
                    File list[] = dir.listFiles();
                    for (int i = 0; i < list.length; i++) {
                        arrayList.add(list[i].getName());
                        pathList.add(list[i].getPath());
                        photosList.setAdapter(adapter);
                    }

                } else {
                    requestPermission(); // Code for permission
                }
            } else {
                File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/RobustaTaskImages");
                if (dir.exists()) {
                    Log.d("path", dir.toString());
                    File list[] = dir.listFiles();
                    for (int i = 0; i < list.length; i++) {
                        arrayList.add(list[i].getName());
                        pathList.add(list[i].getPath());
                        photosList.setAdapter(adapter);
                    }
                }
            }

            photosList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent=new Intent(getContext(), FullScreenPhoto.class);
                    intent.putExtra("SendPath",pathList.get(i));
                    startActivity(intent);
                }
            });

        }


        return v;
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(getActivity(), "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }
}