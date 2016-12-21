package com.redgeckotech.beerfinder.data;

import android.net.Uri;

import com.redgeckotech.beerfinder.BuildConfig;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(authority = BreweryProvider.AUTHORITY, database = BreweryDatabase.class)
public final class BreweryProvider {

    public static final String AUTHORITY = BuildConfig.APPLICATION_ID;

    @TableEndpoint(table = BreweryDatabase.BREWERIES)
    public static class Breweries {

        @ContentUri(
                path = "breweries",
                type = "vnd.android.cursor.dir/list",
                defaultSort = BreweryColumns.NAME + " ASC")
        public static final Uri BREWERIES = Uri.parse("content://" + AUTHORITY + "/breweries");
    }
}
