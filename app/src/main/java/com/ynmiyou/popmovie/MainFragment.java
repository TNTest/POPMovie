package com.ynmiyou.popmovie;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.ynmiyou.popmovie.data.MovieColunms;
import com.ynmiyou.popmovie.data.MoviesContentProvider;
import com.ynmiyou.popmovie.sync.POPMovieSyncAdapter;

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MainFragment.class.getSimpleName();
    public static final String DETAIL_MSG = "detail";
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private static final int MOVIEINFO_LOADER = 1;
    private static final String SELECTED_KEY = "GRID_POSITION";
    private MovieListAdapter mgvapr;
    private boolean mTwoPane;
    private int mPosition = GridView.INVALID_POSITION;
    private GridView mView;

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
        mgvapr = new MovieListAdapter(getContext(),null,0);
        GridView lv = (GridView)view.findViewById(R.id.gridView);
        mView = lv;
        lv.setAdapter(mgvapr);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                Toast.makeText(getContext(),adapterView.getAdapter().getItem(i).toString(),
//                        Toast.LENGTH_SHORT).show();
                Intent startDetailIntent = new Intent(getContext(), DetailActivity.class);
                //using parcelble obj to transfer data
                //Parcelable obj = (Parcelable)adapterView.getAdapter().getItem(i);
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                MovieItem mi = new MovieItem();
                mi.setOverview(cursor.getString(cursor.getColumnIndex(MovieColunms.OVERVIEW)));
                mi.setPosterUrl(cursor.getString(cursor.getColumnIndex(MovieColunms.POSTERURL)));
                mi.setTitle(cursor.getString(cursor.getColumnIndex(MovieColunms.TITLE)));
                mi.setTmdId(cursor.getString(cursor.getColumnIndex(MovieColunms.TMDID)));
                mi.setVoteAverage(cursor.getString(cursor.getColumnIndex(MovieColunms.VOTEAVERAGE)));
                mi.setVoteCount(cursor.getString(cursor.getColumnIndex(MovieColunms.VOTECOUNT)));
                mi.setReleaseDate(cursor.getString(cursor.getColumnIndex(MovieColunms.RELEASEDATE)));
                Log.d(LOG_TAG,"onClick...");
                if (mTwoPane) {
                    // In two-pane mode, show the detail view in this activity by
                    // adding or replacing the detail fragment using a
                    // fragment transaction.
//                    Bundle args = new Bundle();
//                    args.putParcelable(DETAIL_MSG, mi);

                    DetailFragment fragment = new DetailFragment();
                    getActivity().getIntent().putExtra(DETAIL_MSG, mi);

                    getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                        .commit();
                } else {
                    startDetailIntent.putExtra(DETAIL_MSG, mi);
                    startActivity(startDetailIntent);
                }
                mPosition = position;
                Log.d(LOG_TAG,"onItemClick mPosition: " + mPosition);
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIEINFO_LOADER, null, this);
        if (getActivity().findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            /*if (savedInstanceState == null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }*/
        } else {
            mTwoPane = false;
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
            Log.d(LOG_TAG, "onActivityCreated savedInstanceState contains key: " + SELECTED_KEY);
        }
        Log.d(LOG_TAG, "onActivityCreated mPosition: " + mPosition);
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
            case R.id.action_refresh:
                updateMovieInfo();
                getLoaderManager().restartLoader(MOVIEINFO_LOADER, null, this);//refresh view by loader
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().restartLoader(MOVIEINFO_LOADER, null, this);//refresh view by loader
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
            Log.d(LOG_TAG,"onSaveInstanceState saving mPosition. ");
        }
        Log.d(LOG_TAG,"onSaveInstanceState mPosition: " + mPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // To only show current and future dates, filter the query to return weather only for
        // dates after or including today.

        Uri movieContentUri = null;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String orderByPref = sharedPref.getString(getString(R.string.pref_movie_order_key),getString(R.string.pref_movie_order_default));
        String[] orders = getResources().getStringArray(R.array.pref_movie_order_values);
        if (orders[0].equals(orderByPref)) {// pref is order by vote average
            movieContentUri = MoviesContentProvider.Movies.moviesv;
        } else if(orders[2].equals(orderByPref)) {//favorites
            movieContentUri = MoviesContentProvider.Movies.moviesf;
        }else {
            movieContentUri = MoviesContentProvider.Movies.CONTENT_URI;
        }

        return new CursorLoader(getActivity(),
                movieContentUri,
                MovieListAdapter.PROJECTION,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(LOG_TAG,"onLoadFinished...");
        mgvapr.swapCursor(data);
        Log.d(LOG_TAG,"onLoadFinish mPosition: " + mPosition);
        if (mPosition != ListView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mgvapr.swapCursor(null);
    }

    private void updateMovieInfo() {
        POPMovieSyncAdapter.syncImmediately(getActivity());
    }


}
