package com.gh.crosig.model;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.gh.crosig.R;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseObject;

public class ViewProblem extends ActionBarActivity {

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
                final Problem problem = p;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView tv = (TextView) findViewById(R.id.view_problem_name);
                        tv.setText(problem.getName());
                        ParseImageView piv = (ParseImageView) findViewById(R.id.view_problem_image);
                        piv.setParseFile(problem.getImage());
                    }
                });
            }
        });
    }

}
