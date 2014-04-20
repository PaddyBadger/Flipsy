package com.flipsy.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.util.ArrayList;

/**
 * Created by patriciaestridge on 4/17/14.
 */
public class GalleryFragment extends Fragment {
    GridView mGridView;
    ArrayList<FlipsyObject> mItems;
    private static final String TAG = "GalleryFragment";
    AsyncTask<Void, Void, ArrayList<FlipsyObject>> fetchItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        fetchItems = new FetchItemsTask().execute();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        mGridView = (GridView)v.findViewById(R.id.gridView);
        setupAdapter();
        return v;
    }
    
    public void onPause() {
        super.onPause();
        fetchItems.cancel(true);
    }

    void setupAdapter() {
        if (getActivity() == null || mGridView == null) return;

        if (mItems != null) {
            mGridView.setAdapter(new ArrayAdapter<FlipsyObject>(getActivity(), android.R.layout.simple_gallery_item, mItems));
        } else {
            mGridView.setAdapter(null);
        }
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, ArrayList<FlipsyObject>> {
        @Override
        protected ArrayList<FlipsyObject> doInBackground(Void... params) {
            return new ApiFetcher().getItems();
        }

        @Override
        protected void onPostExecute(ArrayList<FlipsyObject> items) {
            mItems = items;
            setupAdapter();
        }
    }
}
