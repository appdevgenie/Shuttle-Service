package com.appdevgenie.shuttleservice.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class WeatherCurrentModel {

    @SerializedName("weather")
    @Expose
    private ArrayList<Weather> weather = null;
    @SerializedName("main")
    @Expose
    private Main main;
    @SerializedName("dt")
    @Expose
    private long dt;

    public ArrayList<Weather> getWeather() {
        return weather;
    }

    public Main getMain() {
        return main;
    }

    public long getDt() {
        return dt;
    }

    public class Main {

        @SerializedName("temp")
        @Expose
        private double temp;
        @SerializedName("humidity")
        @Expose
        private int humidity;

        public double getTemp() {
            return temp;
        }

        public int getHumidity() {
            return humidity;
        }
    }

    public class Weather {

        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("icon")
        @Expose
        private String icon;


        public String getDescription() {
            return description;
        }

        public String getIcon() {
            return icon;
        }
    }
}
