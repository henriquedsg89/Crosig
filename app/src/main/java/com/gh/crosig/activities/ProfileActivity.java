package com.gh.crosig.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gh.crosig.R;
import com.parse.ParseImageView;
import com.parse.ParseUser;

public class ProfileActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ParseImageView profilePicture = (ParseImageView) findViewById(R.id.activity_profile_picture);
        profilePicture.setParseFile(ParseUser.getCurrentUser().getParseFile("profilePicture"));
        profilePicture.loadInBackground();

        TextView name = (TextView) findViewById(R.id.activity_profile_name);
        name.setText(ParseUser.getCurrentUser().getUsername());

        TextView score = (TextView) findViewById(R.id.activity_profile_score);
        score.setText(String.valueOf(ParseUser.getCurrentUser().get("score")));

        Button logout = (Button) findViewById(R.id.activity_profile_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        ParseUser.logOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
