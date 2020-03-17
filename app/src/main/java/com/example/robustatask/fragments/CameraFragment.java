package com.example.robustatask.fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.robustatask.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


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
    TextView weatherTx,placeNameTx;
    Button getWeatherData;
    String baseUrl="http://api.openweathermap.org/data/2.5/weather?q=";
    String apiKey="02e430742041faae5137dd5caa87e7f9";
    String desc="";
    String city_Name="";
    String temp="";
    private FusedLocationProviderClient client;
    private static final int REQUEST_LOCATION = 1;







    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=  inflater.inflate(R.layout.fragment_camera, container, false);

        share=v.findViewById(R.id.share);
        capturedPhoto=v.findViewById(R.id.capturedImage);
        weatherTx=v.findViewById(R.id.weatherData);
        placeNameTx=v.findViewById(R.id.currentplacename);
        getWeatherData=v.findViewById(R.id.coordinates);

        client= LocationServices.getFusedLocationProviderClient(getActivity());

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage();

            }
        });
        fetchLocation();




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
            String allWeatherData=weatherTx.getText().toString();
            String placeName=placeNameTx.getText().toString();
            String shareData=allWeatherData+"\n"+placeName;
            Log.e("CHECKDATA",shareData);
            canvas.drawText(shareData, 25, 25, paint);

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


    private void weatherData(String city)
    {

        String full_Url=baseUrl+city+"&appid="+apiKey+"&units=metric ";

        RequestQueue q= Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest request=new StringRequest(Request.Method.GET, full_Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object=new JSONObject(response);
                    JSONObject mainObj=object.getJSONObject("main");
                    JSONArray array=object.getJSONArray("weather");
                    for (int i=0;i<array.length();i++)
                    {
                        JSONObject getobj = array.getJSONObject(i);
                        desc = getobj.getString("description");
                    }
                    city_Name = object.getString("name");
                    temp = String.valueOf(mainObj.getDouble("temp"));
                    weatherTx.setText(desc+" "+city_Name+" "+temp);




                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        q.add(request);
    }

    public void fetchLocation()
    {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getActivity(),
                ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Required Location Permission")
                        .setMessage("You have to give this permission to acess this feature")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_LOCATION);

                            }

                        })  .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                        .create()
                        .show();

            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

            }
        } else {
            // Permission has already been granted
            client.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    Double latittude = location.getLatitude();
                    Double longitude = location.getLongitude();
                    Log.e("getDAta",latittude.toString()+" "+longitude.toString());

                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(getContext(), Locale.getDefault());

                    try {
                        addresses = geocoder. getFromLocation(latittude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        String state = addresses.get(0).getAdminArea();
                        String placeName=addresses.get(0).getLocality();

                        String arr[] = state.split(" ", 2);

                        String cityName = arr[0];
                        weatherData(cityName);
                        placeNameTx.setText(placeName);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    weatherData(city_Name);

                }
            });
        }
    }


}
