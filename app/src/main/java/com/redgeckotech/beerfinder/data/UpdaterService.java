package com.redgeckotech.beerfinder.data;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.redgeckotech.beerfinder.BreweryInfoApplication;
import com.redgeckotech.beerfinder.BuildConfig;
import com.redgeckotech.beerfinder.R;
import com.redgeckotech.beerfinder.api.BreweryApi;
import com.redgeckotech.beerfinder.widget.BreweryOfTheDayRemoteViewsService;
import com.redgeckotech.beerfinder.widget.BreweryWidgetProvider;

import java.util.ArrayList;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static java.net.HttpURLConnection.HTTP_OK;

public class UpdaterService extends IntentService {

    public static final String BROADCAST_ACTION_STATE_CHANGE
            = BuildConfig.APPLICATION_ID + ".intent.action.STATE_CHANGE";
    public static final String EXTRA_REFRESHING
            = BuildConfig.APPLICATION_ID + ".intent.extra.REFRESHING";

    @Inject BreweryApi breweryApi;

    public UpdaterService() {
        super(UpdaterService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // Don't even inspect the intent, we only do one thing, and that's fetch content.

        ((BreweryInfoApplication) getApplicationContext()).getApplicationComponent().inject(this);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isConnected()) {
            Timber.w("Not online, not refreshing.");
            return;
        }

        sendStickyBroadcast(
                new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, true));

        Observable<Response<ResponseBody>> observable = breweryApi.retrieveAustinBreweries();

        observable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Response<ResponseBody>>() {
                    @Override
                    public void onCompleted() {
                        Timber.d("onCompleted.");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, null);
                    }

                    @Override
                    public void onNext(Response<ResponseBody> response) {
                        Timber.d("Received response.");
                        try {
                            if (response.code() == HTTP_OK) {
                                String body = response.body().string();

                                Timber.d("received: %s", body);

                                processJson(body);
                            } else {
                                Timber.e("Error retrieving brewery data.");
                            }

                        } catch (Exception e) {
                            Timber.e(e, null);
                        }
                    }
                });
    }

    void processJson(String json) {

        ArrayList<ContentProviderOperation> cpo = new ArrayList<ContentProviderOperation>();

        Uri dirUri = BreweryProvider.Breweries.BREWERIES;

        // Delete all items
        cpo.add(ContentProviderOperation.newDelete(dirUri).build());

        try {
            Brewery[] breweries = new Gson().fromJson(json, Brewery[].class);

            for (Brewery brewery : breweries) {
                Timber.d(brewery.toString());

                ContentValues values = new ContentValues();
                values.put(BreweryColumns._ID, brewery.getId());
                values.put(BreweryColumns.NAME, brewery.getName());
                values.put(BreweryColumns.ADDRESS, brewery.getAddress());
                values.put(BreweryColumns.ADDRESS2, brewery.getAddress2());
                values.put(BreweryColumns.CITY, brewery.getCity());
                values.put(BreweryColumns.STATE, brewery.getState());
                values.put(BreweryColumns.POSTAL_CODE, brewery.getPostalCode());
                values.put(BreweryColumns.LATITUDE, brewery.getLatitude());
                values.put(BreweryColumns.LONGITUDE, brewery.getLongitude());
                values.put(BreweryColumns.PHONE, brewery.getPhone());
                values.put(BreweryColumns.TAPROOM_HOURS, brewery.getTaproomHours());
                values.put(BreweryColumns.TOUR_HOURS, brewery.getTourHours());
                values.put(BreweryColumns.BEER_LIST, brewery.getBeerListAsJsonString());
                values.put(BreweryColumns.LOGO_URL, brewery.getLogoUrl());
                values.put(BreweryColumns.PHOTO_URL, brewery.getPhotoUrl());
                values.put(BreweryColumns.WEBSITE, brewery.getWebsite());
                values.put(BreweryColumns.TWITTER, brewery.getTwitter());
                cpo.add(ContentProviderOperation.newInsert(dirUri).withValues(values).build());
            }

            getContentResolver().applyBatch(BreweryProvider.AUTHORITY, cpo);

            Intent intent = new Intent(this, BreweryWidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
            // since it seems the onUpdate() is only fired on that:
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            ComponentName name = new ComponentName(BuildConfig.APPLICATION_ID, BreweryWidgetProvider.class.getName());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(name);

            //int[] ids = {widgetId};
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,appWidgetIds);
            sendBroadcast(intent);

            /*
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.widget_list);

            remoteViews.setRemoteAdapter(R.id.brewery_listview,
                    new Intent(this, BreweryOfTheDayRemoteViewsService.class));

            ComponentName thisWidget = new ComponentName(this, BreweryWidgetProvider.class);
            remoteViews.setTextViewText(R.id.my_text_view, "myText" + System.currentTimeMillis());
            appWidgetManager.updateAppWidget(thisWidget, remoteViews);
            */
        } catch (Exception e) {
            Timber.e(e, "Error updating content.");
        }

//        Intent intent = new Intent();
//        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
//        sendBroadcast(intent);

        sendStickyBroadcast(
                new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, false));
    }
}
