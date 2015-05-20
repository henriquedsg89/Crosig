package com.gh.crosig;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.gh.crosig.model.Problem;
import com.gh.crosig.model.SuggestedStatus;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
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
        ParseObject.registerSubclass(Problem.class);
        ParseObject.registerSubclass(SuggestedStatus.class);
        Parse.initialize(this, "k4C4iDQPonsBtWzZeOyzsxQrYpfn7ODBilu5v2XC", "80AjPLWpKuTZs0oI4A8Tb9wOXuWPzzHWoCS40ZGd");

        ParseFacebookUtils.initialize(getApplicationContext());
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
                        startLogged();
                    }
                }
            });
    }

    private void startLogged() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void startAnonymous(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("anonymous", true);
        this.startActivity(intent);
    }
}
