package com.gh.crosig.model;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by henrique on 23/05/15.
 */
@ParseClassName("Denounce")
public class Denounce extends ParseObject {

    public static boolean existDenounce(Problem p, ParseUser u) {
        ParseQuery<Denounce> q = getQuery();
        q.whereEqualTo("user", u);
        q.whereEqualTo("problem", p);
        try {
            return q.count() > 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static ParseQuery<Denounce> getQuery() {
        return ParseQuery.getQuery(Denounce.class);
    }

    public String getComment() {
        return (String) get("comment");
    }

    public void setComment(String comment) {
        put("comment", comment);
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

    @Override
    public String toString() {
        return "Comment= " + getComment() + ", Problem=" + getProblem() + ", CreatedAt=" + getCreatedAt();
    }
}
