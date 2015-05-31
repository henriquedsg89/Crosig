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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gh.crosig.R;
import com.gh.crosig.dialogs.CommentDlg;
import com.gh.crosig.dialogs.DenounceDlg;
import com.gh.crosig.dialogs.SuggestStatusDlg;
import com.gh.crosig.dialogs.UpdateStatusDlg;
import com.gh.crosig.dialogs.VeracityDlg;
import com.gh.crosig.model.Comment;
import com.gh.crosig.model.Denounce;
import com.gh.crosig.model.Problem;
import com.gh.crosig.model.ProblemFollow;
import com.gh.crosig.model.SuggestedStatus;
import com.gh.crosig.model.UserDetail;
import com.gh.crosig.model.UserNotification;
import com.gh.crosig.model.Veracity;
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
        CommentDlg.CommentDlgListener, VeracityDlg.VeracityDlgListener,
        DenounceDlg.DenounceDlgListener {

    private static final String TAG = "ViewProblemActivity";
    private Problem currentProblem;
    private ParseQueryAdapter<Comment> commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_problem);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        try {
            currentProblem = MainActivity.selectedProblem;
            currentProblem.fetchIfNeeded();
            currentProblem.getUser().fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }

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

        final Button evaluateBtn = (Button) findViewById(R.id.view_evaluate);
        if (Boolean.TRUE.equals(currentProblem.getVeridicStatus())) {
            evaluateBtn.setText("Verídico!");
            evaluateBtn.setBackgroundColor(Color.BLUE);
            evaluateBtn.setTextColor(Color.WHITE);
            evaluateBtn.setEnabled(false);
        } else if (ParseUser.getCurrentUser() != null && currentProblem.getUser().getObjectId()
                .equals(ParseUser.getCurrentUser().getObjectId())) {
            evaluateBtn.setVisibility(View.INVISIBLE);
        } else {
            try {
                ParseQuery<Veracity> q = Veracity.getQuery();
                q.whereEqualTo("user", ParseUser.getCurrentUser());
                q.whereEqualTo("problem", currentProblem);
                List<Veracity> list = q.find();
                if (!list.isEmpty()) {
                    evaluateBtn.setVisibility(View.INVISIBLE);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

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
        MenuItem denounceProblem = menu.findItem(R.id.view_problem_denounce);
        MenuItem deleteProblem = menu.findItem(R.id.view_problem_delete_problem);

        if (ParseUser.getCurrentUser() == null) {//anonymous login
            suggestStatus.setVisible(false);
            updateStatus.setVisible(false);
            deleteProblem.setVisible(false);
            denounceProblem.setVisible(false);
            follow.setVisible(false);
            unfollow.setVisible(false);
            findViewById(R.id.view_actions).setVisibility(View.INVISIBLE);
        } else if (ParseUser.getCurrentUser().getObjectId().equals(currentProblem.getUser().getObjectId())) {
            suggestStatus.setVisible(false);
            denounceProblem.setVisible(false);
            follow.setVisible(false);
            unfollow.setVisible(false);
        } else {
            updateStatus.setVisible(false);
            deleteProblem.setVisible(false);

            if (Denounce.existDenounce(currentProblem, ParseUser.getCurrentUser())) {
                denounceProblem.setVisible(false);
            }
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
            case R.id.view_problem_denounce:
                showDenounceDlg();
                break;
            case R.id.view_problem_delete_problem:
                delete();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void followProblem() {
        if (!ProblemFollow.isFollowingProblem(currentProblem, ParseUser.getCurrentUser())) {
            ProblemFollow follow = ProblemFollow.newInstance(currentProblem, ParseUser.getCurrentUser());
            follow.saveInBackground();
            MainActivity.followingCurrentProblem = true;
            finish();
        }
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
        MainActivity.followingCurrentProblem = false;
    }

    private void suggestStatus() {
        SuggestStatusDlg dlg = new SuggestStatusDlg();
        dlg.show(getFragmentManager(), "SuggestStatusDlg");
    }

    private void showDenounceDlg() {
        DenounceDlg dlg = new DenounceDlg();
        dlg.show(getFragmentManager(), "DenounceDlg");
    }

    private void delete() {
        new AlertDialog.Builder(this)
                .setMessage("Confirma exclusão?")
                .setCancelable(false)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            MainActivity.selectedMarker.setVisible(false);
                            MainActivity.selectedMarker.remove();
                            currentProblem.delete();
                            notifyAllFollowers("Problema '" + currentProblem.getName() + "' foi excluído!");
                            finish();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
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
    public void onPickSuggestStatus(final String status) {
        SuggestedStatus suggestedStatus = new SuggestedStatus();
        suggestedStatus.setStatus(status);
        suggestedStatus.setProblem(currentProblem);
        suggestedStatus.setUser(ParseUser.getCurrentUser());
        suggestedStatus.saveInBackground();

        followProblem();
        notifyAllFollowers(ParseUser.getCurrentUser().getUsername()
                + ": sugeriu o status '" + status + "'");
        finish();
    }

    private void notifyAllFollowers(final String msg) {
        ParseQuery<ProblemFollow> fpQuery = ProblemFollow.getQuery();
        fpQuery.whereEqualTo("problem", currentProblem);
        fpQuery.findInBackground(new FindCallback<ProblemFollow>() {
            public void done(List<ProblemFollow> problemFollows, ParseException e) {
                for (ProblemFollow pf : problemFollows) {
                    if (!pf.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                        UserNotification userNotification = new UserNotification();
                        userNotification.setMsg(msg);
                        userNotification.setTo(pf.getUser());
                        userNotification.setFrom(ParseUser.getCurrentUser());
                        userNotification.setProblem(currentProblem);
                        userNotification.saveInBackground();
                    }
                }
            }
        });
    }

    @Override
    public void onPickUpdateStatus(String status) {
        try {
            currentProblem.setStatus(status);
            currentProblem.save();

            updatedUsersReputationSuggestedStatus(status);
            if ("Problema resolvido".equals(currentProblem.getStatus())) {
                updateFollowersReputation();
            }
            finish();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void updatedUsersReputationSuggestedStatus(String status) {
        ParseQuery<SuggestedStatus> q = SuggestedStatus.getQuery();
        q.whereEqualTo("problem", currentProblem);
        q.whereEqualTo("status", status);
        q.findInBackground(new FindCallback<SuggestedStatus>() {
            public void done(List<SuggestedStatus> suggestedStatuses, ParseException e) {
                for (SuggestedStatus ss : suggestedStatuses) {
                    UserDetail.getUserDetailFor(ss.getUser()).incReputation(1).saveInBackground();
                }
            }
        });
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

        followProblem();

        String problemName = currentProblem.getName().length() > 20 ?
                currentProblem.getName().substring(0, 20) : currentProblem.getName();
        notifyAllFollowers(ParseUser.getCurrentUser().getUsername() +
                " comentou no problema " + problemName + "...");
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
        Veracity veracity = new Veracity();
        veracity.setProblem(MainActivity.selectedProblem);
        veracity.setUser(ParseUser.getCurrentUser());
        veracity.setVeridic(isVeridic);

        try {
            veracity.save();
            recalcVeracities(isVeridic);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        followProblem();
        notifyAllFollowers(ParseUser.getCurrentUser().getUsername() +
                " informou que o relato '" + MainActivity.selectedProblem.getName() + "' é " +
                (isVeridic ? "verídico!" : "falso!"));
        finish();
    }

    private void recalcVeracities(boolean isVeridic) {
        if (isVeridic) {
            currentProblem.incVeridicScore(UserDetail.getCurrentUserDetail().getReputation());
        } else {
            currentProblem.incFalseScore(UserDetail.getCurrentUserDetail().getReputation());
        }

        if (currentProblem.getVeridicScore() > MainActivity.VERACITY_INDICE) {
            currentProblem.setVeridicStatus(true);
            currentProblem.saveInBackground();
            updateEvaluatedUsersReputation(isVeridic);
        } else if (currentProblem.getFalseScore() > MainActivity.VERACITY_INDICE) {
            updateEvaluatedUsersReputation(isVeridic);
            try {
                currentProblem.delete();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateEvaluatedUsersReputation(final boolean isVeridic) {
        ParseQuery<Veracity> q = Veracity.getQuery();
        q.whereEqualTo("problem", currentProblem);
        q.findInBackground(new FindCallback<Veracity>() {
            public void done(List<Veracity> veracities, ParseException e) {
                for (Veracity v : veracities) {
                    if (v.getVeridic() == isVeridic) {
                        UserDetail.getUserDetailFor(v.getUser())
                                .incReputation(3);
                    } else {
                        UserDetail.getUserDetailFor(v.getUser())
                                .decReputation(5);
                    }
                }
            }
        });
    }

    public void updateFollowersReputation() {
        ParseQuery<ProblemFollow> q2 = ProblemFollow.getQuery();
        q2.whereEqualTo("problem", currentProblem);
        q2.findInBackground(new FindCallback<ProblemFollow>() {
            public void done(List<ProblemFollow> problemFollows, ParseException e) {
                for (ProblemFollow pf : problemFollows) {
                    UserDetail.getUserDetailFor(pf.getUser())
                            .incReputation(1);
                }
            }
        });
    }

    public void onDenounce(String comment) {
        Denounce denounce = new Denounce();
        denounce.setProblem(MainActivity.selectedProblem);
        denounce.setUser(ParseUser.getCurrentUser());
        denounce.setComment(comment);
        denounce.saveInBackground();

        followProblem();
        String problemName = MainActivity.selectedProblem.getName().length() > 15 ?
                MainActivity.selectedProblem.getName().substring(0, 15) : MainActivity.selectedProblem.getName();
        notifyAllFollowers("'" + problemName + "' foi denunciado! Comentário: " + comment);
    }
}
