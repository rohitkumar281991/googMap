package com.contacts.rohit.sidemenu;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class SideMenuActivity extends AppCompatActivity
        implements LocationListener,NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        PlaceSelectionListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
         {
    private GoogleMap mMap;
    Context context;
    GoogleApiClient mGoogleApiClient;
    final float ZOOM_LEVEL = 12;
    public static LatLng myLatLong = null;
    public Location mLastLocation;
    public Location mCurrentLocation;
    private double mLastLatitude;
    private double mLastLongitude;
    final int mMY_PERMISSIONS_REQUEST = 1;
    public boolean request_granted=false;
    Marker now;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 6; // 1 seconds

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    private LocationManager locManager;
    TextView mapText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_menu);
        this.context = this;

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mapText = (TextView)findViewById(R.id.mapText);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLocation();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (myLatLong == null) {
            getCurrentLocation();
        }
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.side_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void callAlertDialog(String message) {
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setMessage(message);
        alertBuilder.setTitle("Enable Location Service");
        alertBuilder.setPositiveButton(context.getResources().getString(R.string.str_enable), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(locationIntent);
            }
        });
        alertBuilder.setNegativeButton(context.getResources().getString(R.string.str_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(SideMenuActivity.this,"Cannot get current location",Toast.LENGTH_LONG).show();
            }
        });
        alertBuilder.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(now != null){
            now.remove();

        }
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if(now != null){
                    now.remove();
                }
                Log.i("centerLat",String.valueOf(cameraPosition.target.latitude));
                Log.i("centerLong", String.valueOf(cameraPosition.target.latitude));

                mMap.clear();
                mapText.setText(String.valueOf(cameraPosition.target.latitude) + " , " + String.valueOf(cameraPosition.target.latitude));
//                mMap.addMarker(new MarkerOptions().position(cameraPosition.target));
            }
        });
    }

    @Override
    public void onPlaceSelected(Place place) {
        float zoomLevel = ZOOM_LEVEL; //This goes up to 21
        myLatLong = place.getLatLng();
        Log.i("My Place", "Selected Place: " + place.getName());
//        mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title("Marker in " + place.getName()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), zoomLevel));
        mapText.setText(place.getName()+", "+place.getLatLng().toString());
    }

    @Override
    public void onError(Status status) {
        Log.i("My Place", "Error occurred " + status);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case mMY_PERMISSIONS_REQUEST:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    request_granted = true;
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    request_granted = false;
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
    public void requestPermission(){
        ActivityCompat.requestPermissions(SideMenuActivity.this,
                new String[]{Manifest.permission.READ_CONTACTS},
                mMY_PERMISSIONS_REQUEST);
    }
    @Override
    public void onConnected(Bundle bundle) {
        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermission();

                if(request_granted==true){
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                    if (mLastLocation != null) {
                        mLastLatitude = mLastLocation.getLatitude();
                        mLastLongitude = mLastLocation.getLongitude();
                    }
                }


            }else {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                if (mLastLocation != null) {
                    mLastLatitude = mLastLocation.getLatitude();
                    mLastLongitude = mLastLocation.getLongitude();
                }
            }
        } else {
            // Pre-Marshmallow
            requestPermission();
            if(request_granted==true){
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                if (mLastLocation != null) {
                    mLastLatitude = mLastLocation.getLatitude();
                    mLastLatitude = mLastLocation.getLongitude();
                    myLatLong = new LatLng(mLastLatitude,mLastLongitude);
                    mMap.clear();
//                    mMap.addMarker(new MarkerOptions().position(myLatLong));
                }
            }
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(context,"GoogleAPI Connection failed..",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;

        if(now != null){
            now.remove();

        }

        // Getting latitude of the current location
        double latitude = location.getLatitude();

        // Getting longitude of the current location
        double longitude = location.getLongitude();

        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);
//        now = mMap.addMarker(new MarkerOptions().position(latLng));
        // Showing the current location in Google Map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map
        mMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM_LEVEL));
        mapText.setText(latitude+" , "+longitude);
    }

     @Override
     public void onStatusChanged(String provider, int status, Bundle extras) {
         System.out.println("--onStatusChanged "+provider +"status "+status+ "bundle "+extras.toString());
     }

     @Override
     public void onProviderEnabled(String provider) {
         System.out.println("--onProviderEnabled "+provider);
         getCurrentLocation();
     }

     @Override
     public void onProviderDisabled(String provider) {
         Snackbar.make(findViewById(android.R.id.content), "gps connection lost", Snackbar.LENGTH_LONG).setAction("Action", null).show();
     }


    public void getCurrentLocation(){
        boolean isGps, isInternet;

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        isGps = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
        isInternet = locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);

        if (!isInternet) {
            callAlertDialog("Kindle enable location service");

        }else {
            if (isInternet) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        mapText.setText(latitude+" , "+longitude);
                    }
                }
            }
        }
    }
}
