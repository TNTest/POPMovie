package com.ynmiyou.popmovie;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by TNT on 16/9/2.
 */
public class Util {

    /**
     * Confirm network is available at this moment
     * @param context activity context
     * @return true if network available, false otherwise
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected())
            {
                if (info.getState() == NetworkInfo.State.CONNECTED)
                {
                    // Available
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }
}
