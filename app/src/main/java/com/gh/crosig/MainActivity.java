package com.gh.crosig;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.gh.crosig.model.Problem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.LinkedList;
import java.util.List;

import static android.widget.Toast.LENGTH_LONG;


public class MainActivity extends ActionBarActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String INTENT_EXTRA_LOCATION = "location";
    private static final int MAX_SEARCH_DISTANCE = 100;

    private static final String TAG = "MainActivity";
    private GoogleMap mMap;
    private GraphUser user;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private List<Marker> markers = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDB();
        buildGoogleApiClient();
        setUpMapIfNeeded();
        setUpButtons();
    }

    private void initDB() {
        ParseObject.registerSubclass(Problem.class);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "k4C4iDQPonsBtWzZeOyzsxQrYpfn7ODBilu5v2XC", "80AjPLWpKuTZs0oI4A8Tb9wOXuWPzzHWoCS40ZGd");
    }

    private void setUpButtons() {
        ImageButton npBtn = (ImageButton) findViewById(R.id.new_problem);
        npBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newProblem(v);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "On resuming...");
        setUpMapIfNeeded();
        setUpUserIfNeed();
    }

    private void setUpUserIfNeed() {
        Log.d(TAG, "Setting Up facebook user...");
        if (user == null) {
            Session session = Session.getActiveSession();
            if (session != null && session.isOpened()) {
                Request.newMeRequest(session, new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser graphUser, Response response) {
                        user = graphUser;
                        ProfilePictureView pictureView = (ProfilePictureView) findViewById(R.id.profile_picture);
                        pictureView.setProfileId(user.getId());
                        Log.i(TAG, String.format("Logged as %s", user.getFirstName()));
                    }
                }).executeAsync();
            } else {
                Log.i(TAG, "Facebook session isn't opened!");
            }
        }
    }

    private void setUpMapIfNeeded() {
        Log.d(TAG, "Setting Up Google Maps...");
        SupportMapFragment mapFrag = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.activity_map));
        mapFrag.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "Google map ready!");
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                executeMapQuery();
            }
        });
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.activity_main_menu);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        restoreActionBar();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void newProblem(View view) {
        if (mLastLocation == null) {
            Toast.makeText(MainActivity.this,
                    "Por favor ative a localização do seu aparelho e tente novamente.", Toast.LENGTH_LONG).show();
            return;
        }
        Log.i(TAG, "Click at new problem icon!");
        Intent intent = new Intent(this, NewProblem.class);
        intent.putExtra(INTENT_EXTRA_LOCATION, mLastLocation);
        startActivity(intent);
    }

    protected synchronized void buildGoogleApiClient() {
        Log.d(TAG, "Building google api client");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Connecting location...");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Toast.makeText(this, String.format("Position... Lat: %.2f - Long: %.2f",
                    mLastLocation.getLatitude(), mLastLocation.getLongitude()), LENGTH_LONG).show();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 16));
            executeMapQuery();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    private void executeMapQuery() {
        if (mLastLocation == null) {
            return;
        }
        ParseQuery<Problem> mapQuery = Problem.getQuery();
        mapQuery.whereWithinKilometers("location", new ParseGeoPoint(mLastLocation.getLatitude(),
                mLastLocation.getLongitude()), MAX_SEARCH_DISTANCE);
        mapQuery.include("user");
        mapQuery.orderByDescending("createdAt");
        mapQuery.setLimit(MAX_SEARCH_DISTANCE);
        mapQuery.findInBackground(new FindCallback<Problem>() {
            @Override
            public void done(List<Problem> problems, com.parse.ParseException e) {
                for (Marker marker : markers) {
                    marker.remove();
                }
                markers = new LinkedList<Marker>();
                for (Problem problem : problems) {
                    MarkerOptions opts = new MarkerOptions().title(problem.getName())
                            .snippet(problem.getType());
                    Marker marker = mMap.addMarker(opts);
                    marker.showInfoWindow();
                }
            }
        });
    }
}
