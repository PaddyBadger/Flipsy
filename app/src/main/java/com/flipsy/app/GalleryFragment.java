package com.flipsy.app;

import android.app.Activity;
import android.app.Fragment;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by patriciaestridge on 4/17/14.
 */
public class GalleryFragment extends Fragment {
    ArrayList<FlipsyItem> mItems;
    AsyncTask<Void, Void, ArrayList<FlipsyItem>> fetchItems;
    private View v;
    public static final String ARG_PAGE = "page";
    private int mPageNumber;
    ItemThumbnailDownloader<ImageView> mThumbnailThread;


    public static GalleryFragment create(int pageNumber) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);

        setHasOptionsMenu(true);
        setRetainInstance(true);
        updateItems();

        mThumbnailThread = new ItemThumbnailDownloader<ImageView>(new Handler());
        mThumbnailThread.setListener(new ItemThumbnailDownloader.Listener<ImageView>() {
            public void onThumbnailDownloaded(ImageView imageView, Bitmap thumbnail) {
                if (isVisible()) {
                    imageView.setImageBitmap(thumbnail);
                }
            }
        });
        mThumbnailThread.start();
        mThumbnailThread.getLooper();
    }

    public void updateItems() {
        fetchItems = new FetchItemsTask().execute();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.gallery_fragment, container, false);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        SearchView searchView = (SearchView)searchItem.getActionView();

        SearchManager searchManager = (SearchManager)getActivity()
                .getSystemService(Context.SEARCH_SERVICE);
        ComponentName name = getActivity().getComponentName();
        SearchableInfo searchInfo = searchManager.getSearchableInfo(name);

        searchView.setSearchableInfo(searchInfo);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_search:
                getActivity().onSearchRequested();
                return true;
            case R.id.menu_item_clear:
                PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .edit()
                        .putString(ApiFetcher.SEARCH_QUERY, null)
                        .commit();
                updateItems();
                return true;
            default:
            return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailThread.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailThread.quit();
    }

    public void onPause() {
        super.onPause();
        fetchItems.cancel(true);
    }

    void setupAdapter() {

        if (getActivity() == null) return;

        if (mItems != null) {

            FlipsyItem item = mItems.get(mPageNumber);

            TextView titleTextView = (TextView) v.findViewById(R.id.title);
            String title = item.getTitle();
            titleTextView.setText(title);

            ImageView imageView = (ImageView)v.findViewById(R.id.item_imageView);
            mThumbnailThread.queueThumbnail(imageView, item.getImageUrl());

            TextView priceTextView = (TextView) v.findViewById(R.id.price);
            String price = item.getPrice();
            priceTextView.setText(price);

            TextView descriptionTextView = (TextView) v.findViewById(R.id.description);
            String description = item.getDescription();
            descriptionTextView.setText(description);
        }
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, ArrayList<FlipsyItem>> {
        @Override
        protected ArrayList<FlipsyItem> doInBackground(Void... params) {

            Activity activity = getActivity();
            if (activity == null)
                return new ArrayList<FlipsyItem>();

            String query = PreferenceManager.getDefaultSharedPreferences(activity)
                    .getString(ApiFetcher.SEARCH_QUERY, null);
            if (query != null) {
                Log.i("do I, query search", "ever get called" + query);
                return new ApiFetcher().search(query);
            } else {
                return new ApiFetcher().getItems();
            }
        }

        @Override
        protected void onPostExecute(ArrayList<FlipsyItem> items) {
            mItems = items;
            setupAdapter();
        }
    }
}
