package com.ynmiyou.popmovie.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by TNT on 16/9/17.
 */
@ContentProvider(authority = MoviesContentProvider.AUTHORITY, database = MovieDatabase.class)
public class MoviesContentProvider {

    //public static final String AUTHORITY = Resources.getSystem().getString(R.string.content_authority);
    public static final String AUTHORITY = "com.ynmiyou.popmovie.app";

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = Uri.parse("content://" + AUTHORITY).buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }


    @TableEndpoint(table = MovieDatabase.MOVIES) public static class Movies {

        @ContentUri(
                path = "moviesp",
                type = "vnd.android.cursor.dir/movie",
                defaultSort = MovieColunms._ID + " ASC")
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/moviesp");

        @ContentUri(
                path = "moviesv",
                type = "vnd.android.cursor.dir/movie",
                defaultSort = MovieColunms.VOTEAVERAGE + " DESC")
        public static final Uri moviesv = Uri.parse("content://" + AUTHORITY + "/moviesv");

        @ContentUri(
                path = "moviesf",
                type = "vnd.android.cursor.dir/movie",
                join = "inner join " + MovieDatabase.FAVORITES + " on " + MovieColunms.TMDID + " = " + FavoriteColunms.TMDMID
                )
        public static final Uri moviesf = Uri.parse("content://" + AUTHORITY + "/moviesf");

    }

    @TableEndpoint(table = MovieDatabase.VIDEOS) public static class Videos {
        @ContentUri(
                path = "videos",
                type = "vnd.android.cursor.dir/video",
                defaultSort = VideoColunms._ID + " ASC")
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/videos");
    }

    @TableEndpoint(table = MovieDatabase.REVIEWS) public static class Reviews {

        @ContentUri(
                path = "reviews",
                type = "vnd.android.cursor.dir/review",
                defaultSort = ReviewColunms._ID + " ASC")
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/reviews");

    }

    @TableEndpoint(table = MovieDatabase.FAVORITES) public static class Favorites {

        @InexactContentUri(
                path = "favorites" + "/#",
                name = "FAVORITE_ID",
                whereColumn = FavoriteColunms.TMDMID,
                pathSegment = 1,
                type = "vnd.android.cursor.item/favorite",
                defaultSort = ReviewColunms._ID + " ASC")
        public static Uri withTmdmId(String id) {
            return buildUri("favorites", id);
        }

        @ContentUri(
                path = "favorites",
                type = "vnd.android.cursor.dir/favorite",
                defaultSort = FavoriteColunms._ID + " ASC")
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites");

    }
}
