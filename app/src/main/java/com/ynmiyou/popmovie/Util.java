package com.ynmiyou.popmovie;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by TNT on 16/9/2.
 */
public class Util {

    public static final String TMDB_IMG_W185_URL = "http://image.tmdb.org/t/p/w185";
    public static final String YOUTUBE_WATCH_URL_PREFIX = "https://www.youtube.com/watch?v=";
    public static final String INVALID_STR = "N/A";

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

    public static String connectUrlJson(String urlstr) {
        // These two need to be declared outside the try/catch
// so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

// Will contain the raw JSON response as a string.
        String responseJsonStr = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are available at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            URL url = new URL(urlstr);

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                responseJsonStr = null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                responseJsonStr = null;
            }
            responseJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            // If the code didn't successfully get the movie data, there's no point in attempting
            // to parse it.
            responseJsonStr = null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }
        return responseJsonStr;
    }

    public static MovieItem[] getMovieDataFromJson(String movieJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String TMD_LIST = "results";
        final String TMD_ID = "id";
        final String TMD_TITILE = "title";
        final String TMD_VOTE_AVG = "vote_average";
        final String TMD_VOTE_COUNT = "vote_count";
        final String TMD_POSTER_PATH = "poster_path";
        final String TMD_RELEASE_DATE = "release_date";
        final String TMD_OVERVIEW = "overview";

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(TMD_LIST);

        // OWM returns daily forecasts based upon the local time of the city that is being
        // asked for, which means that we need to know the GMT offset to translate this data
        // properly.

        // Since this data is also sent in-order and the first day is always the
        // current day, we're going to take advantage of that to get a nice
        // normalized UTC date for all of our weather.

        MovieItem[] resultMovies = new MovieItem[movieArray.length()];
        for(int i = 0; i < movieArray.length(); i++) {
            MovieItem mi = new MovieItem();
            // Get the JSON object representing the day
            JSONObject movieInfo = movieArray.getJSONObject(i);
            // description is in a child array called "weather", which is 1 element long.
            mi.setTmdId(movieInfo.getString(TMD_ID));
            mi.setTitle(movieInfo.getString(TMD_TITILE));
            mi.setVoteAverage(movieInfo.getString(TMD_VOTE_AVG));
            mi.setVoteCount(movieInfo.getString(TMD_VOTE_COUNT));
            mi.setPosterUrl(genFullPosterPath(movieInfo.getString(TMD_POSTER_PATH)));
            mi.setReleaseDate(movieInfo.getString(TMD_RELEASE_DATE));
            mi.setOverview(movieInfo.getString(TMD_OVERVIEW));
            resultMovies[i] = mi;
        }
        //Log.v(LOG_TAG, "Movies: " + Arrays.toString(resultMovies));
        return resultMovies;
    }

    public static ReviewItem[] getReviewDataFromJson(String reviewJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String TMD_LIST = "results";
        final String TMD_ID = "id";
        final String TMD_AUTHOR = "author";
        final String TMD_CONTENT= "content";
        final String TMD_URL = "url";

        JSONObject reviewJson = new JSONObject(reviewJsonStr);
        JSONArray reviewArray = reviewJson.getJSONArray(TMD_LIST);

        ReviewItem[] resultReviews = new ReviewItem[reviewArray.length()];
        for(int i = 0; i < reviewArray.length(); i++) {
            ReviewItem mi = new ReviewItem();
            JSONObject reviewInfo = reviewArray.getJSONObject(i);
            mi.setTmdId(reviewInfo.getString(TMD_ID));
            mi.setAuthor(reviewInfo.getString(TMD_AUTHOR));
            mi.setContent(reviewInfo.getString(TMD_CONTENT));
            mi.setUrl(reviewInfo.getString(TMD_URL));
            resultReviews[i] = mi;
        }
        return resultReviews;
    }

    public static VideoItem[] getVideoDataFromJson(String videoJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String TMD_LIST = "results";
        final String TMD_ID = "id";
        final String TMD_KEY = "key";
        final String TMD_NAME= "name";
        final String TMD_TYPE = "type";
        final String TMD_SITE = "site";

        JSONObject videoJson = new JSONObject(videoJsonStr);
        JSONArray videoArray = videoJson.getJSONArray(TMD_LIST);

        VideoItem[] resultVideos = new VideoItem[videoArray.length()];
        for(int i = 0; i < videoArray.length(); i++) {
            VideoItem mi = new VideoItem();
            JSONObject videoInfo = videoArray.getJSONObject(i);
            mi.setTmdId(videoInfo.getString(TMD_ID));
            mi.setKey(videoInfo.getString(TMD_KEY));
            mi.setName(videoInfo.getString(TMD_NAME));
            mi.setSite(videoInfo.getString(TMD_SITE));
            mi.setType(videoInfo.getString(TMD_TYPE));
            resultVideos[i] = mi;
        }
        return resultVideos;
    }

    public static String genFullPosterPath(String relativePath) {
        StringBuilder sb = new StringBuilder(TMDB_IMG_W185_URL);
        sb.append(relativePath);
        return sb.toString();
    }

    public static String getYoutubeUrlByKey(String key) {
        if (TextUtils.isEmpty(key))
            return "";
        return YOUTUBE_WATCH_URL_PREFIX + key;
    }
}
