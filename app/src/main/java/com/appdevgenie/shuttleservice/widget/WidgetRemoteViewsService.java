package com.appdevgenie.shuttleservice.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.appdevgenie.shuttleservice.R;
import com.appdevgenie.shuttleservice.model.BookingInfo;

import java.util.ArrayList;
import java.util.List;

public class WidgetRemoteViewsService extends RemoteViewsService {


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetRemoteViewsItemFactory(getApplicationContext(), intent);
    }

    class WidgetRemoteViewsItemFactory implements RemoteViewsFactory {

        private Context context;
        private int appWidgetId;
        /*private List<BookingInfo> bookingInfoList = new ArrayList<>();
        private String testString;
        private String[] exampleData = {"one", "two", "three", "four",
                "five", "six", "seven", "eight", "nine", "ten"};*/


        public WidgetRemoteViewsItemFactory(Context context, Intent intent) {
            this.context = context;
            this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            //this.testString = intent.getStringExtra(EXTRA_STRING);
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {

            //return exampleData.length;
            /*if (bookingInfoList == null) {
                return 0;
            }
            return bookingInfoList.size();*/

            return 0;
        }

        @Override
        public RemoteViews getViewAt(int position) {

            //RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_item);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_shuttle_booking);
            //views.setTextViewText(R.id.tvWidgetDate, testString);

            //BookingInfo bookingInfo = bookingInfoList.get(position);
            //views.setTextViewText(R.id.example_widget_item_text, bookingInfo.getDate());
            //views.setTextViewText(R.id.tvWidgetDate, exampleData[position]);

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
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
