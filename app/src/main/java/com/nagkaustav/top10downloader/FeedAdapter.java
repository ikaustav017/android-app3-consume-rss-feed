package com.nagkaustav.top10downloader;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ikaustav017 on 2/16/2017.
 */

public class FeedAdapter<T extends FeedEntry> extends ArrayAdapter {

    private static final String TAG = "FeedAdapter";
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<T> applications;

    public FeedAdapter(Context context, int resource, List<T> applications) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.applications = applications;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        //create a new view everytime getView method is called.. unncesseary overhead....should reuse previously created views
        // View view =layoutInflater.inflate(layoutResource,parent,false);

        //altenative reuse convertView which is reference to previously created views

        if (convertView == null) {
            Log.d(TAG, "getView: called with null convertView");
            convertView = layoutInflater.inflate(layoutResource, parent, false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            Log.d(TAG, "getView: provided with convertview");
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //findviewbyid is costly operation..but when you are reusing views, widgets haven't changed since last usage.
        // TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        //TextView tvArtist = (TextView) convertView.findViewById(R.id.tvArtist);
        //TextView tvSummary = (TextView) convertView.findViewById(R.id.tvSummary);


        T currentApp = applications.get(position);

        viewHolder.tvName.setText(currentApp.getName());
        viewHolder.tvArtist.setText(currentApp.getArtist());
        viewHolder.tvSummary.setText(currentApp.getSummary());

        return convertView;
    }


    //provide correct count of items in get count method
    @Override
    public int getCount() {
        return applications.size();
    }


    //optimization to prevent costly findbyid everythime we reuse Views
    private class ViewHolder {
        final TextView tvName;
        final TextView tvSummary;
        final TextView tvArtist;

        ViewHolder(View v) {
            this.tvArtist = (TextView) v.findViewById(R.id.tvArtist);
            this.tvSummary = (TextView) v.findViewById(R.id.tvSummary);
            this.tvName = (TextView) v.findViewById(R.id.tvName);

        }
    }
}
