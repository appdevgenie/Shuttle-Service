package com.appdevgenie.shuttleservice.utils;

import android.content.Context;

import com.appdevgenie.shuttleservice.R;
import com.appdevgenie.shuttleservice.model.MainGridIcon;

import java.util.ArrayList;
import java.util.Arrays;

public class CreateCommuterSignedInArrayList {

    public static ArrayList<MainGridIcon> createCommuterSignedInIcons(Context context){

        ArrayList<MainGridIcon> mainGridIcons = new ArrayList<>();

        String[] iconNames = context.getResources().getStringArray(R.array.commuter_logged_in_list);
        ArrayList<String> iconName = new ArrayList<>(Arrays.asList(iconNames));

        Integer[] icons = {
                R.drawable.ic_lock,
                R.drawable.ic_route_stop,
                R.drawable.ic_price_list,
                R.drawable.ic_contact_info,
                R.drawable.ic_check_availibility,
                R.drawable.ic_make_booking,
                R.drawable.ic_booking_history_list,
                R.drawable.ic_weather_clear,
                R.drawable.ic_account};
        ArrayList<Integer> icon = new ArrayList<>(Arrays.asList(icons));

        for (int i = 0; i < iconNames.length; i++) {
            mainGridIcons.add(new MainGridIcon(
                    icon.get(i),
                    iconName.get(i)));
        }

        return mainGridIcons;
    }
}
