package com.app.pico;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

public class LocationListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    ArrayList<Location> locations;
    DBOperationsClass db;
    LocationArrayAdapter adapter;
    ListView locationListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);

        db = new DBOperationsClass(this);
        locations = db.getAllLocations();

        locationListView = (ListView)findViewById(R.id.list_locations);

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
        adapter = new LocationArrayAdapter(LocationListActivity.this, locations);
        locationListView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Location location = locations.get(i);
        Intent parentInt = this.getIntent();
        if (parentInt.getExtras().getString("parent").equals("alarm")){
            Log.e("parent", "alarm");
            Intent retInt = new Intent();
            retInt.putExtra("location", location.getLocationName());
            setResult(Activity.RESULT_OK, retInt);
            finish();
        } else {
            Log.e("parent", "not alarm");
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
}
