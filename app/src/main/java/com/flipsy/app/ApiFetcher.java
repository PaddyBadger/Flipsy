package com.flipsy.app;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by patriciaestridge on 4/17/14.
 */
public class ApiFetcher {

    private static final String API_KEY = "9b8cl8hiyrixx6w3v0r6jw1l";
    private static final String FIELDS = "listing_id,title,price,url,description";
    private static final String IMAGE = "MainImage";
    private static final String ENDPOINT = "https://openapi.etsy.com/v2/listings/active?api_key=";
    private static final String METHOD = "GET";
    private static final String LIMIT = "100";
    public static final String SEARCH_QUERY = "SearchQuery";
    private static final String TAG = "API FETCHER";

    byte[] getUrlBytes(String urlSpec) throws IOException {
        java.net.URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }

            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrl(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public ArrayList<FlipsyItem> downloadFlipsyItems(String url) {
        ArrayList<FlipsyItem> listings = new ArrayList<FlipsyItem>();
        try{
            String jsonString = getUrl(url);
            JSONObject parseJson = new JSONObject(jsonString);

            try {
                makeJSON(listings, parseJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException ioe) {
            Log.e(TAG, "FAIL", ioe);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return listings;
    }

    public ArrayList<FlipsyItem> getItems() {
        String url = Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("method", METHOD)
                .appendQueryParameter("fields",FIELDS)
                .appendQueryParameter("limit", LIMIT)
                .appendQueryParameter("includes",IMAGE)
                .build().toString();
        return downloadFlipsyItems(url);
    }

    public ArrayList<FlipsyItem> search(String query) {
        String url = Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("method", METHOD)
                .appendQueryParameter("fields",FIELDS)
                .appendQueryParameter("limit", LIMIT)
                .appendQueryParameter("includes",IMAGE)
                .appendQueryParameter("keywords", query)
                .build().toString();
        return downloadFlipsyItems(url);
    }

    void makeJSON(ArrayList<FlipsyItem> listings, JSONObject data) throws JSONException {
        JSONArray parseJson = data.getJSONArray("results");

        for (int i=0; i < parseJson.length(); i++) {
            JSONObject listing = parseJson.getJSONObject(i);
            long listing_id = listing.getLong("listing_id");
            String title = listing.getString("title");
            String listing_url = listing.getString("url");
            String price;
            if (listing.has("price")) {
                price = listing.getString("price");
            } else {
                price = "0.0";
            }
            String description = listing.getString("description");
            String image_url_array = listing.getString("MainImage");
            String image_url = getImageUrl(image_url_array);
            i++;

            FlipsyItem newListing = new FlipsyItem();
            newListing.setTitle(title);
            newListing.setUrl(listing_url);
            newListing.setListing_id(listing_id);
            newListing.setPrice(price);
            newListing.setImageUrl(image_url);
            newListing.setDescription(description);
            listings.add(newListing);
        }
    }

    private String  getImageUrl(String image_url_array) throws  JSONException {
            JSONObject parseImageJson = new JSONObject(image_url_array);
            String image_url = parseImageJson.getString("url_fullxfull");
            return image_url;
    }
}
