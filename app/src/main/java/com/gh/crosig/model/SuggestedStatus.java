package com.gh.crosig.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

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

}
