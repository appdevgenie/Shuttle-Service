package com.appdevgenie.shuttleservice.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.appdevgenie.shuttleservice.R;
import com.appdevgenie.shuttleservice.activities.MainActivity;
import com.appdevgenie.shuttleservice.utils.Constants;

import static com.appdevgenie.shuttleservice.utils.Constants.SHARED_PREFS_ARRIVE_TIME;
import static com.appdevgenie.shuttleservice.utils.Constants.SHARED_PREFS_DATE;
import static com.appdevgenie.shuttleservice.utils.Constants.SHARED_PREFS_DEPART_TIME;
import static com.appdevgenie.shuttleservice.utils.Constants.SHARED_PREFS_FROM_TOWN;
import static com.appdevgenie.shuttleservice.utils.Constants.SHARED_PREFS_SEATS;
import static com.appdevgenie.shuttleservice.utils.Constants.SHARED_PREFS_TO_TOWN;

/**
 * Implementation of App Widget functionality.
 */
public class ShuttleAppWidgetProvider extends AppWidgetProvider {

    //public static final String EXTRA_STRING = "com.appdevgenie.shuttleservice.widget.EXTRA_STRING";

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFS, 0);

        String seats = prefs.getString(SHARED_PREFS_SEATS, "") + " " + context.getResources().getString(R.string.seats);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_shuttle_booking);
        views.setTextViewText(R.id.tvWidgetDate, prefs.getString(SHARED_PREFS_DATE, ""));
        views.setTextViewText(R.id.tvWidgetFrom, prefs.getString(SHARED_PREFS_FROM_TOWN, ""));
        views.setTextViewText(R.id.tvWidgetTo, prefs.getString(SHARED_PREFS_TO_TOWN, ""));
        views.setTextViewText(R.id.tvWidgetFromTime, prefs.getString(SHARED_PREFS_DEPART_TIME, ""));
        views.setTextViewText(R.id.tvWidgetToTime, prefs.getString(SHARED_PREFS_ARRIVE_TIME, ""));
        views.setTextViewText(R.id.tvWidgetSeats, seats);

        views.setEmptyView(R.id.llWidgetInfoLayout, R.id.widgetEmptyView);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.ivWidgetLogo, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

