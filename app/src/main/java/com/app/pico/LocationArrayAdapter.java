package com.app.pico;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Rachel on 5/1/17.
 */

public class LocationArrayAdapter extends ArrayAdapter<Location> {

    private static final int ROW_LAYOUT_ID = R.layout.listview_location_item_row;

    Context context;
    List<Location> locationList;

    public LocationArrayAdapter(Context context, List<Location> locationList) {
        super(context, ROW_LAYOUT_ID, locationList);
        this.context = context;
        this.locationList = locationList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        LocationHolder locationholder = null;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(ROW_LAYOUT_ID, parent, false);

        locationholder = new LocationHolder();
        locationholder.location = locationList.get(position);
        locationholder.name = (TextView)row.findViewById(R.id.locationNameView);
        row.setTag(locationholder);

        setupItem(locationholder);
        return row;
    }

    private void setupItem(LocationHolder locationHolder)
    {
        locationHolder.name.setText(locationHolder.location.getLocationName());
    }

    public static class LocationHolder
    {
        Location location;
        TextView name;
    }
}
