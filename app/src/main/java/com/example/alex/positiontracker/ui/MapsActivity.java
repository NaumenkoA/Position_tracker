package com.example.alex.positiontracker.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.alex.positiontracker.R;
import com.example.alex.positiontracker.database.LocationDataSource;
import com.example.alex.positiontracker.locationTracking.UserLocation;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private ArrayList<UserLocation> mUserLocations;
    private long mFromDate;
    private long mToDate;

    @BindView(R.id.sendResultsButton) Button mSendResultsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //Searching for positions over requested period
        mUserLocations = new ArrayList<>();
        LocationDataSource locationDataSource = new LocationDataSource(this);
        Intent intent = getIntent();
        mFromDate = intent.getLongExtra(MainActivity.USER_SELECTED_FROM_DATE, 0);
        mToDate = intent.getLongExtra(MainActivity.USER_SELECTED_TO_DATE, 0);
        if (mFromDate != 0 && mToDate != 0) {
            mUserLocations = locationDataSource.getLocationsForTimePeriod (mFromDate, mToDate);
        }
        mSendResultsButton.setEnabled(mUserLocations.size() > 0);
        mSendResultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setTitle("Enter your e-mail address");
                final EditText input = new EditText(MapsActivity.this);
                input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT);
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String message = createMessageWithUserLocations();
                        String userMail = input.getText().toString();
                        String subject = "My locations " + " from " + MainActivity.getStringFromUnixTime(mFromDate) + " to " + MainActivity.getStringFromUnixTime(mToDate);
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse ("mailto:" + userMail));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
                        startActivity(Intent.createChooser(emailIntent, "Choose app to send e-mail:"));
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
                }
        });
    }

    private String createMessageWithUserLocations() {
        StringBuilder stringBuilder = new StringBuilder();
        String time;
        String longitude;
        String latitude;
        String address;
        String newLine;
        if (mUserLocations.size() == 0) {
            return "No locations were recorded over the period" + " from " +
                    MainActivity.getStringFromUnixTime(mFromDate) + " to " + MainActivity.getStringFromUnixTime(mToDate);
        }
        stringBuilder.append("Locations recorded over the period" + " from ").append(MainActivity.getStringFromUnixTime(mFromDate)).append(" to ").append(MainActivity.getStringFromUnixTime(mToDate)).append("\n\n");

        for (UserLocation location: mUserLocations) {
            time = location.getFormattedTime();
            longitude = Double.toString(location.getLongitude());
            latitude = Double.toString(location.getLatitude());
            address = location.getAddress();
            newLine = time + ", long: " + longitude + " lat: " + latitude + ", address: " + address + "\n";
            stringBuilder.append(newLine);
        }
        return stringBuilder.toString();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mUserLocations.size() != 0) {
            LatLng latLng;
            int count = 0;
            for (UserLocation location:mUserLocations){
                count++;
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(latLng));
                if (count == mUserLocations.size()) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    CameraUpdate zoom=CameraUpdateFactory.zoomTo(10);
                    googleMap.animateCamera(zoom);
                }
            }
        } else {
            Toast.makeText(this, "No locations were found", Toast.LENGTH_SHORT).show();
         }
    }
}
