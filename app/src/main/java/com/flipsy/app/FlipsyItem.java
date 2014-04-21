package com.flipsy.app;

/**
 * Created by patriciaestridge on 4/21/14.
 */
import android.support.v4.app.Fragment;

/**
 * Created by patriciaestridge on 4/19/14.
 */

public class FlipsyItem extends Fragment {
    long listing_id;
    String title;
    String url;
    String price;
    String imageUrl;

    public String toString() {
        String listing = title + price;
        return listing;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getListing_idId() {
        return listing_id;
    }

    public void setListing_id(long listing_id) { this.listing_id = listing_id; }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
