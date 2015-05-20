package com.gh.crosig.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;

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
        return (ParseFile) get("image");
    }

    public ParseGeoPoint getLocation() {
        return (ParseGeoPoint)get("location");
    }

}
