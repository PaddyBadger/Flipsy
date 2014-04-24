package com.flipsy.app;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by patriciaestridge on 4/17/14.
 */
public class GalleryFragment extends Fragment {
    ArrayList<FlipsyItem> mItems;
    private static final String TAG = "GalleryFragment";
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
        fetchItems = new FetchItemsTask().execute();

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

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.gallery_fragment, container, false);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_search:
                getActivity().onSearchRequested();
                return true;
            case R.id.menu_item_clear:
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
            String query = "bags";

            if (query != null) {
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
