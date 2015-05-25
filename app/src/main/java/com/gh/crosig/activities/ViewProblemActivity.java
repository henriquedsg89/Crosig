package com.gh.crosig.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gh.crosig.R;
import com.gh.crosig.dialogs.CommentDlg;
import com.gh.crosig.dialogs.SuggestStatusDlg;
import com.gh.crosig.dialogs.UpdateStatusDlg;
import com.gh.crosig.dialogs.VeracityDlg;
import com.gh.crosig.model.Comment;
import com.gh.crosig.model.Problem;
import com.gh.crosig.model.ProblemFollow;
import com.gh.crosig.model.SuggestedStatus;
import com.gh.crosig.model.UserNotification;
import com.gh.crosig.utils.CommonUtils;
import com.gh.crosig.utils.DateUtils;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.List;

public class ViewProblemActivity extends ActionBarActivity implements
        SuggestStatusDlg.SuggestStatusDlgListener, UpdateStatusDlg.UpdateStatusDlgListener,
        CommentDlg.CommentDlgListener, VeracityDlg.VeracityDlgListener {

    private static final String TAG = "ViewProblemActivity";
    private Problem currentProblem;
    private ParseQueryAdapter<Comment> commentAdapter;

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

        setUpCommentList();
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

        if (!ProblemFollow.isFollowingProblem(currentProblem, ParseUser.getCurrentUser())) {
            ProblemFollow follow = ProblemFollow.newInstance(currentProblem, ParseUser.getCurrentUser());
            follow.saveInBackground();
        }

        UserNotification userNotification = new UserNotification();
        userNotification.setMsg(ParseUser.getCurrentUser().getUsername()
                + ": sugeriu o status '" + status + "'");
        userNotification.setTo(currentProblem.getUser());
        userNotification.setFrom(ParseUser.getCurrentUser());
        userNotification.setProblem(currentProblem);
        userNotification.saveInBackground();

        finish();
    }

    @Override
    public void onPickUpdateStatus(String status) {
        currentProblem.setStatus(status);
        currentProblem.saveInBackground();
        finish();
    }

    public void commentClick(View v) {
        CommentDlg dlg = new CommentDlg();
        dlg.show(getFragmentManager(), "CommentDlg");
    }

    public void evaluateClick(View v) {
        VeracityDlg dlg = new VeracityDlg();
        dlg.show(getFragmentManager(), "VeracityDlg");
    }

    @Override
    public void onUserCommented(String comment) {
        Comment c = new Comment();
        c.setComment(comment);
        c.setProblem(currentProblem);
        c.setUser(ParseUser.getCurrentUser());
        try {
            c.save();
            Toast.makeText(getApplicationContext(), "Comentário feito!", Toast.LENGTH_SHORT).show();
            commentAdapter.loadObjects();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (!ProblemFollow.isFollowingProblem(currentProblem, ParseUser.getCurrentUser())) {
            ProblemFollow follow = ProblemFollow.newInstance(currentProblem, ParseUser.getCurrentUser());
            follow.saveInBackground();
        }

        UserNotification userNotification = new UserNotification();
        userNotification.setMsg(ParseUser.getCurrentUser().getUsername() +
                " comentou no problema " + currentProblem.getName().substring(0, 10) + "...");
        userNotification.setTo(currentProblem.getUser());
        userNotification.setFrom(ParseUser.getCurrentUser());
        userNotification.setProblem(currentProblem);
        userNotification.saveInBackground();

    }

    private void setUpCommentList() {
        final ParseQueryAdapter.QueryFactory<Comment> factory =
                new ParseQueryAdapter.QueryFactory<Comment>() {
                    public ParseQuery<Comment> create() {
                        ParseQuery<Comment> query = Comment.getQuery();
                        query.whereEqualTo("problem", currentProblem);
                        query.include("user");
                        query.orderByAscending("createdAt");
                        return query;
                    }
                };
        commentAdapter = new ParseQueryAdapter<Comment>(this, factory) {
            @Override
            public View getItemView(Comment comment, View view, ViewGroup parent) {
                Log.d(TAG, "Item => " + comment.toString());
                if (view == null) {
                    view = View.inflate(getContext(), R.layout.comment_list_view_item, null);
                }

                ParseImageView parseImageView = (ParseImageView) view.findViewById(R.id.view_comment_profile_picture);
                TextView authorView = (TextView) view.findViewById(R.id.comment_author);
                TextView dateView = (TextView) view.findViewById(R.id.comment_date);
                TextView textView = (TextView) view.findViewById(R.id.comment_text);

                ParseUser user = comment.getUser();
                ParseFile pp = user.getParseFile("profilePicture");

                parseImageView.setParseFile(pp);
                parseImageView.loadInBackground();
                authorView.setText(user.getUsername());
                dateView.setText(DateUtils.dateToStr(comment.getCreatedAt()));
                textView.setText(comment.getComment());

                return view;
            }
        };
        commentAdapter.setTextKey("name");
        commentAdapter.setPaginationEnabled(true);

        TextView header = new TextView(getApplicationContext());
        header.setText(R.string.comments);
        header.setTextColor(Color.BLACK);

        ListView commentList = (ListView) findViewById(R.id.view_list_view_comment);
        commentList.addHeaderView(header);
        commentList.setAdapter(commentAdapter);
        commentAdapter.loadObjects();
        Log.d(TAG, "Loaded comments for " + currentProblem.getName());

    }

    @Override
    public void onPickVeracity(boolean isVeridic) {

    }
}
