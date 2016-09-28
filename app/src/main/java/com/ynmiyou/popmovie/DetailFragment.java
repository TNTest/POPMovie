package com.ynmiyou.popmovie;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.ynmiyou.popmovie.data.FavoriteColunms;
import com.ynmiyou.popmovie.data.MoviesContentProvider;
import com.ynmiyou.popmovie.data.ReviewColunms;
import com.ynmiyou.popmovie.data.VideoColunms;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.ynmiyou.popmovie.Util.connectUrlJson;


public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final int VIDEOINFO_LOADER = 11;
    private static final int REVIEWINFO_LOADER = 12;
    public static final int SYNC_INTERVAL = 1000 * 60 * 60 * 24;
    private ReviewListAdapter rvlapr;
    private VideoListAdapter vdlapr;
    private static final SimpleDateFormat sdf = new SimpleDateFormat();
    private String mSelectionClause;
    private String[] mSelectionArgs;
    private DetailFragment mThis;
    private MovieItem mMi;

    public static final String[] PROJECTION = new String[] {
            FavoriteColunms._ID,FavoriteColunms.TMDMID,FavoriteColunms.UPDATED
    };
    private Cursor mFvCursor;


    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        mMi = (MovieItem)getActivity().getIntent().getParcelableExtra(MainFragment.DETAIL_MSG);
        if (mMi != null) {
            Log.d(LOG_TAG, mMi.toString());
            ((TextView) view.findViewById(R.id.detail_title)).setText(mMi.getTitle());
            ((TextView) view.findViewById(R.id.detail_overview)).setText(mMi.getOverview());
            ((TextView) view.findViewById(R.id.detail_release_date)).setText(mMi.getReleaseDate());
            String voteStr = mMi.getVoteAverage() + "/" + mMi.getVoteCount();
            ((TextView) view.findViewById(R.id.detail_vote)).setText(voteStr);
            Picasso.with(getContext())
                    .load(mMi.getPosterUrl())
                    //.placeholder(R.drawable.launcher)
                    //.error(R.drawable.launcher)
                    .into((ImageView) view.findViewById(R.id.detail_poster));

            //video adapter init
            vdlapr = new VideoListAdapter(getContext(), null, 0);
            ListView lv_vd = (ListView) view.findViewById(R.id.detail_videos);
            lv_vd.setAdapter(vdlapr);
            lv_vd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                    String key = cursor.getString(cursor.getColumnIndex(VideoColunms.KEY));
                    String url = Util.getYoutubeUrlByKey(key);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });

            //init favorite button
            mFvCursor = getContext().getContentResolver().query(MoviesContentProvider.Favorites.withTmdmId(mMi.getTmdId()), PROJECTION, null, null, null);
            try {
                TextView fv_btn = (TextView) view.findViewById(R.id.detail_favorite);
                if (mFvCursor.getCount() > 0) {//already marked
                    fv_btn.setText(R.string.btn_text_unfavorite);
                    fv_btn.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                } else {//not marked
                    fv_btn.setText(R.string.btn_text_favorite);
                    fv_btn.setTextColor(getResources().getColor(R.color.colorAccent));
                }
                fv_btn.setOnClickListener(new TextView.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mFvCursor = getContext().getContentResolver().query(MoviesContentProvider.Favorites.withTmdmId(mMi.getTmdId()), PROJECTION, null, null, null);
                        if (mFvCursor.getCount() > 1) {
                            Log.w(LOG_TAG, "more than 1 record for same movie favorite!!");
                        }
                        if (mFvCursor.getCount() <= 0) {//not marked
                            ContentValues value = new ContentValues();
                            value.put(FavoriteColunms.TMDMID, mMi.getTmdId());
                            value.put(FavoriteColunms.UPDATED, sdf.format(new Date()));
                            getContext().getContentResolver().insert(MoviesContentProvider.Favorites.CONTENT_URI, value);
                            ((TextView) view).setText(R.string.btn_text_unfavorite);
                            ((TextView) view).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        } else {//marked
                            getContext().getContentResolver().delete(MoviesContentProvider.Favorites.withTmdmId(mMi.getTmdId()), null, null);
                            ((TextView) view).setText(R.string.btn_text_favorite);
                            ((TextView) view).setTextColor(getResources().getColor(R.color.colorAccent));
                        }
                    }
                });
            } finally {
                mFvCursor.close();
            }

            //review adapter init
            rvlapr = new ReviewListAdapter(getContext(), null, 0);
            ListView lv_rv = (ListView) view.findViewById(R.id.detail_reviews);
            lv_rv.setAdapter(rvlapr);
            lv_rv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                    String url = cursor.getString(cursor.getColumnIndex(ReviewColunms.URL));
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);

                }
            });
        }
        mThis = this;
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(REVIEWINFO_LOADER, null, this);
        getLoaderManager().initLoader(VIDEOINFO_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG,"onCreateLoader...");
        Uri contentUri = null;
        String relativePath = null;
        String[] projection = null;
        String updateColumnName = null;
        MovieItem mi = (MovieItem)getActivity().getIntent().getParcelableExtra(MainFragment.DETAIL_MSG);
        if (mi != null) {
            switch (id) {
                case VIDEOINFO_LOADER:
                    contentUri = MoviesContentProvider.Videos.CONTENT_URI;
                    relativePath = "videos";
                    projection = VideoListAdapter.PROJECTION;
                    updateColumnName = VideoColunms.UPDATED;
                    break;
                case REVIEWINFO_LOADER:
                    contentUri = MoviesContentProvider.Reviews.CONTENT_URI;
                    relativePath = "reviews";
                    projection = ReviewListAdapter.PROJECTION;
                    updateColumnName = ReviewColunms.UPDATED;
                    break;
                default:
                    Log.w(LOG_TAG, "ID: " + id + " cannot be recognized!");
                    return null;

            }
            String tmdmId = mi.getTmdId();
            mSelectionClause = ReviewColunms.TMDMID + " = ?";
            mSelectionArgs = new String[]{tmdmId};
            boolean needUpdate;
            Cursor cursor = getContext().getContentResolver().query(contentUri, projection, mSelectionClause, mSelectionArgs, null);
            try {
                needUpdate = true;
                Date now = new Date();
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    String update = cursor.getString(cursor.getColumnIndex(updateColumnName));
                    Date updateD = null;
                    try {
                        updateD = sdf.parse(update);
                    } catch (ParseException e) {
                        Log.e(LOG_TAG, "parsing update string to date fail! string: " + update, e);
                    }
                    if (updateD != null && (now.getTime() - updateD.getTime()) <= SYNC_INTERVAL) {
                        Log.d(LOG_TAG, "data is new, no update excuted.");
                        needUpdate = false;
                    }
                }
            } finally {
                cursor.close();
            }
            if (needUpdate) { //fetch remote data if no data or data is too old
                new FetchMovieDetailTask().execute(tmdmId, String.valueOf(id), relativePath, contentUri.toString(),getString(R.string.tmd_api_base_url));
            }
            return new CursorLoader(getActivity(),
                    contentUri,
                    projection,
                    mSelectionClause,
                    mSelectionArgs,
                    null);
        }
        return null;
    }

    private class FetchMovieDetailTask extends AsyncTask<String, Void, Integer> {
        /** The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute()
         * prefs[0] = order by
         * */
        protected Integer doInBackground(String... args) {
            String tmdmId = args[0];
            int id = Integer.valueOf(args[1]);
            String relativePath = args[2];
            Uri contentUri = Uri.parse(args[3]);
            String apiBaseUrl = args[4];
            Date now = new Date();
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https").appendEncodedPath(apiBaseUrl)
                    .appendEncodedPath("movie")
                    .appendEncodedPath(tmdmId)
                    .appendEncodedPath(relativePath)
                    .appendQueryParameter("api_key", BuildConfig.MOVIE_DB_API_KEY);
            String apiUrlStr = builder.build().toString();
            Log.d(LOG_TAG, "url: " + apiUrlStr);
            String json = connectUrlJson(apiUrlStr);
            if (TextUtils.isEmpty(json)) {
                Log.w(LOG_TAG, "Response is empty!");
                return null;
            } else { //delete and insert newer data
                try {
                    ContentValues[] values;
                    switch (id) {
                        case VIDEOINFO_LOADER:
                            VideoItem[] vis = Util.getVideoDataFromJson(json);
                            if (vis.length == 0) {
                                ContentValues value = new ContentValues();
                                value.put(VideoColunms.KEY, Util.INVALID_STR);
                                value.put(VideoColunms.TMDID, Util.INVALID_STR);
                                value.put(VideoColunms.TMDMID, tmdmId);
                                value.put(VideoColunms.NAME, Util.INVALID_STR);
                                value.put(VideoColunms.SITE, Util.INVALID_STR);
                                value.put(VideoColunms.TYPE, Util.INVALID_STR);
                                value.put(VideoColunms.UPDATED, sdf.format(now));
                                ContentValues invalidValue[]={value};
                                values = invalidValue;
                            } else {
                                values = new ContentValues[vis.length];
                                for (int i = 0; i < vis.length; i++) {
                                    ContentValues value = new ContentValues();
                                    value.put(VideoColunms.KEY, vis[i].getKey());
                                    value.put(VideoColunms.TMDID, vis[i].getTmdId());
                                    value.put(VideoColunms.TMDMID, tmdmId);
                                    value.put(VideoColunms.NAME, vis[i].getName());
                                    value.put(VideoColunms.SITE, vis[i].getSite());
                                    value.put(VideoColunms.TYPE, vis[i].getType());
                                    value.put(VideoColunms.UPDATED, sdf.format(now));
                                    values[i] = value;
                                }
                            }

                            break;
                        case REVIEWINFO_LOADER:
                            ReviewItem[] ris = Util.getReviewDataFromJson(json);
                            if (ris.length == 0) {
                                ContentValues value = new ContentValues();
                                value.put(ReviewColunms.AUTHOR, Util.INVALID_STR);
                                value.put(ReviewColunms.TMDID, Util.INVALID_STR);
                                value.put(ReviewColunms.TMDMID, tmdmId);
                                value.put(ReviewColunms.CONTENT, Util.INVALID_STR);
                                value.put(ReviewColunms.URL, Util.INVALID_STR);
                                value.put(ReviewColunms.UPDATED, sdf.format(now));
                                ContentValues invalidValue[]={value};
                                values = invalidValue;
                            } else {
                                values = new ContentValues[ris.length];
                                for (int i = 0; i < ris.length; i++) {
                                    ContentValues value = new ContentValues();
                                    value.put(ReviewColunms.AUTHOR, ris[i].getAuthor());
                                    value.put(ReviewColunms.TMDID, ris[i].getTmdId());
                                    value.put(ReviewColunms.TMDMID, tmdmId);
                                    value.put(ReviewColunms.CONTENT, ris[i].getContent());
                                    value.put(ReviewColunms.URL, ris[i].getUrl());
                                    value.put(ReviewColunms.UPDATED, sdf.format(now));
                                    values[i] = value;
                                }
                            }
                            break;
                        default:
                            Log.w(LOG_TAG, "ID: " + id + " cannot be recognized!");
                            return null;

                    }
                    if (mThis != null && mThis.getContext() != null && mThis.getContext().getContentResolver() != null) {
                        mThis.getContext().getContentResolver().delete(contentUri, mSelectionClause, mSelectionArgs);
                        mThis.getContext().getContentResolver().bulkInsert(contentUri, values);
                    } else {
                        Log.e(LOG_TAG,"something is null! (mThis.getContext().getContentResolver())");
                    }
                    return id;
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "parsing json fail!");
                    return null;
                }
            }
        }
        protected void onPostExecute(Integer loaderId) {
            if (isAdded()) {
                getLoaderManager().restartLoader(loaderId, null, mThis);
            }
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(LOG_TAG,"onLoadFinished...");
        switch (loader.getId()){
            case VIDEOINFO_LOADER:
                if (vdlapr != null)
                    vdlapr.swapCursor(data);
                break;
            case REVIEWINFO_LOADER:
                if (rvlapr != null)
                    rvlapr.swapCursor(data);
                break;
            default:
                Log.w(LOG_TAG,"ID: " + loader.getId() + " cannot be recognized!");
                return;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(LOG_TAG,"onLoadFinished...");
        switch (loader.getId()){
            case VIDEOINFO_LOADER:
                vdlapr.swapCursor(null);
                break;
            case REVIEWINFO_LOADER:
                rvlapr.swapCursor(null);
                break;
            default:
                Log.w(LOG_TAG,"ID: " + loader.getId() + " cannot be recognized!");
                return;
        }
    }
}
