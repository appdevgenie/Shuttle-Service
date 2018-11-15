package com.appdevgenie.shuttleservice.utils;

import com.appdevgenie.shuttleservice.BuildConfig;

public class Constants {

    public static final String FIRESTORE_USER_COLLECTION = "users";
    //public static final String FIRESTORE_BOOKINGS_COLLECTION = "bookings";
    public static final String FIRESTORE_TRAVEL_INFO_COLLECTION = "travelInfo";
    public static final String FIRESTORE_BOOKING_DATE_FIELD = "bookingDate";
    public static final String FIRESTORE_USER_EMAIL_FIELD = "userEmail";

    public static final String WEATHER_API_KEY = BuildConfig.WEATHER_API_KEY;

    public static final String WEATHER_API_QUERY_KEY = "appid";
    public static final double TEMP_KELVIN = 273.15;

    public static final String USER_ADMIN = "appdevgenie@gmail.com";
    public static final double HOP_COST = 65.25;
    public static final String EXTRA_PARSE_IS_SIGNED_IN = "isSignedIn";
    public static final String EXTRA_PARSE_CLICKED_ICON = "clickedIconPosition";

    public static final int VIEW_TYPE_BOOKING_SELECTED = 1;
    public static final int VIEW_TYPE_BOOKING_DEFAULT = 0;

    public static final String SHARED_PREFS = "sharedPrefs.com.appdevgenie.shuttleservice";
    public static final String SHARED_PREFS_FROM_TOWN = "fromTown";
    public static final String SHARED_PREFS_TO_TOWN = "toTown";
    public static final String SHARED_PREFS_DEPART_TIME = "fromTime";
    public static final String SHARED_PREFS_ARRIVE_TIME = "toTime";
    public static final String SHARED_PREFS_DATE = "date";
    public static final String SHARED_PREFS_SEATS = "seats";

    public static final String BUNDLE_FROM_SPINNER = "fromSpinner";
    public static final String BUNDLE_TO_SPINNER = "toSpinner";
    public static final String BUNDLE_TRIP_DATE = "tripDate";

}
