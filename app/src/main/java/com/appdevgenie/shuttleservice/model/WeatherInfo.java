package com.appdevgenie.shuttleservice.model;

import com.google.gson.annotations.SerializedName;

public class WeatherInfo {

    @SerializedName("icon")
    private String icon;
    @SerializedName("dt_txt")
    private String date;
    @SerializedName("dt")
    private long dateLong;
    @SerializedName("description")
    private String description;
    @SerializedName("temp")
    private double temp;
    @SerializedName("humidity")
    private double humidity;
    @SerializedName("name")
    public String name;

    public WeatherInfo() {
    }

    public WeatherInfo(String icon, String date, long dateLong, String description, double temp, double humidity) {
        this.icon = icon;
        this.date = date;
        this.dateLong = dateLong;
        this.description = description;
        this.temp = temp;
        this.humidity = humidity;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getDateLong() {
        return dateLong;
    }

    public void setDateLong(long dateLong) {
        this.dateLong = dateLong;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }
}
