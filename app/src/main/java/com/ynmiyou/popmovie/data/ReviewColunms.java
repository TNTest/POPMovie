package com.ynmiyou.popmovie.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

/**
 * Created by TNT on 16/9/17.
 */
public interface ReviewColunms {

    @DataType(INTEGER) @PrimaryKey
    @AutoIncrement
    String _ID = "_id";

    @DataType(TEXT) @NotNull
    String CONTENT = "content";

    @DataType(TEXT) @NotNull
    String AUTHOR = "author";

    @DataType(TEXT) @NotNull
    String URL = "url";

    @DataType(TEXT) @NotNull
    String TMDID = "tmdId";

    @DataType(TEXT) @NotNull
    String TMDMID = "tmdmId";

    @DataType(TEXT)
    String UPDATED = "updated";

}
