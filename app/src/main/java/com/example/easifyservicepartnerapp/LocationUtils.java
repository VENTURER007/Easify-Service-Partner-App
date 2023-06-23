package com.example.easifyservicepartnerapp;

import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class LocationUtils {
    private static final String API_KEY = "AIzaSyAds3AXfOtH_54LE7GgVNkd82ez7llLVBc";

    public static String getLocationName(LatLngWrapper latLng) {
        try {
            String encodedLatLng = URLEncoder.encode(latLng.toString(), StandardCharsets.UTF_8.toString());
            String urlStr = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + encodedLatLng + "&key=" + API_KEY;

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new IOException("Failed to retrieve location data. HTTP error code: " + conn.getResponseCode());
            }

            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(in).getAsJsonObject();

            JsonArray results = jsonObject.getAsJsonArray("results");
            if (results.size() > 0) {
                JsonObject firstResult = results.get(0).getAsJsonObject();
                Log.e("addresses",firstResult.get("formatted_address").getAsString());
                return firstResult.get("formatted_address").getAsString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

