package com.ynmiyou.popmovie;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by TNT on 16/8/29.
 */
public class MovieItem implements Parcelable {
    private String posterUrl;
    private String title;
    private String overview;
    private String tmdId;
    private String voteAverage;
    private String voteCount;

    public String getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(String voteCount) {
        this.voteCount = voteCount;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }


    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getTmdId() {
        return tmdId;
    }

    public void setTmdId(String tmdId) {
        this.tmdId = tmdId;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    private String releaseDate;

    public MovieItem(){
    }

    public MovieItem(String posterUrl, String title) {
        super();
        this.posterUrl = posterUrl;
        this.title = title;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "MovieItem{" +
                "posterUrl='" + posterUrl + '\'' +
                ", title='" + title + '\'' +
                ", overview='" + overview + '\'' +
                ", tmdId='" + tmdId + '\'' +
                ", voteAverage='" + voteAverage + '\'' +
                ", voteCount='" + voteCount + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.posterUrl);
        dest.writeString(this.title);
        dest.writeString(this.overview);
        dest.writeString(this.tmdId);
        dest.writeString(this.voteAverage);
        dest.writeString(this.voteCount);
        dest.writeString(this.releaseDate);
    }

    protected MovieItem(Parcel in) {
        this.posterUrl = in.readString();
        this.title = in.readString();
        this.overview = in.readString();
        this.tmdId = in.readString();
        this.voteAverage = in.readString();
        this.voteCount = in.readString();
        this.releaseDate = in.readString();
    }

    public static final Creator<MovieItem> CREATOR = new Creator<MovieItem>() {
        @Override
        public MovieItem createFromParcel(Parcel source) {
            return new MovieItem(source);
        }

        @Override
        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };
}