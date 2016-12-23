package com.redgeckotech.beerfinder.widget;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.redgeckotech.beerfinder.R;
import com.redgeckotech.beerfinder.data.Brewery;
import com.redgeckotech.beerfinder.utils.BreweryUtils;
import com.redgeckotech.beerfinder.view.BreweryDetailActivity;
import com.redgeckotech.beerfinder.view.MainActivity;

import timber.log.Timber;

public class BreweryWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

            // Create intent to launch MainActivity
            Intent titleClickIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, titleClickIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            Brewery brewery = BreweryUtils.getRandomBrewery(context);

            if (brewery != null) {
                views.setTextViewText(R.id.brewery_name, brewery.getName());

                Intent breweryClickIntent = new Intent(context, BreweryDetailActivity.class);
                breweryClickIntent.putExtra("brewery", brewery);
                PendingIntent breweryClickPendingIntent = TaskStackBuilder.create(context)
                        .addNextIntentWithParentStack(breweryClickIntent)
                        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                views.setOnClickPendingIntent(R.id.brewery_name, breweryClickPendingIntent);
                views.setPendingIntentTemplate(R.id.brewery_name, breweryClickPendingIntent);
            }

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.d("Received intent");
        //Toast.makeText(context, "Intent Detected.", Toast.LENGTH_LONG).show();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName name = new ComponentName(context.getPackageName(), BreweryWidgetProvider.class.getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(name);

        onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
