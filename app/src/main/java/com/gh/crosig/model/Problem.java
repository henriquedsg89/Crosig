package com.gh.crosig.model;

import android.graphics.Bitmap;

/**
 * Created by henrique on 05/05/15.
 */
public class Problem {

    private final String name;
    private final String desc;
    private final String type;
    private final Bitmap image;
    private final double lon;
    private final double lat;

    public Problem(String name, String desc, String type, Bitmap image, double lon, double lat) {
        this.name = name;
        this.desc = desc;
        this.type = type;
        this.image = image;
        this.lon = lon;
        this.lat = lat;
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

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    @Override
    public String toString() {
        return "Problem{" +
                "name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", type='" + type + '\'' +
                ", image=" + image +
                ", lon=" + lon +
                ", lat=" + lat +
                '}';
    }
}
