package com.redgeckotech.beerfinder.widget;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.redgeckotech.beerfinder.BuildConfig;
import com.redgeckotech.beerfinder.R;
import com.redgeckotech.beerfinder.view.BreweryDetailActivity;
import com.redgeckotech.beerfinder.view.MainActivity;

import timber.log.Timber;

public class BreweryWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Timber.d("onUpdate called.");

        for (int appWidgetId : appWidgetIds) {
            Timber.d("appWidgetId: %d", appWidgetId);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list);

            // Create intent to launch MainActivity
            Intent titleClickIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, titleClickIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            views.setRemoteAdapter(R.id.brewery_listview,
                    new Intent(context, BreweryOfTheDayRemoteViewsService.class));

            // Set up collection items
            Intent breweryClickIntent = new Intent(context, BreweryDetailActivity.class);
            PendingIntent breweryClickPendingIntent = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(breweryClickIntent)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.brewery_listview, breweryClickPendingIntent);

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
