package com.appdevgenie.shuttleservice.utils;

import android.content.Context;

import com.appdevgenie.shuttleservice.R;
import com.appdevgenie.shuttleservice.model.MainGridIcon;

import java.util.ArrayList;
import java.util.Arrays;

public class CreateMainSignedOutArrayList {

    public static ArrayList<MainGridIcon> createMainLoggedOutIcons(Context context){

        ArrayList<MainGridIcon> mainGridIcons = new ArrayList<>();

        String[] iconNames = context.getResources().getStringArray(R.array.main_logged_out_list);
        ArrayList<String> iconName = new ArrayList<>(Arrays.asList(iconNames));

        Integer[] icons = {
                R.drawable.ic_lock,
                R.drawable.ic_route_stop,
                R.drawable.ic_price_list,
                R.drawable.ic_contact_info};
        ArrayList<Integer> icon = new ArrayList<>(Arrays.asList(icons));

        for (int i = 0; i < iconNames.length; i++) {
            mainGridIcons.add(new MainGridIcon(
                    icon.get(i),
                    iconName.get(i)));
        }

        return mainGridIcons;
    }
}
