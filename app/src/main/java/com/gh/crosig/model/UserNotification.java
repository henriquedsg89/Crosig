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


    private ParseUser from;

    public static ParseQuery<UserNotification> getQuery() {
        return ParseQuery.getQuery(UserNotification.class);
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }

    public ParseUser getUser() {
        return (ParseUser)get("user");
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
}
