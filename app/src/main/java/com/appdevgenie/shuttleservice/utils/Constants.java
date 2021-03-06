package com.appdevgenie.shuttleservice.utils;

import com.appdevgenie.shuttleservice.BuildConfig;

public class Constants {

    public static final String FIRESTORE_USER_COLLECTION = "users";
    //public static final String FIRESTORE_BOOKINGS_COLLECTION = "bookings";
    public static final String FIRESTORE_TRAVEL_INFO_COLLECTION = "travelInfo";
    public static final String FIRESTORE_BOOKING_DATE_FIELD = "bookingDate";
    public static final String FIRESTORE_USER_EMAIL_FIELD = "userEmail";
    public static final String FIRESTORE_TRAVEL_DATE_FIELD = "date";
    public static final String FIRESTORE_USER_IMAGE_CHILD = "user.jpg";

    public static final int DOWNSTREAM_DIFFERENCE = 17;
    public static final int SHUTTLE_MAX = 30;

    public static final int DIRECTION_UPSTREAM = 0;
    public static final int DIRECTION_DOWNSTREAM = 1;

    public static final String WEATHER_BASE_URL = "http://api.openweathermap.org/";
    public static final String WEATHER_API_KEY = BuildConfig.WEATHER_API_KEY;
    public static final String WEATHER_COUNTRY = ",za";

    static final String WEATHER_API_QUERY_KEY = "appid";
    public static final double TEMP_KELVIN = 273.15;

    static final String JSON_ARRAY_LIST = "list";
    static final String JSON_OBJECT_MAIN = "main";
    static final String JSON_ARRAY_WEATHER = "weather";
    static final String JSON_ITEM_DATE_LONG = "dt";
    static final String JSON_ITEM_DATE_TEXT = "dt_txt";
    static final String JSON_ITEM_ICON = "icon";
    static final String JSON_ITEM_DESCRIPTION = "description";
    static final String JSON_ITEM_TEMP = "temp";
    static final String JSON_ITEM_HUMIDITY = "humidity";

    public static final String USER_ADMIN = "appdevgenie@gmail.com";
    public static final double HOP_COST = 65.25;

    public static final int VIEW_TYPE_BOOKING_SELECTED = 1;
    public static final int VIEW_TYPE_BOOKING_DEFAULT = 0;
    public static final int VIEW_TYPE_NEXT_STOP = 1;
    public static final int VIEW_TYPE_DEFAULT = 0;

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
    public static final String BUNDLE_IS_SIGNED_IN = "isSignedIn";
    public static final String BUNDLE_CLICKED_ICON = "clickedIconPosition";
    public static final String BUNDLE_IS_DUAL_PANE = "dualPane";
    public static final String BUNDLE_RECEIVER_DESTINATION = "destination";
    public static final String BUNDLE_MAX_SEATS = "maxSeats";

    public static final String SAVED_DUAL_PANE = "dualPane";
    public static final String SAVED_SELECTED_ICON = "selectedPosition";
    //public static final String SAVED_FROM_SPINNER = "fromSpinner";
    //public static final String SAVED_TO_SPINNER = "toSpinner";
    public static final String SAVED_DATE = "date";
    public static final String SAVED_SEATS = "seats";
    public static final String SAVED_SEATS_ARRAY = "seatsArray";

    public static final int THIRTY_MINUTES = 30 * 60 * 1000;

    public static final int RC_SIGN_IN = 155;

    public static final int SELECT_IMAGE_REQUEST = 101;

    public static final int UPDATE_INTERVAL = 1000;

}
