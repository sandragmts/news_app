package com.example.android.news_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NewsAdapter extends ArrayAdapter<NewsItem> {

    /**
     * constructors a new ArrayAdapter
     */
    public NewsAdapter(Context context, List<NewsItem> news) {
        super(context, 0, news);
    }

    /**
     * getView returns the news information fitting position of the views in news_item_layout
     */
    public View getView(int position, View convertView, ViewGroup parent) {


        /**
         * we must check to see if there is an existing view we can reuse
         * if the view is null we must inflate a new list item layout
         */
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_item_layout, parent, false);
        }

        NewsItem currentNews = getItem(position);

        //find the news title with the ID news_type
        TextView typeView = listItemView.findViewById(R.id.news_type);
        typeView.setText(currentNews.getmTitle());


        //find the news title with the ID news_type
        TextView titleView = listItemView.findViewById(R.id.newsTitle);
        titleView.setText(currentNews.getHeader());

        //find news section with iD author
        TextView newsAuthor = listItemView.findViewById(R.id.author);
        // if current news has an name: SHOW
        if (currentNews.getmAuthor() != "") {
            newsAuthor.setText(currentNews.getmAuthor());

            newsAuthor.setVisibility(View.VISIBLE);
            // if not do not show
        } else {
            newsAuthor.setVisibility(View.GONE);
        }

        // Create a new Date object from the time in milliseconds of the published
        Date dateObject = new Date(String.valueOf(currentNews.getmTimeAndDate()));

        // Find the TextView with view ID date
        TextView dateView = (TextView) listItemView.findViewById(R.id.date);
        // Format the date string (i.e. "June 1, 2018")
        String formattedDate = formatDate(dateObject);
        // Display the date of the current news in that TextView
        dateView.setText(formattedDate);

        // Find the TextView with view ID time
        TextView timeView = (TextView) listItemView.findViewById(R.id.time);
        // Format the time string (i.e. "10:50PM")
        String formattedTime = formatTime(dateObject);
        // Display the time of the current news in that TextView
        timeView.setText(formattedTime);

        return listItemView;

    }

    /**
     * Return the formatted date string (i.e. "June 1, 2018") from a Date object.
     */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    /**
     * Return the formatted date string (i.e. "10:50PM") from a Date object.
     */
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }
}
