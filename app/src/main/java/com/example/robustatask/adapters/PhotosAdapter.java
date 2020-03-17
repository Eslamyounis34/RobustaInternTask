package com.example.robustatask.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.robustatask.R;

import java.util.ArrayList;

public class PhotosAdapter extends BaseAdapter {

    ArrayList<String>bookName=new ArrayList<>();
    ArrayList<String>pathList=new ArrayList<>();

    Activity ac ;

    public PhotosAdapter(ArrayList<String> bookName, ArrayList<String> pathList, Activity ac) {
        this.bookName = bookName;
        this.pathList = pathList;
        this.ac = ac;
    }

    @Override
    public int getCount() {
        return pathList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=ac.getLayoutInflater();
        View v=inflater.inflate(R.layout.photos_item,null);
        ImageView imageView=v.findViewById(R.id.weatherimage);
        Bitmap myBitmap = BitmapFactory.decodeFile(pathList.get(position));

        imageView.setImageBitmap(myBitmap);

        return v;
    }
}
