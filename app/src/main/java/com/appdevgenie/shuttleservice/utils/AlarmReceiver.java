package com.appdevgenie.shuttleservice.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import static com.appdevgenie.shuttleservice.utils.Constants.BUNDLE_RECEIVER_DESTINATION;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            NotificationUtils.remindCommuterOfTrip(context, bundle.getString(BUNDLE_RECEIVER_DESTINATION));
        }
    }
}
