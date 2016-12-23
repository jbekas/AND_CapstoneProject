package com.redgeckotech.beerfinder.utils;

import android.content.Context;
import android.database.Cursor;

import com.redgeckotech.beerfinder.data.Brewery;
import com.redgeckotech.beerfinder.data.BreweryColumns;
import com.redgeckotech.beerfinder.data.BreweryProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import timber.log.Timber;

public class BreweryUtils {

    public static Brewery getRandomBrewery(Context context) {
            Cursor cursor = null;
            Brewery result = null;

            try {
                List<Brewery> breweries = new ArrayList<>();

                cursor = context.getContentResolver().query(
                        BreweryProvider.Breweries.BREWERIES,
                        new String[]{
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
                        },
                        null,
                        null,
                        null);

                if (cursor != null) {

                    while (cursor.moveToNext()) {
                        Brewery brewery = new Brewery(cursor);
                        breweries.add(brewery);

                        cursor.moveToNext();
                    }
                }

                Timber.d("breweries.size(): %d", breweries.size());

                if (breweries.size() > 0) {
                    Random random = new Random(System.currentTimeMillis());
                    int position = random.nextInt(breweries.size());
                    result = breweries.get(position);
                }
            } catch (Exception e) {
                Timber.e(e, null);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            return result;
    }
}
