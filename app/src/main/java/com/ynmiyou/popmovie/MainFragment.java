package com.ynmiyou.popmovie;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainFragment extends Fragment {

    private static final String LOG_TAG = MainFragment.class.getSimpleName();
    public static final String DETAIL_MSG = "detail";
    private MovieGridViewAdapter mgvapr;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        Log.d(LOG_TAG,"onCreateView...");
        List<MovieItem> data = new ArrayList<>();
        /*MovieItem movie = new MovieItem("http://i.imgur.com/DvpvklR.png","Jason Bourne 5");
        data.add(movie);*/
        mgvapr = new MovieGridViewAdapter(getContext(),
                R.layout.grid_item_layout,data);
        GridView lv = (GridView)view.findViewById(R.id.gridView);
        lv.setAdapter(mgvapr);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(getContext(),adapterView.getAdapter().getItem(i).toString(),
//                        Toast.LENGTH_SHORT).show();
                Intent startDetailIntent = new Intent(getContext(), DetailActivity.class);
                //using parcelble obj to transfer data
                Parcelable obj = (Parcelable)adapterView.getAdapter().getItem(i);
                startDetailIntent.putExtra(DETAIL_MSG, obj);
                startActivity(startDetailIntent);

            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.mainfragment,menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent startSettingsIntent = new Intent(getContext(), SettingsActivity.class);
                startActivity(startSettingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    @Override
    public void onStart() {
        super.onStart();
        updateMovie();
    }

    private void updateMovie() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //Log.d(LOG_TAG,"location key:" + getString(R.string.pref_location_key));
        String orderBy = sharedPref.getString(getString(R.string.pref_movie_order_key),
                getString(R.string.pref_movie_order_default));
        if (Util.isNetworkAvailable(getContext())) {
            new FetchMovieTask().execute(orderBy); //prefs[0]
        } else {
            Toast.makeText(getContext(),R.string.err_network_not_available,Toast.LENGTH_SHORT).show();
            Log.w(LOG_TAG, "updateMovie list fail! The network is not available.");
        }
    }

    private class FetchMovieTask extends AsyncTask<String, Void, MovieItem[]> {
        /** The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute()
         * prefs[0] = order by
         * */
        protected MovieItem[] doInBackground(String... prefs) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https").appendEncodedPath("/api.themoviedb.org/3/discover/movie")
                    .appendQueryParameter("sort_by",prefs[0])
                    .appendQueryParameter("api_key",BuildConfig.MOVIE_DB_API_KEY);
            String uristr = builder.build().toString();
            Log.d(FetchMovieTask.class.getSimpleName(), "built url: " +uristr);
            String json = connectUrlJson(uristr);
            if (Util.isEmpty(json)){
                Toast.makeText(getContext(),R.string.err_no_response,Toast.LENGTH_SHORT).show();
                Log.w(LOG_TAG, "Response is empty!");
                return null;
            } else {
                try {
                    return getMovieDataFromJson(json);
                } catch (JSONException e) {
                    Log.e(LOG_TAG,"parse json fail!");
                    return null;
                }
            }

        }

        protected void onPostExecute(MovieItem[] result) {
            //Log.d(LOG_TAG, Arrays.toString(result));
            if (mgvapr != null) {
                mgvapr.clear();
                mgvapr.addAll(result);
            }
            //update share intent, using first item
            /*Intent intent = new Intent(Intent.ACTION_SEND);
            Uri geoLocation = Uri.parse(result[0] + "#SunshineApp").buildUpon().build();
            intent.setData(geoLocation);
            mShareActionProvider.setShareIntent(intent);*/
        }
    }

    private MovieItem[] getMovieDataFromJson(String movieJsonStr)
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

    private String genFullPosterPath(String relativePath) {
        StringBuilder sb = new StringBuilder("http://image.tmdb.org/t/p/w185");
        sb.append(relativePath);
        return sb.toString();
    }

}
