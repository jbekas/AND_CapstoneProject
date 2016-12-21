package com.redgeckotech.beerfinder.data;


import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(version = BreweryDatabase.VERSION)
public final class BreweryDatabase {

    public static final int VERSION = 1;

    @Table(BreweryColumns.class) public static final String BREWERIES = "breweries";
}
