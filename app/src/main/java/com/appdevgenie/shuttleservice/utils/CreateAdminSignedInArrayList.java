package com.appdevgenie.shuttleservice.utils;

import android.content.Context;

import com.appdevgenie.shuttleservice.R;
import com.appdevgenie.shuttleservice.model.MainGridIcon;

import java.util.ArrayList;
import java.util.Arrays;

public class CreateAdminSignedInArrayList {

    public static ArrayList<MainGridIcon> createAdminSignedInIcons(Context context){

        ArrayList<MainGridIcon> mainGridIcons = new ArrayList<>();

        String[] iconNames = context.getResources().getStringArray(R.array.admin_logged_in_list);
        ArrayList<String> iconName = new ArrayList<>(Arrays.asList(iconNames));

        Integer[] icons = {
                R.drawable.ic_login,
                R.drawable.ic_location,
                R.drawable.ic_price,
                R.drawable.ic_contact_us,
                R.drawable.ic_booking_availibility,
                R.drawable.ic_route_stops,
                R.drawable.ic_booking_history,
                R.drawable.ic_weather,
                R.drawable.ic_account,
                R.drawable.ic_travel_info,
                R.drawable.ic_users
        };
        ArrayList<Integer> icon = new ArrayList<>(Arrays.asList(icons));

        for (int i = 0; i < iconNames.length; i++) {
            mainGridIcons.add(new MainGridIcon(
                    icon.get(i),
                    iconName.get(i)));
        }

        return mainGridIcons;
    }
}
