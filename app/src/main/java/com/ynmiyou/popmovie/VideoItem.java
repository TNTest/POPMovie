package com.ynmiyou.popmovie;

/**
 * Created by TNT on 16/9/27.
 */

public class VideoItem {

    private String key;
    private String name;
    private String type;
    private String tmdId;
    private String site;

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTmdId() {
        return tmdId;
    }

    public void setTmdId(String tmdId) {
        this.tmdId = tmdId;
    }

    @Override
    public String toString() {
        return "VideoItem{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", tmdId='" + tmdId + '\'' +
                '}';
    }
}