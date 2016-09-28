package com.ynmiyou.popmovie.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;
/**
 * Created by TNT on 16/9/17.
 */
public interface MovieColunms {

    @DataType(INTEGER) @PrimaryKey
    @AutoIncrement
    String _ID = "_id";

    @DataType(TEXT) @NotNull
    String TITLE = "title";

    @DataType(TEXT) @NotNull
    String POSTERURL = "posterUrl";

    @DataType(TEXT) @NotNull
    String OVERVIEW = "overview";

    @DataType(TEXT) @NotNull
    String TMDID = "tmdId";

    @DataType(TEXT) @NotNull
    String VOTEAVERAGE = "voteAverage";

    @DataType(TEXT) @NotNull
    String VOTECOUNT = "voteCount";

    @DataType(TEXT) @NotNull
    String RELEASEDATE = "releaseDate";

    @DataType(TEXT)
    String UPDATED = "updated";
}
