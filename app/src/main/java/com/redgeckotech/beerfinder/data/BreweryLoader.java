package com.redgeckotech.beerfinder.data;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

public class BreweryLoader extends CursorLoader {
    public static BreweryLoader breweryInstance(Context context) {
        String sortOrder = BreweryColumns.NAME + " ASC";

        return new BreweryLoader(context, BreweryProvider.Breweries.BREWERIES, null, null, sortOrder);
    }

    private BreweryLoader(Context context, Uri uri, String selection, String[] selectionArgs, String sortOrder) {
        super(context, uri, Query.PROJECTION, selection, selectionArgs, sortOrder);
    }

    public interface Query {
        String[] PROJECTION = {
                BreweryColumns._ID,
                BreweryColumns.NAME,
                BreweryColumns.ADDRESS,
                BreweryColumns.ADDRESS2,
                BreweryColumns.CITY,
                BreweryColumns.STATE,
                BreweryColumns.POSTAL_CODE,
                BreweryColumns.LATITUDE,
                BreweryColumns.LONGITUDE,
                BreweryColumns.PHONE,
                BreweryColumns.TAPROOM_HOURS,
                BreweryColumns.TOUR_HOURS,
                BreweryColumns.BEER_LIST,
                BreweryColumns.LOGO_URL,
                BreweryColumns.PHOTO_URL,
                BreweryColumns.WEBSITE,
                BreweryColumns.TWITTER,
        };

        int _ID = 0;
        int NAME = 1;
        int ADDRESS = 2;
        int ADDRESS2 = 3;
        int CITY = 4;
        int STATE = 5;
        int POSTAL_CODE = 6;
        int LATITUDE = 7;
        int LONGITUDE = 8;
        int PHONE = 9;
        int TAPROOM_HOURS = 10;
        int TOUR_HOURS = 11;
        int BEER_LIST = 12;
        int LOGO_URL = 13;
        int PHOTO_URL = 14;
        int WEBSITE = 15;
        int TWITTER = 16;
    }
}


