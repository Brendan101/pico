package com.app.pico;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class LocationListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    ArrayList<Location> locations;
    DBOperationsClass db;
    LocationArrayAdapter adapter;
    ListView locationListView;
    String parentIntName;
    String locationType;

    StyledTextHeader pageHeader;

    Handler myHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);

        Intent parentInt = this.getIntent();
        parentIntName = parentInt.getExtras().getString("parent");
        locationType = parentInt.getExtras().getString("type");

        db = new DBOperationsClass(this);

        locationListView = (ListView)findViewById(R.id.list_locations);
        pageHeader = (StyledTextHeader)findViewById(R.id.locationsHeader);

        // Add "Add Location" footer to the list view
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup footer = (ViewGroup) inflater.inflate(R.layout.listview_location_footer, locationListView, false);
        locationListView.addFooterView(footer, null, true);

        ImageButton newLocationBtn = (ImageButton) footer.findViewById(R.id.addLocationBtn);
        newLocationBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Route to the Places page
                Intent placesInt = new Intent(LocationListActivity.this, PlacesActivity.class);
                startActivity(placesInt);
            }
        });

        locationListView.setOnItemClickListener(this);
        locationListView.setOnItemLongClickListener(this);

        if (parentIntName.equals("alarm")){
            myHandler.post(new TextViewRunnable(getString(R.string.page_location_choose_header), pageHeader));
        }


        populateLocationList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update the list
        populateLocationList();
    }

    private void populateLocationList() {
        locations = db.getAllLocations();
        /*if (parentIntName.equals("alarm") && locationType.equals("start")){
            //TODO temp to simulate current loc loaded from db
            Location tmpDefault = new Location();
            tmpDefault.setID(-1);
            tmpDefault.setLatitude(0);
            tmpDefault.setLongitude(0);
            tmpDefault.setLocationName("Current Location");
            locations.add(0, tmpDefault);
        } else if (parentIntName.equals("alarm") && locationType.equals("end")){
            //TODO temp to simulate default loc loaded from db
            Location tmpDefault = new Location();
            tmpDefault.setID(-2);
            tmpDefault.setLatitude(0);
            tmpDefault.setLongitude(0);
            tmpDefault.setLocationName("Default Location");
            locations.add(0, tmpDefault);
        }*/
        adapter = new LocationArrayAdapter(LocationListActivity.this, locations);
        locationListView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Location location = locations.get(i);
        if (parentIntName.equals("alarm")){
            Intent retInt = new Intent();
            retInt.putExtra("location", location);
            setResult(Activity.RESULT_OK, retInt);
            finish();
        } else {
            Intent placesInt = new Intent(LocationListActivity.this, PlacesActivity.class);
            placesInt.putExtra("location", location);
            startActivity(placesInt);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        final Location location = locations.get(i);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Confirmation!");
        alert.setMessage("Are you sure you want to delete " + location.getLocationName() + " ?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.deleteLocation(location);
                adapter.remove(location);
                adapter.notifyDataSetChanged();
                dialog.dismiss();

            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();

        return true;
    }

    private class TextViewRunnable implements Runnable {
        TextView _textView;
        String _value;

        public TextViewRunnable(String value, TextView textView){
            this._textView = textView;
            this._value = value;
        }

        @Override
        public void run() {
            this._textView.setText(this._value);
        }
    }
}
