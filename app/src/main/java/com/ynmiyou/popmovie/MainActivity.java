package com.ynmiyou.popmovie;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ynmiyou.popmovie.sync.POPMovieSyncAdapter;

public class MainActivity extends AppCompatActivity {

    private Fragment mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        POPMovieSyncAdapter.initializeSyncAdapter(this);
        /*if (savedInstanceState != null) {
            //Restore the fragment's instance
            mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
        }*/
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's instance
        /*if (mContent != null)
            getSupportFragmentManager().putFragment(outState, "mContent", mContent);*/
    }
}
