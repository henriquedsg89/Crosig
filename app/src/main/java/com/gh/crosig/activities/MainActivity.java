package com.gh.crosig.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.gh.crosig.R;
import com.gh.crosig.model.Problem;
import com.gh.crosig.model.ProblemFollow;
import com.gh.crosig.model.UserDetail;
import com.gh.crosig.model.UserNotification;
import com.gh.crosig.utils.CommonUtils;
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
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.widget.Toast.LENGTH_LONG;


public class MainActivity extends ActionBarActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnInfoWindowClickListener {

    public static final String INTENT_EXTRA_LOCATION = "location";
    private static final int MAX_SEARCH_DISTANCE = 100;

    private static final String TAG = "MainActivity";

    private static final int ZOOM = 16;

    public static final int NEUTRAL_SCORE = 50;
    public static int VERACITY_INDICE = 0;

    public static Problem selectedProblem;
    public static Location mLastLocation, previousLocation;
    public static Marker selectedMarker;
    public static boolean followingCurrentProblem = false;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Map<String, Marker> markers = new HashMap<>();
    private Map<Marker, Problem> markersProblems = new HashMap<>();
    private TextView notificationCounter;
    private ParseImageView profilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ParseUser.getCurrentUser() == null && !getIntent().getBooleanExtra("anonymous", false)) {
            goToSplash();
            return;
        } else if (ParseUser.getCurrentUser() != null) {
            loadFBProfile();
        }
        calcVeracityIndice();
        buildGoogleApiClient();
        setUpMapIfNeeded();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.global, menu);

        if (ParseUser.getCurrentUser() == null) {
            menu.findItem(R.id.action_new_problem).setVisible(false);
            menu.findItem(R.id.action_alert).setVisible(false);
        }

        MenuItem alertItem = menu.findItem(R.id.action_alert);
        MenuItem profileItem = menu.findItem(R.id.profile_action);

        setUpNotificationCounter(alertItem);
        setUpProfile(profileItem);

        return super.onCreateOptionsMenu(menu);
    }

    private void setUpNotificationCounter(MenuItem alertItem) {
        ImageView notifImage = (ImageView) alertItem.getActionView().findViewById(R.id.notification_image);
        notifImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNotification();
            }
        });

        notificationCounter = (TextView) alertItem.getActionView().findViewById(R.id.action_notification_count);
        notificationCounter.setText("0");
        ParseQuery<UserNotification> query = UserNotification.getQuery();
        query.whereEqualTo("to", ParseUser.getCurrentUser());
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int i, ParseException e) {
                notificationCounter.setText(String.valueOf(i));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_new_problem) {
            goNewProblem();
        } else if (id == R.id.action_search) {
            openSearch();
        } else if (id == R.id.action_alert) {
            openNotification();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openNotification() {
        Log.d(TAG, "Creating User Notification Activity");
        Intent intent = new Intent(this, UserNotificationActivity.class);
        startActivity(intent);
    }

    private void openSearch() {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("location", mLastLocation);
        startActivity(intent);
    }

    private void logout() {
        goToSplash();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "On resuming...");
    }

    private void setUpMapIfNeeded() {
        Log.d(TAG, "Setting Up Google Maps...");
        SupportMapFragment mapFrag = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment));
        mapFrag.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "Google map ready!");
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setOnInfoWindowClickListener(this);
    }

    private void setUpProfile(MenuItem profileItem) {
        Log.d(TAG, "Setting Up facebook user...");
        if (Profile.getCurrentProfile() == null) {
            return;
        }

        profilePicture = (ParseImageView) profileItem.getActionView().findViewById(R.id.profile_picture);
        profilePicture.setParseFile(ParseUser.getCurrentUser().getParseFile("profilePicture"));
        profilePicture.loadInBackground();
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

    }

    private void goToSplash() {
        ParseUser.logOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void goNewProblem() {
        if (mLastLocation == null) {
            Toast.makeText(MainActivity.this,
                    "Por favor ative a localização do seu aparelho e tente novamente.", Toast.LENGTH_LONG).show();
            return;
        }
        Log.i(TAG, "Click at new problem icon!");
        Intent intent = new Intent(this, NewProblemActivity.class);
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
                    ParseGeoPoint loc = problem.getLocation();

                    BitmapDescriptor markerIconColor = BitmapDescriptorFactory
                            .defaultMarker(CommonUtils.getColor(MainActivity.this,
                                    problem.getStatus()));
                    MarkerOptions opts = new MarkerOptions().title(problem.getName())
                            .snippet(problem.getType())
                            .position(new LatLng(loc.getLatitude(), loc.getLongitude()))
                            .alpha(0.7f)
                            .icon(markerIconColor);
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

                if (problems.isEmpty()) {
                    for (Marker m : markers.values()) {
                        m.remove();
                    }
                }
            }
        });
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        followingCurrentProblem = false;
        selectedProblem = markersProblems.get(marker);
        selectedMarker = marker;
        ParseQuery<ProblemFollow> followParseQuery = ProblemFollow.getQuery();
        followParseQuery.whereEqualTo("user", ParseUser.getCurrentUser());
        followParseQuery.whereEqualTo("problem", selectedProblem);
        followParseQuery.findInBackground(new FindCallback<ProblemFollow>() {
            @Override
            public void done(List<ProblemFollow> problemFollows, ParseException e) {
                for (ProblemFollow p : problemFollows) {
                    if (p.getProblem().getObjectId().equals(selectedProblem.getObjectId())) {
                        MainActivity.followingCurrentProblem = true;
                        break;
                    }
                }
                Intent intent = new Intent(MainActivity.this, ViewProblemActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadFBProfile() {
        final String profilePictureURL = Profile.getCurrentProfile().getProfilePictureUri(120, 120).toString();
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                try {
                    if (!UserDetail.existsDetail(ParseUser.getCurrentUser())) {
                        UserDetail.newInstance(ParseUser.getCurrentUser()).saveInBackground();
                    }
                    checkIsUserBanned();

                    URL url = new URL(profilePictureURL);
                    final Bitmap bitmap = ImageUtils.getRoundedCornerBitmap(BitmapFactory
                            .decodeStream(url.openConnection().getInputStream()));
                    if (ParseUser.getCurrentUser().getParseFile("profilePicture") == null) {
                        final ParseFile parseFile = new ParseFile("profilePicture_" + ParseUser.getCurrentUser().getObjectId() + ".jpg",
                                ImageUtils.bitmapToByteArray(bitmap));
                        parseFile.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                profilePicture.setParseFile(parseFile);
                                profilePicture.loadInBackground();
                                ParseUser.getCurrentUser().put("profilePicture", parseFile);
                                ParseUser.getCurrentUser().put("username", Profile.getCurrentProfile().getName());
                                    ParseUser.getCurrentUser().saveInBackground();
                            }
                        });
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    private void checkIsUserBanned() {
        if (UserDetail.getCurrentUserDetail().getReputation() <= 0) {
            Toast.makeText(getApplicationContext(), "Desculpe, mas seu usuário foi banido!", LENGTH_LONG);
            ParseUser.logOut();
            finish();
        }
    }

    private void calcVeracityIndice() {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                ParseQuery<UserDetail> q = UserDetail.getQuery();
                q.whereGreaterThan("reputation", 50);
                q.findInBackground(new FindCallback<UserDetail>() {
                    public void done(List<UserDetail> userDetails, ParseException e) {
                        if (userDetails.size() < 2) {
                            //cant set veracity if there isnt true users enought
                            VERACITY_INDICE = Integer.MAX_VALUE;
                            return;
                        }
                        int sum = 0;
                        for (UserDetail u : userDetails) {
                            sum += (Integer) u.get("reputation");
                        }
                        try {
                            VERACITY_INDICE = (int) (sum * 1.5 / userDetails.size());
                        } catch (Exception e2) {
                            Log.d(TAG, "Failed to calc veracity indice! " + e.getMessage());
                        }
                    }
                });
                return null;
            }
        }.execute();
    }

}
