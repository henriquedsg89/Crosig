package com.gh.crosig;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gh.crosig.R;
import com.gh.crosig.model.Problem;
import com.gh.crosig.model.SuggestedStatus;
import com.gh.crosig.model.UserNotification;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class ViewProblem extends ActionBarActivity implements
        SuggestStatusDlg.SuggestStatusDlgListener {

    private Problem currentProblem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_problem);
        String problemId = (String) getIntent().getExtras().get("problem");

        Problem problem = new Problem();
        problem.setObjectId(problemId);
        problem.fetchInBackground(new GetCallback<Problem>() {
            @Override
            public void done(Problem p, ParseException e) {
                currentProblem = p;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ParseImageView piv = (ParseImageView) findViewById(R.id.view_problem_image);
                        piv.setParseFile(currentProblem.getImage());
                        piv.loadInBackground();

                        TextView tv = (TextView) findViewById(R.id.view_problem_name);
                        tv.setText(currentProblem.getName());

                        TextView type = (TextView) findViewById(R.id.view_problem_type);
                        type.setText(currentProblem.getType());

                        TextView desc = (TextView) findViewById(R.id.view_problem_desc);
                        desc.setText(currentProblem.getDesc());

                        if (ParseUser.getCurrentUser().equals(currentProblem.getUser())) {
                            LinearLayout layout =  (LinearLayout) findViewById(R.id.author_layout);
                            layout.setVisibility(View.VISIBLE);
                        } else {
                            LinearLayout layout =  (LinearLayout) findViewById(R.id.no_author_layout);
                            layout.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
        setUpButtonsClick();
    }

    private void setUpButtonsClick() {
        Button updateStatus = (Button) findViewById(R.id.view_problem_update_status);
        updateStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SuggestStatusDlg dlg = new SuggestStatusDlg();
                dlg.show(getFragmentManager(), "SuggestStatusDlg");
            }
        });
    }

    @Override
    public void onPickSuggestStatus(int suggestStatus) {
        SuggestedStatus suggestedStatus = new SuggestedStatus();
        suggestedStatus.setStatus(suggestStatus);
        suggestedStatus.setProblem(currentProblem);
        suggestedStatus.saveEventually();

        UserNotification userNotification = new UserNotification();
        userNotification.setUser(currentProblem.getUser());
    }
}
