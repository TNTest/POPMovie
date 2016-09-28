package com.ynmiyou.popmovie.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by TNT on 16/9/17.
 */

@Database(version = MovieDatabase.VERSION)
public class MovieDatabase {

    private MovieDatabase() {
    }

    public static final int VERSION = 2;

    @Table(MovieColunms.class) public static final String MOVIES = "movies";

    @Table(ReviewColunms.class) public static final String REVIEWS = "reviews";

    @Table(VideoColunms.class) public static final String VIDEOS = "videos";

    @Table(FavoriteColunms.class) public static final String FAVORITES = "favorites";




    /*@OnCreate
    public static void onCreate(Context context, SQLiteDatabase db) {
    }

    @OnUpgrade
    public static void onUpgrade(Context context, SQLiteDatabase db, int oldVersion,
                                 int newVersion) {
    }

    @OnConfigure
    public static void onConfigure(SQLiteDatabase db) {
    }

    @ExecOnCreate
    public static final String EXEC_ON_CREATE = "SELECT * FROM " + MOVIES;*/
}
