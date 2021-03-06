package com.gh.crosig.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by henrique on 05/05/15.
 */
@ParseClassName("SuggestedStatus")
public class SuggestedStatus extends ParseObject {


    public static ParseQuery<SuggestedStatus> getQuery() {
        return ParseQuery.getQuery(SuggestedStatus.class);
    }

    public void setProblem(Problem problem) {
        put("problem", problem);
    }

    public Problem getProblem() {
        return (Problem)get("problem");
    }

    public void setStatus(String status) {
        put("status", status);
    }

    public String getStatus() {
        return (String)get("status");
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }

    public ParseUser getUser() {
        return (ParseUser)get("user");
    }
}
