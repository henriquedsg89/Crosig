package com.gh.crosig.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by henrique on 05/05/15.
 */
@ParseClassName("UserNotification")
public class UserNotification extends ParseObject {

    public static ParseQuery<UserNotification> getQuery() {
        return ParseQuery.getQuery(UserNotification.class);
    }

    public void setTo(ParseUser user) {
        put("to", user);
    }

    public ParseUser getTo() {
        return (ParseUser)get("to");
    }

    public void setMsg(String msg) {
        put("msg", msg);
    }

    public String getMsg() {
        return (String)get("msg");
    }

    public void setFrom(ParseUser from) {
        put("from", from);
    }

    public ParseUser getFrom() {
        return (ParseUser)get("from");
    }

    public void setProblem(Problem p) {
        put("problem", p);
    }

    public Problem getProblem() {
        return (Problem) get("problem");
    }
}
