package com.gh.crosig.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.gh.crosig.R;
import com.gh.crosig.model.Problem;
import com.gh.crosig.utils.CommonUtils;
import com.gh.crosig.utils.DateUtils;
import com.parse.ParseGeoPoint;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;


public class SearchActivity extends ActionBarActivity {

    private static final String TAG = "SearchFragment";
    private String queryFilter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        final ParseQueryAdapter.QueryFactory<Problem> factory =
                new ParseQueryAdapter.QueryFactory<Problem>() {
                    public ParseQuery<Problem> create() {
                        Log.d(TAG, "Location " + MainActivity.mLastLocation);
                        ParseGeoPoint geoPoint = new ParseGeoPoint(MainActivity.mLastLocation.getLatitude(), MainActivity.mLastLocation.getLongitude());
                        ParseQuery<Problem> query = Problem.getQuery();
                        query.include("user");
                        query.orderByDescending("createdAt");
                        if (queryFilter != null) {
                            query.whereMatches("name", "(" + queryFilter + ")", "i");
                        }
                        query.whereWithinKilometers("location", geoPoint, 100);
                        return query;
                    }
                };
        final ParseQueryAdapter<Problem> adapter = new ParseQueryAdapter<Problem>(this, factory) {
            @Override
            public View getItemView(Problem problem, View view, ViewGroup parent) {
                if (view == null) {
                    view = View.inflate(getContext(), R.layout.problem_list_view_item, null);
                }
                ParseImageView parseImageView = (ParseImageView) view.findViewById(R.id.search_problem_image);
                TextView nameView = (TextView) view.findViewById(R.id.list_item_problem_name);
                TextView typeView = (TextView) view.findViewById(R.id.list_item_problem_type);
                TextView dateView = (TextView) view.findViewById(R.id.list_item_problem_date);
                TextView statusView = (TextView) view.findViewById(R.id.list_item_status);

                nameView.setText(problem.getName());
                typeView.setText(problem.getType());
                dateView.setText(DateUtils.dateToStr(problem.getCreatedAt()));
                statusView.setText(problem.getStatus());
                statusView.setTextColor(CommonUtils.getColorInt(SearchActivity.this, problem.getStatus()));
                parseImageView.setParseFile(problem.getImage());
                parseImageView.loadInBackground();
                return view;
            }
        };
        adapter.setTextKey("name");
//        adapter.setPaginationEnabled(true);

        ListView problemListView = (ListView) findViewById(R.id.search_list_view);
        problemListView.setAdapter(adapter);
        problemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.selectedProblem = adapter.getItem(position);
                Intent intent = new Intent(SearchActivity.this, ViewProblemActivity.class);
                startActivity(intent);
            }
        });

        final EditText text = (EditText) findViewById(R.id.search_text);
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                queryFilter = s.toString();
                Log.d(TAG, "Loading By Text Changed - QueryFilter " + queryFilter);
                adapter.loadObjects();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        final ImageButton btn = (ImageButton) findViewById(R.id.search_search_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryFilter = text.getText().toString();
                Log.d(TAG, "Loading By BtnSearch Press - QueryFilter " + queryFilter);
                adapter.loadObjects();
            }
        });
        adapter.loadObjects();
    }

}