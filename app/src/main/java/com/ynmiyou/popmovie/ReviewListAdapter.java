package com.ynmiyou.popmovie;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.ynmiyou.popmovie.data.MovieColunms;
import com.ynmiyou.popmovie.data.ReviewColunms;

/**
 * Created by TNT on 16/8/29.
 * extends ArrayAdapter for MovieItem
 */
public class ReviewListAdapter extends CursorAdapter {
    private static final String LOG_TAG = ReviewListAdapter.class.getSimpleName();
    private Context context;

    public static final String[] PROJECTION = new String[] {
            ReviewColunms._ID,ReviewColunms.TMDID, ReviewColunms.TMDMID,ReviewColunms.AUTHOR,
            ReviewColunms.CONTENT, ReviewColunms.URL, ReviewColunms.UPDATED,
    };

    public ReviewListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View item = inflater.inflate(R.layout.reviewinfo_layout, parent, false);
        ViewHolder holder = new ViewHolder();
        //holder.imageTitle = (TextView) row.findViewById(R.id.text);
        holder.content = (TextView) item.findViewById(R.id.review_content);
        holder.author = (TextView) item.findViewById(R.id.review_author);
        item.setTag(holder);
        return item;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.content.setText(cursor.getString(cursor.getColumnIndex(ReviewColunms.CONTENT)));
        holder.author.setText(cursor.getString(cursor.getColumnIndex(ReviewColunms.AUTHOR)));
    }

    // using view holder patthen for further display requirement
   static class ViewHolder {
        //TextView imageTitle;
        TextView content;
        TextView author;
    }
}
