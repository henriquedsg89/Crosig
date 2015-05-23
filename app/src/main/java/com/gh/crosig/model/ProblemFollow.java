package com.gh.crosig.model;

import com.parse.ParseClassName;
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

    public void setProblem(Problem problem) {
        put("problem", problem);
    }

    public Problem getProblem() {
        return (Problem)get("problem");
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }

    public ParseUser getUser() {
        return (ParseUser)get("user");
    }

    public static ParseQuery<ProblemFollow> getQuery() {
        return ParseQuery.getQuery(ProblemFollow.class);
    }
}
