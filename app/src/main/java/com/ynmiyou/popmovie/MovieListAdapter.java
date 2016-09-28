package com.ynmiyou.popmovie;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.ynmiyou.popmovie.data.MovieColunms;
import com.ynmiyou.popmovie.data.MovieDatabase;

/**
 * Created by TNT on 16/8/29.
 * extends ArrayAdapter for MovieItem
 */
public class MovieListAdapter extends CursorAdapter {
    private static final String LOG_TAG = MovieListAdapter.class.getSimpleName();
    private Context context;

    public static final String[] PROJECTION = new String[] {
            MovieDatabase.MOVIES + "." + MovieColunms._ID,MovieColunms.TMDID, MovieColunms.OVERVIEW, MovieColunms.POSTERURL,
            MovieDatabase.MOVIES + "." + MovieColunms.UPDATED,MovieColunms.TITLE,
            MovieColunms.VOTEAVERAGE, MovieColunms.VOTECOUNT,MovieColunms.RELEASEDATE
    };

    public MovieListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.context = context;
    }

    /*@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.movieinfo_layout, parent, false);
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
    }*/

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View item = inflater.inflate(R.layout.movieinfo_layout, parent, false);
        ViewHolder holder = new ViewHolder();
        //holder.imageTitle = (TextView) row.findViewById(R.id.text);
        holder.image = (ImageView) item.findViewById(R.id.poster);
        item.setTag(holder);
        return item;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        Picasso picasso = Picasso.with(context);
        //picasso.setLoggingEnabled(true);
        //picasso.setIndicatorsEnabled(true);
        picasso.load(cursor.getString(cursor.getColumnIndex(MovieColunms.POSTERURL)))
                //.placeholder(R.drawable.launcher)
                //.error(R.drawable.launcher)
                .into(holder.image);
        //Log.d(LOG_TAG,"construct image view:" + holder.image.getDrawable());
    }

    // using view holder patthen for further display requirement
   static class ViewHolder {
        //TextView imageTitle;
        ImageView image;
    }
}
