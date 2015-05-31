package com.gh.crosig.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by henrique on 25/05/15.
 */
@ParseClassName("Veracity")
public class Veracity extends ParseObject {

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

    public void setVeridic(boolean isVeridic) {
        put("veridic", isVeridic);
    }

    public Boolean getVeridic() {
        return (Boolean)get("veridic");
    }


    public static ParseQuery<Veracity> getQuery() {
        return ParseQuery.getQuery(Veracity.class);
    }
}
