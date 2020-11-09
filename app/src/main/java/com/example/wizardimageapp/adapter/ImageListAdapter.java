package com.example.wizardimageapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.wizardimageapp.R;
import java.util.ArrayList;
import java.util.List;

public class ImageListAdapter extends BaseAdapter {

    private List<Bitmap> images;
    private LayoutInflater inflater;
    private View layoutListImage;

    public ImageListAdapter(Context context, List<Bitmap> images) {
        if (images == null) {
            this.images = new ArrayList<>();
        } else {
            this.images = images;

        }
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.list_action_view, parent, false);
        }
        ImageView setImageAction = view.findViewById(R.id.image_result);
        Bitmap bitmap = images.get(position);
        setImageAction.setImageBitmap(bitmap);
        layoutListImage = view.findViewById(R.id.layout_image);
        setBackgroundViewImage(position);
        return view;
    }

    private void setBackgroundViewImage(int position) {
        if (position%2 == 0) {
            layoutListImage.setBackgroundResource(R.color.colorEventBackgroundLayoutImage);
        } else {
            layoutListImage.setBackgroundResource(R.color.colorOddBackgroundLayoutImage);
        }
    }

}
