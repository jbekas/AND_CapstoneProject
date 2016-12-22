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
                BreweryColumns.PHONE,
                BreweryColumns.TAPROOM_HOURS,
                BreweryColumns.TOUR_HOURS,
                BreweryColumns.BEER_LIST,
                BreweryColumns.LOGO_URL,
                BreweryColumns.PHOTO_URL,
                BreweryColumns.TWITTER,
        };

        int _ID = 0;
        int NAME = 1;
        int ADDRESS = 2;
        int ADDRESS2 = 3;
        int CITY = 4;
        int STATE = 5;
        int POSTAL_CODE = 6;
        int PHONE = 7;
        int TAPROOM_HOURS = 8;
        int TOUR_HOURS = 9;
        int BEER_LIST = 10;
        int LOGO_URL = 11;
        int PHOTO_URL = 12;
        int TWITTER = 13;
    }
}


