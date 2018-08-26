package com.rreay.top10downloader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Will need to store layout resource that will be given in the constructor and the list that contains our data
 * Will also inflate our XML resource. Means to take the XML representation and produce the actual widgets from it.
 * XML in list_record describes the textView widgets and their constraints, font sizes, etc. A layout inflater
 * then takes that XML and inflates it to produce the actual view object that can be displayed on the screen
 **/

public class FeedAdapter extends ArrayAdapter {
    private static final String TAG = "FeedAdapter";
    private final int layoutResource;
    private final LayoutInflater layoutInflater; // https://developer.android.com/reference/android/view/LayoutInflater.html
    private List<FeedEntry> applications;

    public FeedAdapter(@NonNull Context context, int resource, List<FeedEntry> applications) {
        super(context, resource);
        this.layoutResource = resource; // "this" refers to the current instance of the class. Referring ot itself.
        this.layoutInflater = LayoutInflater.from(context); // https://developer.android.com/reference/android/content/Context.html
        this.applications = applications;
    }

    /**
     * We have a class that extends the ArrayAdapter class and to make it work we need to override two methods of the base class.
     * when the listView scrolls items off screen, it will ask it's adapter for a new view to display. Does this by calling the getView method, which is
     * first method we need to override. We will then override the getCount method.
     **/

    //TODO create Override methods for getView and getCount.
    @Override
    public int getCount() {
        return applications.size();
    }

    // getView calls the listView every time it wants an item it's going to display
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        /** This will set it so we reuse views and saves memory. It will only create a new view if it is null.
         We are only creating a new view if a view is null and then we are given another view to use**/
        if (convertView == null) {
            convertView = layoutInflater.inflate(layoutResource, parent, false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
//        TextView tvName = convertView.findViewById(R.id.tvName);
//        TextView tvArtist = convertView.findViewById(R.id.tvArtist);
//        TextView tvSummary = convertView.findViewById(R.id.tvSummary);


        FeedEntry currentApp = applications.get(position);

        viewHolder.tvName.setText(currentApp.getName());
        viewHolder.tvArtist.setText(currentApp.getArtist());
        viewHolder.tvSummary.setText(currentApp.getSummary());

        return convertView;
    }

    /**
     * ViewHolder Pattern can be used to increase the speed at which a ListView renders data
     **/
    private class ViewHolder {
        final TextView tvName;
        final TextView tvArtist;
        final TextView tvSummary;

        ViewHolder(View v) {
            this.tvName = v.findViewById(R.id.tvName);
            this.tvArtist = v.findViewById(R.id.tvArtist);
            this.tvSummary = v.findViewById(R.id.tvSummary);

        }
    }

}
