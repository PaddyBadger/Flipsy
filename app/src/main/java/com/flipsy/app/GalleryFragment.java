package com.flipsy.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

/**
 * Created by patriciaestridge on 4/17/14.
 */
public class GalleryFragment extends Fragment {
    GridView mGridView;
    ArrayList<FlipsyItem> mItems;
    private static final String TAG = "GalleryFragment";
    AsyncTask<Void, Void, ArrayList<FlipsyItem>> fetchItems;
    private static int NUM_CARDS;
    private ViewPagerAdapter mAdapter;
    private ViewPager mPager;
    private View v;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        fetchItems = new FetchItemsTask().execute();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_main, container, false);
        return v;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mPager = (ViewPager) getView().findViewById(R.id.pager);
        mPager.setPageTransformer(true, new PageTransformer());
        setupAdapter();
    }

    public void onPause() {
        super.onPause();
        fetchItems.cancel(true);
    }

    void setupAdapter() {

        if (getActivity() == null || mPager == null) return;

        if (mItems != null) {
            mPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager()));

        } else {
            mPager.setAdapter(null);
        }
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, ArrayList<FlipsyItem>> {
        @Override
        protected ArrayList<FlipsyItem> doInBackground(Void... params) {
            return new ApiFetcher().getItems();
        }

        @Override
        protected void onPostExecute(ArrayList<FlipsyItem> items) {
            mItems = items;
            setupAdapter();
        }
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.i("position", ""+position);
            return mItems.get(position);}

        @Override
        public int getCount() {
            NUM_CARDS = mItems.size();
            Log.i("NumCards", "" + NUM_CARDS);
            return NUM_CARDS; }
    }
}
