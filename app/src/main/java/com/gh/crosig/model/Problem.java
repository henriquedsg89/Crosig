package com.gh.crosig.model;

import android.graphics.Bitmap;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by henrique on 05/05/15.
 */
@ParseClassName("Posts")
public class Problem extends ParseObject {

    private String name;
    private String desc;
    private String type;
    private Bitmap image;
    private ParseGeoPoint parseGeoPoint;

    public Problem() {
    }

    public static ParseQuery<Problem> getQuery() {
        return ParseQuery.getQuery(Problem.class);
    }

    public Problem name(String name) {
        this.name = name;
        return this;
    }

    public Problem desc(String desc) {
        this.desc = desc;
        return this;
    }

    public Problem type(String type) {
        this.type = type;
        return this;
    }

    public Problem image(Bitmap image) {
        this.image = image;
        return this;
    }

    public Problem location(ParseGeoPoint geoPoint) {
        this.parseGeoPoint = geoPoint;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getType() {
        return type;
    }

    public Bitmap getImage() {
        return image;
    }

    public ParseGeoPoint getLocation() {
        return parseGeoPoint;
    }

    @Override
    public String toString() {
        return "Problem{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", type='" + type + '\'' +
                ", image=" + image +
                ", parseGeoPoint=" + parseGeoPoint +
                '}';
    }
}
