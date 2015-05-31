package com.gh.crosig.model;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by henrique on 25/05/15.
 */
@ParseClassName("UserDetail")
public class UserDetail extends ParseObject {

    private static int currentUserReputation;

    public static UserDetail newInstance(ParseUser user) {
        UserDetail userDetail = new UserDetail();
        userDetail.setUser(user);
        userDetail.put("reputation", 50);
        return userDetail;
    }

    public static boolean existsDetail(ParseUser user) {
        ParseQuery<UserDetail> q = UserDetail.getQuery();
        q.whereEqualTo("user", user);
        try {
            return q.count() > 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static UserDetail getCurrentUserDetail() {
        ParseQuery<UserDetail> q = getQuery();
        q.whereEqualTo("user", ParseUser.getCurrentUser());
        try {
            return q.find().get(0);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }

    public ParseUser getUser() {
        return (ParseUser)get("user");
    }

    public UserDetail incReputation(int reputation) {
        put("reputation", getReputation() + reputation);
        return this;
    }

    public UserDetail decReputation(int reputation) {
        put("reputation", getReputation() - reputation);
        return this;
    }

    public Integer getReputation() {
        return (Integer)get("reputation");
    }

    public static ParseQuery<UserDetail> getQuery() {
        return ParseQuery.getQuery(UserDetail.class);
    }

    public static UserDetail getUserDetailFor(ParseUser user) {
        ParseQuery<UserDetail> q = getQuery();
        q.whereEqualTo("user", user);
        try {
            List<UserDetail> list = q.find();
            if (list.isEmpty()) {
                return newInstance(user);
            } else {
                return list.get(0);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
