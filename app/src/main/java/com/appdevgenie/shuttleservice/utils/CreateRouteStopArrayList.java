package com.appdevgenie.shuttleservice.utils;

import android.content.Context;

import com.appdevgenie.shuttleservice.R;
import com.appdevgenie.shuttleservice.model.RouteStops;

import java.util.ArrayList;
import java.util.Arrays;

public class CreateRouteStopArrayList {

    public static ArrayList<RouteStops> createArrayList(Context context){

        ArrayList<RouteStops> routeStops = new ArrayList<>();

        String[] towns = context.getResources().getStringArray(R.array.route_stops_town_name);
        ArrayList<String> town = new ArrayList<>(Arrays.asList(towns));

        String[] codes = context.getResources().getStringArray(R.array.route_stops_town_code);
        ArrayList<String> code = new ArrayList<>(Arrays.asList(codes));

        String[] times = context.getResources().getStringArray(R.array.route_stops_time);
        ArrayList<String> time = new ArrayList<>(Arrays.asList(times));

        for (int i = 0; i < towns.length; i++) {
            routeStops.add(new RouteStops(
                    town.get(i),
                    code.get(i),
                    time.get(i)));
        }

        return routeStops;
    }
}
