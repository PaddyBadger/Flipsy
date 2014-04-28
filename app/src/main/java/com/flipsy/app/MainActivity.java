package com.flipsy.app;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;


public class MainActivity extends Activity {

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private int NUM_CARDS = 50;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageTransformer(true, new PageTransformer());
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mPager.setCurrentItem(mPager.getCurrentItem()-1);
        }
    }

    public void onNewIntent(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Log.i("Does a new intent ever get called", " " + intent);
        GalleryFragment fragment = (GalleryFragment) getFragmentManager().findFragmentById(R.id.pager);
        Log.i("Do I get this far?", ""+intent);

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.i("Search", "Is starting to take shape " + query);

            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putString(ApiFetcher.SEARCH_QUERY, query)
                    .commit();
        }
        mPagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());
        fragment.updateItems();
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) { return GalleryFragment.create(position);}

        @Override
        public int getCount() { return NUM_CARDS; }
    }
}

