package com.gh.crosig.model;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by henrique on 22/05/15.
 */
@ParseClassName("ProblemFollow")
public class ProblemFollow extends ParseObject {

    public static ProblemFollow newInstance(Problem problem, ParseUser parseUser) {
        ProblemFollow f = new ProblemFollow();
        f.setProblem(problem);
        f.setUser(parseUser);
        return f;
    }

    public static ParseQuery<ProblemFollow> getQuery() {
        return ParseQuery.getQuery(ProblemFollow.class);
    }

    public static boolean isFollowingProblem(Problem currentProblem, ParseUser currentUser) {
        ParseQuery<ProblemFollow> q = getQuery();
        q.whereEqualTo("problem", currentProblem);
        q.whereEqualTo("user", currentUser);

        try {
            return q.count() > 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Problem getProblem() {
        return (Problem) get("problem");
    }

    public void setProblem(Problem problem) {
        put("problem", problem);
    }

    public ParseUser getUser() {
        return (ParseUser) get("user");
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }
}
