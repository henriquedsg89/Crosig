package com.gh.crosig.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by henrique on 23/05/15.
 */
@ParseClassName("Comment")
public class Comment extends ParseObject {

    public static ParseQuery<Comment> getQuery() {
        return ParseQuery.getQuery(Comment.class);
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
