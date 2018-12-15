package com.appdevgenie.shuttleservice.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class WeatherForecastList {

    @SerializedName("list")
    @Expose
    private ArrayList<WeatherForecastModel> weatherForecastModelArrayList = null;

    public ArrayList<WeatherForecastModel> getWeatherForecastModelArrayList() {
        return weatherForecastModelArrayList;
    }

}
