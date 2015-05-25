package com.gh.crosig.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.gh.crosig.R;
import com.gh.crosig.model.Comment;
import com.gh.crosig.model.UserNotification;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

public class UserNotificationActivity extends ActionBarActivity {

    private static final String TAG = "UserNotificationActivity";
    private ParseQueryAdapter<UserNotification> notificationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_notification);
        setUpAdapter();
    }

    private void setUpAdapter() {
        final ParseQueryAdapter.QueryFactory<UserNotification> factory =
                new ParseQueryAdapter.QueryFactory<UserNotification>() {
                    public ParseQuery<UserNotification> create() {
                        ParseQuery<UserNotification> query = UserNotification.getQuery();
                        query.whereEqualTo("user", ParseUser.getCurrentUser());
                        query.include("user");
                        query.orderByDescending("createdAt");
                        return query;
                    }
                };
        notificationAdapter = new ParseQueryAdapter<>(this, factory);
        notificationAdapter.setTextKey("msg");
        notificationAdapter.setPaginationEnabled(true);

        ListView notificationList = (ListView) findViewById(R.id.notification_list_view);
        notificationList.setAdapter(notificationAdapter);
        notificationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserNotification notification = notificationAdapter.getItem(position);
                MainActivity.selectedProblem = notification.getProblem();
                Intent intent = new Intent(UserNotificationActivity.this, ViewProblemActivity.class);
                startActivity(intent);
            }
        });

        notificationAdapter.loadObjects();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_notification, menu);
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
}
