package com.ynmiyou.popmovie.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.ynmiyou.popmovie.BuildConfig;
import com.ynmiyou.popmovie.MovieItem;
import com.ynmiyou.popmovie.R;
import com.ynmiyou.popmovie.Util;
import com.ynmiyou.popmovie.data.MovieColunms;
import com.ynmiyou.popmovie.data.MoviesContentProvider;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class POPMovieSyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String LOG_TAG = POPMovieSyncAdapter.class.getSimpleName();
    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 60(1 hour) * 24 = 24 hours
    public static final int SYNC_INTERVAL = 60 * 60 * 24;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    //private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    //private static final int WEATHER_NOTIFICATION_ID = 3004;


    private static final String[] NOTIFY_WEATHER_PROJECTION = new String[] {
//            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
//            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
//            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
//            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC
    };

    public POPMovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }



    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");
        //SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        //Log.d(LOG_TAG,"location key:" + getString(R.string.pref_location_key));
        //String orderBy = sharedPref.getString(getContext().getString(R.string.pref_movie_order_key),
        //        getContext().getString(R.string.pref_movie_order_default));
        String orderBy = getContext().getString(R.string.pref_movie_order_default);
        if (Util.isNetworkAvailable(getContext())) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https").appendEncodedPath(getContext().getString(R.string.tmd_api_base_url))
                    .appendEncodedPath("discover/movie")
                    .appendQueryParameter("sort_by",orderBy)
                    .appendQueryParameter("api_key",BuildConfig.MOVIE_DB_API_KEY);
            String uristr = builder.build().toString();
            Log.d(LOG_TAG, "built url: " +uristr);
            String json = Util.connectUrlJson(uristr);
            if (TextUtils.isEmpty(json)){
                Toast.makeText(getContext(),R.string.err_no_response,Toast.LENGTH_SHORT).show();
                Log.w(LOG_TAG, "Response is empty!");
            } else { //delete and insert newer data
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat();
                    MovieItem[] mis = Util.getMovieDataFromJson(json);
                    Log.d(LOG_TAG, "onPostExecute: getContext().getContentResolver().delete");
                    getContext().getContentResolver().delete(MoviesContentProvider.Movies.CONTENT_URI,null,null);
                    ContentValues[] values = new ContentValues[mis.length];
                    for (int i = 0; i < mis.length; i++) {
                        ContentValues value = new ContentValues();
                        value.put(MovieColunms.OVERVIEW,mis[i].getOverview());
                        value.put(MovieColunms.POSTERURL,mis[i].getPosterUrl());
                        value.put(MovieColunms.TITLE,mis[i].getTitle());
                        value.put(MovieColunms.TMDID,mis[i].getTmdId());
                        value.put(MovieColunms.VOTEAVERAGE,mis[i].getVoteAverage());
                        value.put(MovieColunms.VOTECOUNT,mis[i].getVoteCount());
                        value.put(MovieColunms.RELEASEDATE,mis[i].getReleaseDate());
                        value.put(MovieColunms.UPDATED,sdf.format(new Date()));
                        values[i] = value;
                    }
                    Log.d(LOG_TAG, "onPostExecute: getContext().getContentResolver().bulkInsert");
                    getContext().getContentResolver().bulkInsert(MoviesContentProvider.Movies.CONTENT_URI,values);

                } catch (JSONException e) {
                    Log.e(LOG_TAG,"parse json fail!");
                }
            }
        } else {
            Toast.makeText(getContext(),R.string.err_network_not_available,Toast.LENGTH_SHORT).show();
            Log.w(LOG_TAG, "updateMovie list fail! The network is not available.");
        }
    }




    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Log.d(LOG_TAG, "syncImmediately...");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        POPMovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
       // ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}