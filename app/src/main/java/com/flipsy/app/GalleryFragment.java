package com.flipsy.app;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
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
            TextView titleTextView = (TextView) v.findViewById(R.id.title);
            titleTextView.setText(mItems.get(mPageNumber).toString());

            ImageView imageView = (ImageView)v.findViewById(R.id.item_imageView);
            imageView.setImageResource(R.drawable.rio);
            FlipsyItem item = mItems.get(mPageNumber);
            mThumbnailThread.queueThumbnail(imageView, item.getImageUrl());
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
}
