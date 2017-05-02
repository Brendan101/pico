package com.app.pico;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;


public class PlacesActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private AutoCompleteTextView placesSelect;
    private GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private DBOperationsClass db;
    private Location currSelectedLoc;
    private Location savedLoc;
    private StyledButton btnSave;
    private Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        placesSelect = (AutoCompleteTextView) findViewById(R.id.editPlacesSelect);
        placesSelect.setThreshold(3);
        placesSelect.setOnItemClickListener(mAutocompleteClickListener);
        place = null;
        currSelectedLoc = new Location();

        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, null,
                null);
        placesSelect.setAdapter(mAdapter);

        btnSave = (StyledButton) findViewById(R.id.btnSubmitDestination);

        db = new DBOperationsClass(this);
        Bundle intentData = getIntent().getExtras();

        if(intentData != null) {
            savedLoc = (Location) intentData.getSerializable("location");
            // Update values in the layout to the actual Alarm values
            //alarmHeader.setText("Edit Alarm");
            //locID = currSelectedLoc.getID();
        }
        btnSave.setOnClickListener(new SaveListener(savedLoc, currSelectedLoc));

    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            // Unfocus/remove keyboard after clicking on autocomplete option
            InputMethodManager imm = (InputMethodManager) PlacesActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            place = places.get(0);


            LatLng latLng = place.getLatLng();
            currSelectedLoc.setLatitude((float) latLng.latitude);
            currSelectedLoc.setLongitude((float) latLng.longitude);
            currSelectedLoc.setLocationName(place.getName().toString());

            // TODO this is the selected place

            places.release();
        }
    };

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    private class SaveListener implements View.OnClickListener{
        private Location _currLocation;
        private Location _prevLocation;

        public SaveListener(Location prevLoc, Location currLoc){
            this._prevLocation = prevLoc;
            this._currLocation = currLoc;
        }

        @Override
        public void onClick(View v) {
            if (this._currLocation.getLocationName() == null){
                Toast.makeText(getApplicationContext(), "You must enter a location, or hit back to cancel!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (this._prevLocation != null){
                this._prevLocation.setLongitude(this._currLocation.getLongitude());
                this._prevLocation.setLatitude(this._currLocation.getLatitude());
                this._prevLocation.setLocationName(this._currLocation.getLocationName());
                db.updateLocation(this._prevLocation);
                finish();
            } else {
                db.addLocation(this._currLocation);
                finish();
            }

        }
    }
}
