package com.gh.crosig;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.gh.crosig.model.Problem;
import com.gh.crosig.model.ProblemFollow;
import com.gh.crosig.model.SuggestedStatus;
import com.gh.crosig.model.UserNotification;
import com.gh.crosig.utils.CommonUtils;
import com.gh.crosig.utils.DateUtils;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ViewProblemActivity extends ActionBarActivity implements
        SuggestStatusDlg.SuggestStatusDlgListener, UpdateStatusDlg.UpdateStatusDlgListener {

    private static final String TAG = "ViewProblemActivity";
    private Problem currentProblem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_problem);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        currentProblem = MainActivity.selectedProblem;

        ParseImageView piv = (ParseImageView) findViewById(R.id.view_problem_image);
        piv.setParseFile(currentProblem.getImage());
        piv.loadInBackground();

        TextView tv = (TextView) findViewById(R.id.view_problem_name);
        tv.setText(currentProblem.getName());

        TextView type = (TextView) findViewById(R.id.view_problem_type);
        type.setText(currentProblem.getType());

        TextView date = (TextView) findViewById(R.id.view_problem_date);
        date.setText(DateUtils.dateToStr(currentProblem.getCreatedAt()));

        TextView desc = (TextView) findViewById(R.id.view_problem_desc);
        desc.setText(currentProblem.getDesc());

        TextView status = (TextView) findViewById(R.id.view_problem_status);
        status.setText(currentProblem.getStatus());
        status.setTextColor(CommonUtils.getColorInt(this, currentProblem.getStatus()));


        Log.d(TAG, "Loaded problem: " + currentProblem.getType());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_view_problem, menu);

        final MenuItem follow = menu.findItem(R.id.view_follow);
        final MenuItem unfollow = menu.findItem(R.id.view_unfollow);

        if (MainActivity.followingCurrentProblem) {
            follow.setVisible(false);
            unfollow.setVisible(true);
        } else {
            follow.setVisible(true);
            unfollow.setVisible(false);
        }

        MenuItem suggestStatus = menu.findItem(R.id.view_problem_suggest_status);
        MenuItem updateStatus = menu.findItem(R.id.view_problem_update_status);
        MenuItem deleteProblem = menu.findItem(R.id.view_problem_delete_problem);

        if (ParseUser.getCurrentUser().getObjectId().equals(currentProblem.getUser().getObjectId())) {
            suggestStatus.setVisible(false);
        } else if (getIntent().getBooleanExtra("anonymous", false)) {
            suggestStatus.setVisible(false);
            updateStatus.setVisible(false);
            deleteProblem.setVisible(false);
        } else {
            updateStatus.setVisible(false);
            deleteProblem.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.view_follow:
                followProblem();
                break;
            case R.id.view_unfollow:
                unfollowProblem();
                break;
            case R.id.view_problem_suggest_status:
                suggestStatus();
                break;
            case R.id.view_problem_update_status:
                updateStatus();
                break;
            case R.id.view_problem_delete_problem:
                delete();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void followProblem() {
        ProblemFollow follow = ProblemFollow.newInstance(currentProblem, ParseUser.getCurrentUser());
        follow.saveInBackground();
        finish();
    }

    private void unfollowProblem() {
        ParseQuery<ProblemFollow> unfollowParseQuery = ProblemFollow.getQuery();
        unfollowParseQuery.whereEqualTo("user", ParseUser.getCurrentUser());
        unfollowParseQuery.whereEqualTo("problem", currentProblem);
        unfollowParseQuery.findInBackground(new FindCallback<ProblemFollow>() {
            @Override
            public void done(List<ProblemFollow> problemFollows, ParseException e) {
                for (ProblemFollow p : problemFollows) {
                    if (p.getProblem().getObjectId().equals(currentProblem.getObjectId())) {
                        try {
                            p.delete();
                            finish();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                        return;
                    }
                }
            }
        });

    }

    private void suggestStatus() {
        SuggestStatusDlg dlg = new SuggestStatusDlg();
        dlg.show(getFragmentManager(), "SuggestStatusDlg");
    }

    private void delete() {
        new AlertDialog.Builder(this)
                .setMessage("Confirma exclusão?")
                .setCancelable(false)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            currentProblem.delete();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Log.d(ViewProblemActivity.TAG, "Problem removed - " + currentProblem.getName());
                        finish();
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void updateStatus() {
        UpdateStatusDlg dlg = new UpdateStatusDlg();
        dlg.show(getFragmentManager(), "UpdateStatusDlg");
    }

    @Override
    public void onPickSuggestStatus(String status) {
        SuggestedStatus suggestedStatus = new SuggestedStatus();
        suggestedStatus.setStatus(status);
        suggestedStatus.setProblem(currentProblem);
        suggestedStatus.saveInBackground();

        ProblemFollow follow = ProblemFollow.newInstance(currentProblem, ParseUser.getCurrentUser());
        follow.saveInBackground();

        UserNotification userNotification = new UserNotification();
        userNotification.setMsg(currentProblem.getUser().get("name") + ": sugeriu o status '" + status + "'");
        userNotification.setUser(currentProblem.getUser());
        userNotification.setFrom(ParseUser.getCurrentUser());
        userNotification.saveInBackground();

        finish();
    }

    @Override
    public void onPickUpdateStatus(String status) {
        currentProblem.setStatus(status);
        currentProblem.saveInBackground();
        finish();
    }
}
