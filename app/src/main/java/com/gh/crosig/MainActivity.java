package com.gh.crosig;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.Profile;
import com.gh.crosig.model.Problem;
import com.gh.crosig.utils.ImageUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.widget.Toast.LENGTH_LONG;


public class MainActivity extends ActionBarActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnInfoWindowClickListener {

    public static final String INTENT_EXTRA_LOCATION = "location";
    private static final int MAX_SEARCH_DISTANCE = 100;

    private static final String TAG = "MainActivity";

    private static final int ZOOM = 16;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation, previousLocation;
    private Map<String, Marker> markers = new HashMap<>();
    private Map<Marker, Problem> markersProblems = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buildGoogleApiClient();
        setUpMapIfNeeded();
        setUpButtons();
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
        mMap.setOnInfoWindowClickListener(this);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.activity_main_menu);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        setUpUser();
    }

    private void setUpUser() {
        Log.d(TAG, "Setting Up facebook user...");
        final String profilePictureURL = Profile.getCurrentProfile().getProfilePictureUri(64, 64).toString();
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                try {
                    URL url = new URL(profilePictureURL);
                    final Bitmap bitmap = ImageUtils.getRoundedCornerBitmap(BitmapFactory.decodeStream(url.openConnection().getInputStream()));
                    Log.d(TAG, "Bitmap = " + bitmap);
                    runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              ImageView imageView = (ImageView)getSupportActionBar().getCustomView().findViewById(R.id.profile_picture);
                              imageView.setImageBitmap(bitmap);
                          }
                      }
                    );

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
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
    protected void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Connecting location...");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Toast.makeText(this, String.format("GPS conectado",
                    mLastLocation.getLatitude(), mLastLocation.getLongitude()), LENGTH_LONG).show();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 16));
            executeMapQuery(mLastLocation);
        }
        requestLocationUpdates();
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

    @Override
    public void onLocationChanged(Location location) {
        this.previousLocation = mLastLocation;
        this.mLastLocation = location;
        if (previousLocation == null) {
            executeMapQuery(mLastLocation);
        } else if (new ParseGeoPoint(mLastLocation.getLatitude(), mLastLocation.getLongitude())
                .distanceInKilometersTo(new ParseGeoPoint(previousLocation.getLatitude(), previousLocation.getLongitude()))
                > 0.5) { // se a distância for maior que 50 metros, recarrega
            executeMapQuery(mLastLocation);
            moveCamera(location);
        }
    }

    private void requestLocationUpdates() {
        LocationRequest lr = new LocationRequest().setInterval(10000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setFastestInterval(2000);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, lr, this);
    }

    private void moveCamera(Location location) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),
                location.getLongitude()), ZOOM));
    }

    private void executeMapQuery(Location location) {
        if (mLastLocation == null) {
            return;
        }

        ParseQuery<Problem> mapQuery = Problem.getQuery();
        mapQuery.whereWithinKilometers("location", new ParseGeoPoint(location.getLatitude(),
                location.getLongitude()), MAX_SEARCH_DISTANCE);
        mapQuery.include("user");
        mapQuery.orderByDescending("createdAt");
        mapQuery.setLimit(MAX_SEARCH_DISTANCE);

        Log.d(TAG, "Finding problems in backgroud...");
        mapQuery.findInBackground(new FindCallback<Problem>() {
            @Override
            public void done(List<Problem> problems, com.parse.ParseException e) {
                Log.d(TAG, String.format("Found %d problems", problems.size()));
                Set<String> toKeep = new HashSet<String>();
                for (Problem problem : problems) {
                    ParseGeoPoint loc = problem.getParseGeoPoint("location");

                    BitmapDescriptor markerIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE);
                    MarkerOptions opts = new MarkerOptions().title(problem.getName())
                            .snippet(problem.getType())
                            .position(new LatLng(loc.getLatitude(), loc.getLongitude()))
                            .alpha(0.7f)
                            .icon(markerIcon);
                    Marker marker = mMap.addMarker(opts);
                    markersProblems.put(marker, problem);
                    markers.put(problem.getObjectId(), marker);
                    toKeep.add(problem.getObjectId());
                }

                for (Map.Entry<String, Marker> entry : markers.entrySet()) {
                    if (!toKeep.contains(entry.getKey())) {
                        entry.getValue().remove();
                        markersProblems.remove(entry.getValue());
                    }
                }

            }
        });
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        Problem problem = markersProblems.get(marker);
        Intent intent = new Intent(this, ViewProblem.class);
        intent.putExtra("problem", problem.getObjectId());
        startActivity(intent);
    }
}
