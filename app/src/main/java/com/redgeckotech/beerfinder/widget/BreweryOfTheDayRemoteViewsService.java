package com.redgeckotech.beerfinder.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.redgeckotech.beerfinder.R;
import com.redgeckotech.beerfinder.data.Brewery;
import com.redgeckotech.beerfinder.data.BreweryColumns;
import com.redgeckotech.beerfinder.data.BreweryProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import timber.log.Timber;

public class BreweryOfTheDayRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor cursor = null;

            @Override
            public void onCreate() {
                onDataSetChanged();
            }

            @Override
            public void onDataSetChanged() {

                Timber.d("onDataSetChanged");

                if (cursor != null) {
                    cursor.close();
                }

                final long identityToken = Binder.clearCallingIdentity();

                long id = getRandomBreweryId();

                if (id > 0) {
                    cursor = getContentResolver().query(
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
                            BreweryColumns._ID + " = ?",
                            new String[]{Long.toString(id)},
                            null);
                }
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() { }

            @Override
            public int getCount() {
                return cursor == null ? 0 : cursor.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        cursor == null || !cursor.moveToPosition(position)) {
                    return null;
                }

                Brewery brewery = new Brewery(cursor);

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.list_item_widget);

                views.setTextViewText(R.id.brewery_name, brewery.getName());

                // Update color indicator for positive or negative change
//                if (cursor.getInt(cursor.getColumnIndex(QuoteColumns.ISUP)) == 1) {
//                    views.setInt(R.id.change,
//                            getResources().getString(R.string.set_background_resource),
//                            R.drawable.percent_change_pill_green);
//                } else {
//                    views.setInt(R.id.change,
//                            getResources().getString(R.string.set_background_resource),
//                            R.drawable.percent_change_pill_red);
//                }

                //String percentChange = cursor.getString(cursor.getColumnIndex(QuoteColumns.PERCENT_CHANGE));
//                String change = "1.2";//cursor.getString(cursor.getColumnIndex(QuoteColumns.CHANGE));
//                views.setTextViewText(R.id.change, change);//Utils.showPercent ? percentChange : change);

                final Intent intent = new Intent();
                intent.putExtra("brewery", brewery);
                views.setOnClickFillInIntent(R.id.brewery_name, intent);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return null;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (cursor != null && cursor.moveToPosition(position)) {
                    return cursor.getLong(cursor.getColumnIndex(BreweryColumns._ID));
                }
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

            long getRandomBreweryId() {
                Cursor cursor = null;
                long result = -1;

                try {
                    List<Long> allIds = new ArrayList<>();

                    cursor = getContentResolver().query(
                            BreweryProvider.Breweries.BREWERIES,
                            new String[]{
                                    BreweryColumns._ID
                            },
                            null,
                            null,
                            null);

                    if (cursor != null) {

                        while (cursor.moveToNext()) {
                            allIds.add(cursor.getLong(cursor.getColumnIndex(BreweryColumns._ID)));

                            cursor.moveToNext();
                        }
                    }

                    Timber.d("allIds.size(): %d", allIds.size());

                    if (allIds.size() > 0) {
                        Random random = new Random(System.currentTimeMillis());
                        int position = random.nextInt(allIds.size());
                        result = allIds.get(position);
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
        };
    }
}