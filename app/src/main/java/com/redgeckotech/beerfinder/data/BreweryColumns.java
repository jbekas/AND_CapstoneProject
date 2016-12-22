package com.redgeckotech.beerfinder.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.REAL;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public interface BreweryColumns {

    @DataType(INTEGER)
    @PrimaryKey
    @AutoIncrement
    String _ID = "_id";

    @DataType(TEXT)
    @NotNull
    String NAME = "name";

    @DataType(TEXT)
    String ADDRESS = "address";

    @DataType(TEXT)
    String ADDRESS2 = "address2";

    @DataType(TEXT)
    String CITY = "city";

    @DataType(TEXT)
    String STATE = "state";

    @DataType(TEXT)
    String POSTAL_CODE = "postal_code";

    @DataType(REAL)
    String LATITUDE = "latitude";

    @DataType(REAL)
    String LONGITUDE = "longitude";

    @DataType(TEXT)
    String PHONE = "phone";

    @DataType(TEXT)
    String TAPROOM_HOURS = "taproom_hours";

    @DataType(TEXT)
    String TOUR_HOURS = "tour_hours";

    @DataType(TEXT)
    String BEER_LIST = "beer_list";

    @DataType(TEXT)
    String LOGO_URL = "logo_url";

    @DataType(TEXT)
    String PHOTO_URL = "photo_url";

    @DataType(TEXT)
    String WEBSITE = "website";

    @DataType(TEXT)
    String TWITTER = "twitter";
}
