package com.gh.crosig.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by henrique on 05/05/15.
 */
@ParseClassName("Problem")
public class Problem extends ParseObject {

    public static ParseQuery<Problem> getQuery() {
        return ParseQuery.getQuery(Problem.class);
    }

    public void setName(String name) {
        put("name", name);
    }

    public void setDesc(String desc) {
        put("desc", desc);
    }

    public void setType(String type) {
        put("type", type);
    }

    public void setImage(ParseFile image) {
        put("image", image);
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }

    public void setParseGeoPoint(ParseGeoPoint location) {
        put("location", location);
    }

    public void setStatus(String status) {
        put("status", status);
    }

    public String getName() {
        return (String)get("name");
    }

    public String getDesc() {
        return (String)get("desc");
    }

    public String getType() {
        return (String)get("type");
    }

    public ParseFile getImage() {
        if (containsKey("image")) {
            Object img = get("image");
            return img == null ? null : (ParseFile) img;
        }
        return null;
    }

    public ParseUser getUser() { return (ParseUser)get("user"); }

    public ParseGeoPoint getLocation() {
        return (ParseGeoPoint)get("location");
    }

    public String getStatus() {
        return (String) get("status");
    }

    public void incVeridicScore(Integer score) {
        if (score == null)
            score = 0;
        put("veridicScore", getVeridicScore() + score);
    }

    public int getVeridicScore() {
        Integer veridicScore = (Integer) get("veridicScore");
        if (veridicScore == null) {
            veridicScore = 0;
        }
        return veridicScore;
    }

    public void incFalseScore(Integer score) {
        if (score == null)
            score = 0;
        put("falseScore", getFalseScore() + score);
    }

    public int getFalseScore() {
        Integer falseScore = (Integer) get("falseScore");
        if (falseScore == null) {
            falseScore = 0;
        }
        return falseScore;
    }

    public void setVeridicStatus(Boolean veridicStatus) {
        put("veridicStatus", veridicStatus);
    }

    public Boolean getVeridicStatus() {
        return (Boolean) get("veridicStatus");
    }
}
