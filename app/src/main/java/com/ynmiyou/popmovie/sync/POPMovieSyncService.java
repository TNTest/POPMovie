package com.ynmiyou.popmovie.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class POPMovieSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static POPMovieSyncAdapter sPOPMovieSyncAdapter = null;
    private static final String LOG_TAG = "POPMovieSyncService";

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "onCreate - POPMovieSyncService");
        synchronized (sSyncAdapterLock) {
            if (sPOPMovieSyncAdapter == null) {
                sPOPMovieSyncAdapter = new POPMovieSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind - POPMovieSyncService");
        return sPOPMovieSyncAdapter.getSyncAdapterBinder();
    }
}