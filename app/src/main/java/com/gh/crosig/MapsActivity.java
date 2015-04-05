package com.gh.crosig;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

public class MapsActivity extends FragmentActivity {

    private static final String TAG = "MapsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if (findViewById(R.id.activity_map) != null) {
            Log.i(TAG, "View 'activity_map' is not null");
            MapFragment mapFragment = new MapFragment();
            mapFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_map, mapFragment)
                    .commit();
        }
    }

    public void newProblem(View view) {
        Log.i("MAPS", "On new problem");
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.replace(R.id.activity_map, new NewProblem());
        trans.addToBackStack(null);
        trans.commit();
        Log.i("MAPS", "Commited");
    }

}
