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
import com.ynmiyou.popmovie.data.VideoColunms;

/**
 * Created by TNT on 16/8/29.
 * extends ArrayAdapter for MovieItem
 */
public class VideoListAdapter extends CursorAdapter {
    private static final String LOG_TAG = VideoListAdapter.class.getSimpleName();
    private Context context;

    public static final String[] PROJECTION = new String[] {
            VideoColunms._ID,VideoColunms.KEY,VideoColunms.NAME,VideoColunms.SITE,
            VideoColunms.TMDID,VideoColunms.TYPE,VideoColunms.UPDATED, VideoColunms.TMDMID
    };

    public VideoListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View item = inflater.inflate(R.layout.videoinfo_layout, parent, false);
        ViewHolder holder = new ViewHolder();
        //holder.imageTitle = (TextView) row.findViewById(R.id.text);
        holder.videoInfo = (TextView) item.findViewById(R.id.video_info);
        holder.playImg = (ImageView) item.findViewById(R.id.video_play_icon);
        item.setTag(holder);
        return item;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        VideoListAdapter.ViewHolder holder = (VideoListAdapter.ViewHolder) view.getTag();
        String videoName = cursor.getString(cursor.getColumnIndex(VideoColunms.NAME));
        holder.videoInfo.setText(videoName);
        if (Util.INVALID_STR.equals(videoName)){
            holder.playImg.setImageDrawable(null);
        }

    }
   static class ViewHolder {
        //TextView imageTitle;
        TextView videoInfo;
        ImageView playImg;
    }
}
