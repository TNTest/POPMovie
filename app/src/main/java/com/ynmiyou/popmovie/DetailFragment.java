package com.ynmiyou.popmovie;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class DetailFragment extends Fragment {


    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        MovieItem mi = (MovieItem)getActivity().getIntent().getParcelableExtra(MainFragment.DETAIL_MSG);
        Log.d(LOG_TAG, mi.toString());
        ((TextView)view.findViewById(R.id.detail_title)).setText(mi.getTitle());
        ((TextView)view.findViewById(R.id.detail_overview)).setText(mi.getOverview());
        ((TextView)view.findViewById(R.id.detail_release_date)).setText(mi.getReleaseDate());
        String voteStr = mi.getVoteAverage()+"/"+mi.getVoteCount();
        ((TextView)view.findViewById(R.id.detail_vote)).setText(voteStr);
        Picasso.with(getContext())
            .load(mi.getPosterUrl())
            //.placeholder(R.drawable.launcher)
            //.error(R.drawable.launcher)
            .into((ImageView) view.findViewById(R.id.detail_poster));
        return view;
    }

}
