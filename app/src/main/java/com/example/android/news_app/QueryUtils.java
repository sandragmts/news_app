package com.example.android.news_app;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QueryUtils {

    public static final int MIN_MILLISECONDS = 10000;
    public static final int MAX_MILLISECONDS = 15000;

    private static final String RESPONSE = "response";
    private static final String RESULTS = "results";

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create private constructor. No one must create an onject from this class.
     * This Class are only helper methods that hold static variables and methods
     */
    private QueryUtils() {
    }

    /**
     * Query the The Guardian dataset and return a list of {@link NewsItem} objects.
     */
    public static List<NewsItem> fetchNewsData(String requestUrl) {
        // Create URL object
        URL url = createURL(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Earthquake}s
        List<NewsItem> articles = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Earthquake}s
        return articles;
    }

    /**
     * returns a URL Object from the given String URL
     */
    private static URL createURL(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(MIN_MILLISECONDS /* milliseconds */);
            urlConnection.setConnectTimeout(MAX_MILLISECONDS /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == urlConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the Gaurdian JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<NewsItem> extractFeatureFromJson(String newsJSON) {

        // if the JSON string is empty return early
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news to
        List<NewsItem> newsList = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of results (or news).
            JSONArray newsArray = baseJsonResponse.getJSONObject(RESPONSE).getJSONArray(RESULTS);

            // For each news in the newsArray, create an {@link News} object
            for (int i = 0; i < newsArray.length(); i++) {

                // Get a single news at position i within the list of news
                JSONObject currentNews = newsArray.getJSONObject(i);

                // Extract the value for the key called "webTitle"
                String title = currentNews.getString("webTitle");

                // Extract the value for the key called "sectionName"
                String sectionName = currentNews.getString("sectionName");

                //Extract the value of the key called fields
                String authorFullName = currentNews.getJSONObject("fields").getString("byline");

                // Extract the value for the key called "webPublicationDate"
                String originalPublicationDate = currentNews.getString("webPublicationDate");

                //Format publication date
                Date publicationDate = null;
                try {
                    publicationDate = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")).parse(originalPublicationDate);
                } catch (Exception e) {
                    // If an error is thrown when executing the above statement in the "try" block,
                    // catch the exception here, so the app doesn't crash. Print a log message
                    // with the message from the exception.
                    Log.e("QueryUtils", "Problem parsing the news date", e);
                }

                // Extract the value for the key called "url"
                String url = currentNews.getString("webUrl");

                // Create a new {@link News} object with the title, section name, publication date,
                // and url from the JSON response.
                NewsItem news = new NewsItem(title, sectionName, authorFullName, publicationDate, url);

                newsList.add(news);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);
        }

        return newsList;
    }

}
