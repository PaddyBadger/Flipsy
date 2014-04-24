package com.flipsy.app;

/**
 * Created by patriciaestridge on 4/21/14.
 */
/**
 * Created by patriciaestridge on 4/19/14.
 */

public class FlipsyItem {
    long listing_id;
    String title;
    String url;
    String price;
    String imageUrl;
    String description;

    public String getTitle() {
        String itemTitle;
        if(title.length() > 50) {
            itemTitle = title.substring(0, 50);
        } else {
            itemTitle = title;
        }
        return itemTitle;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
