package com.gh.crosig;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.GraphRequestAsyncTask;
import com.facebook.Profile;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.Arrays;


public class SplashActivity extends Activity {

    private final static String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initDB();
    }

    private void initDB() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null && ParseFacebookUtils.isLinked(currentUser)) {
            startLogged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    public void loginFb(View view) {
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, Arrays.asList("public_profile", "email"),
                new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (parseUser == null) {
                            Log.d(TAG, "Usuário cancelou login.");
                        } else {
                            if (parseUser.isNew()) {
                                loadFBProfile();
                            }
                            startLogged();
                        }
                    }
                });

    }

    private void loadFBProfile() {
        ParseUser.getCurrentUser().put("name", Profile.getCurrentProfile().getName());
        ParseUser.getCurrentUser().saveEventually();

    }

    private void startLogged() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void startAnonymous(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("anonymous", true);
        startActivity(intent);
        finish();
    }
}
