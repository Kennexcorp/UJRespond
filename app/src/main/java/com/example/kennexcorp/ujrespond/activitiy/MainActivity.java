package com.example.kennexcorp.ujrespond.activitiy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kennexcorp.ujrespond.R;
import com.example.kennexcorp.ujrespond.model.Report;
import com.example.kennexcorp.ujrespond.model.UserLocation;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends FragmentActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private static final int ZOOM = 15;
    private static final String HEALTH_EMERGENCY_ID = "111";
    private static final String FIRE_EMERGENCY_ID = "222";
    private static final String POLICE_EMERGENCY_ID = "333";
    private static final int OTHER_EMERGENCY_ID = 000;
    private static final int REQUEST_CHECK_SETTINGS = 14;

    private GoogleMap mMap;

    // fields
    private TextView longitude;
    private TextView latitude;
    private CardView mHealth;
    private CardView mFire;
    private CardView mPolice;
    private CardView mOthers;
    private CardView mcall;
    private Report eReport;
    private UserLocation lastKnownLoc;
    private String user_id;



    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mLocationReference;

    SettingsClient msSettingsClient;
    FusedLocationProviderClient mFusedLocationProviderClient;
    Location mCurrentLocation;
    LocationRequest mLocationRequest;
    LocationCallback mLocationCallback;
    LocationSettingsRequest mLocationSettingsRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        msSettingsClient = LocationServices.getSettingsClient(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        createLocationRequest();
        createLocationCallback();
        buildLocationSettingsRequest();

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(MainActivity.this, Login.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Reports");
        mLocationReference = FirebaseDatabase.getInstance().getReference("LastKnownLocation");

        longitude = findViewById(R.id.longitude);
        latitude = findViewById(R.id.latitude);

        mHealth = findViewById(R.id.ehealth);
        mHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportEmergency(HEALTH_EMERGENCY_ID);
            }
        });

        mFire = findViewById(R.id.efire);
        mFire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportEmergency(FIRE_EMERGENCY_ID);
            }
        });

        mPolice = findViewById(R.id.epolicebutton);
        mPolice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportEmergency(POLICE_EMERGENCY_ID);
            }
        });

        mOthers = findViewById(R.id.eothers);
        mOthers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle loc = new Bundle();
                Intent intent = new Intent(getApplicationContext(), ReportActivity.class);
                if (latitude.getText().toString() != null && longitude.getText().toString() != null) {
                    String[] location = {latitude.getText().toString(), longitude.getText().toString()};
                    loc.putStringArray("location", location);
                    intent.putExtras(loc);
                    startActivity(intent);
                }
            }
        });

        mcall = findViewById(R.id.ecall);
        mcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HotlinesActivity.class);
                startActivity(intent);
            }
        });

        lastKnownLoc = new UserLocation();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    protected void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mCurrentLocation = locationResult.getLastLocation();
                latitude.setText(mCurrentLocation.getLatitude() + "");
                longitude.setText(mCurrentLocation.getLongitude() + "");
                mMap.clear();
                LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng).title("You're Here"));

                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(ZOOM).build();

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }
        };
    }

    private void stopLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setSmallestDisplacement(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        Task<LocationSettingsResponse> task = msSettingsClient.checkLocationSettings(mLocationSettingsRequest);

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            Log.e("error", sendEx.getMessage());
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        String errorMessage = "Location settings are inadequate, and cannot be " +
                                "fixed here. Fix in Settings.";
                        Log.e("error", errorMessage);
                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }

    private void reportEmergency(String Eid) {

        DatabaseReference reportRef = mDatabaseReference.push();
        Long timeStamp = System.currentTimeMillis() / 1000;
        //String ts = timeStamp.toString();
        Log.e("TimeStamp: ", timeStamp.toString());
        String lat = latitude.getText().toString();
        String lon = longitude.getText().toString();
        eReport = new Report();
        if (TextUtils.isEmpty(lon) && TextUtils.isEmpty(lat)) {
            Toast.makeText(this, "Your location cannot be determined", Toast.LENGTH_LONG).show();
            return;
        } else {
            eReport.setReportId(reportRef.getKey());
            eReport.setId(Eid);
            eReport.setTimeStamp(timeStamp);
            eReport.setUserId(user_id);
            eReport.setReportStatus(false);
            eReport.setLatitude(lat);
            eReport.setLongitude(lon);
            eReport.setReportStatus(false);

            reportRef.setValue(eReport);
            Toast.makeText(MainActivity.this, "Report Sent", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

        if (id == R.id.profile) {

            startActivity(new Intent(this, ProfileActivity.class));

        } else if (id == R.id.notifications) {

            //startActivity(new Intent(this, NotificationActivity.class));
            Toast.makeText(this, "No new Notification", Toast.LENGTH_LONG).show();

        } else if (id == R.id.safety_tips) {

            startActivity(new Intent(this, TipsActivity.class));

        } else if (id == R.id.hotlines) {

            startActivity(new Intent(this, HotlinesActivity.class));

        } else if (id == R.id.request_complaints) {

            startActivity(new Intent(this, RequestActivity.class));

        } else if (id == R.id.about_us) {

            startActivity(new Intent(this, About.class));

        } else if (id == R.id.logout) {

            mAuth.signOut();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
        startLocationUpdates();
    }
}
