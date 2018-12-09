package com.appdevgenie.shuttleservice.utils;

import android.content.Context;
import android.text.TextUtils;

import com.appdevgenie.shuttleservice.R;
import com.appdevgenie.shuttleservice.model.BookingInfo;
import com.appdevgenie.shuttleservice.model.TravelInfo;

import java.util.ArrayList;
import java.util.Arrays;

public class CreateTravelInfoArrayList {

    public static ArrayList<TravelInfo> createTravelInfoList(Context context, ArrayList<BookingInfo> bookingInfoArrayList){

        ArrayList<TravelInfo> travelInfoArrayList = new ArrayList<>();

        int totalPassengers = 0;

        String[] towns = context.getResources().getStringArray(R.array.route_stops_town_name);
        ArrayList<String> town = new ArrayList<>(Arrays.asList(towns));

        String[] codes = context.getResources().getStringArray(R.array.route_stops_town_code);
        ArrayList<String> code = new ArrayList<>(Arrays.asList(codes));

        String[] times = context.getResources().getStringArray(R.array.route_stops_time);
        ArrayList<String> time = new ArrayList<>(Arrays.asList(times));

        for (int i = 0; i < towns.length; i++) {

            String townString = town.get(i);
            String timeString = time.get(i);
            int passengersOn = 0;
            int passengersOff = 0;

            for (int j = 0; j < bookingInfoArrayList.size(); j++) {
                if(TextUtils.equals(bookingInfoArrayList.get(j).getFromTown(), townString) &&
                        TextUtils.equals(bookingInfoArrayList.get(j).getDepartureTime(), timeString)){
                    totalPassengers = totalPassengers + bookingInfoArrayList.get(j).getSeats();

                    passengersOn = passengersOn + bookingInfoArrayList.get(j).getSeats();

                }
                if(TextUtils.equals(bookingInfoArrayList.get(j).getToTown(), townString) &&
                        TextUtils.equals(bookingInfoArrayList.get(j).getArrivalTime(), timeString)){
                    totalPassengers = totalPassengers - bookingInfoArrayList.get(j).getSeats();

                    passengersOff = passengersOff - bookingInfoArrayList.get(j).getSeats();
                }
            }

            travelInfoArrayList.add(new TravelInfo(
                    townString,
                    code.get(i),
                    timeString,
                    totalPassengers,
                    passengersOn,
                    Math.abs(passengersOff)));
        }

        return travelInfoArrayList;
    }

    public static ArrayList<Integer> createPassengerMaxList(Context context, ArrayList<BookingInfo> bookingInfoArrayList) {

        ArrayList<Integer> maxList = new ArrayList<>();

        int totalPassengers = 0;

        String[] towns = context.getResources().getStringArray(R.array.route_stops_town_name);
        ArrayList<String> town = new ArrayList<>(Arrays.asList(towns));

        String[] times = context.getResources().getStringArray(R.array.route_stops_time);
        ArrayList<String> time = new ArrayList<>(Arrays.asList(times));

        for (int i = 0; i < towns.length; i++) {

            String townString = town.get(i);
            String timeString = time.get(i);

            for (int j = 0; j < bookingInfoArrayList.size(); j++) {
                if (TextUtils.equals(bookingInfoArrayList.get(j).getFromTown(), townString) &&
                        TextUtils.equals(bookingInfoArrayList.get(j).getDepartureTime(), timeString)) {
                    totalPassengers = totalPassengers + bookingInfoArrayList.get(j).getSeats();

                }
                if (TextUtils.equals(bookingInfoArrayList.get(j).getToTown(), townString) &&
                        TextUtils.equals(bookingInfoArrayList.get(j).getArrivalTime(), timeString)) {
                    totalPassengers = totalPassengers - bookingInfoArrayList.get(j).getSeats();

                }
            }

            maxList.add(totalPassengers);
        }
        return maxList;
    }
}
