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
public interface VideoColunms {

    @DataType(INTEGER) @PrimaryKey
    @AutoIncrement
    String _ID = "_id";

    @DataType(TEXT) @NotNull
    String KEY = "key";

    @DataType(TEXT) @NotNull
    String NAME = "name";

    @DataType(TEXT) @NotNull
    String TYPE = "type";

    @DataType(TEXT) @NotNull
    String SITE = "site";

    @DataType(TEXT) @NotNull
    String TMDID = "tmdId";

    @DataType(TEXT) @NotNull
    String TMDMID = "tmdmId";

    @DataType(TEXT)
    String UPDATED = "updated";

}
