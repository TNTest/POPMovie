package com.ynmiyou.popmovie;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TNT on 16/8/29.
 * extends ArrayAdapter for MovieItem
 */
public class MovieGridViewAdapter extends ArrayAdapter<MovieItem> {
    private static final String LOG_TAG = MovieGridViewAdapter.class.getSimpleName();
    private Context context;
    private int layoutResourceId;
    private List<MovieItem> data = new ArrayList<>();
    public MovieGridViewAdapter(Context context, int layoutResourceId, List<MovieItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            //holder.imageTitle = (TextView) row.findViewById(R.id.text);
            holder.image = (ImageView) row.findViewById(R.id.poster);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        MovieItem item = data.get(position);
        //holder.imageTitle.setText(item.getTitle());

        //using Picasso to render ImageView
        //Log.d(LOG_TAG,"image url:" + item.getPosterUrl());
        Picasso picasso = Picasso.with(context);
        //picasso.setLoggingEnabled(true);
        //picasso.setIndicatorsEnabled(true);
        picasso.load(item.getPosterUrl())
                //.placeholder(R.drawable.launcher)
                //.error(R.drawable.launcher)
                .into(holder.image);
        //Log.d(LOG_TAG,"construct image view:" + holder.image.getDrawable());
        return row;
    }

    // using view holder patthen for further display requirement
   static class ViewHolder {
        //TextView imageTitle;
        ImageView image;
    }
}
