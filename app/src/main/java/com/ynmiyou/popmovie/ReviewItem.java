package com.ynmiyou.popmovie;

/**
 * Created by TNT on 16/9/27.
 */

public class ReviewItem {

    private String content;
    private String url;
    private String author;
    private String tmdId;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTmdId() {
        return tmdId;
    }

    public void setTmdId(String tmdId) {
        this.tmdId = tmdId;
    }

    @Override
    public String toString() {
        return "ReviewItem{" +
                "content='" + content + '\'' +
                ", url='" + url + '\'' +
                ", author='" + author + '\'' +
                ", tmdId='" + tmdId + '\'' +
                '}';
    }
}