package com.appdevgenie.shuttleservice.model;

public class WeatherTodayInfo {

    private String icon;
    private long dateLong;
    private String description;
    private double temp;
    private double humidity;

    public WeatherTodayInfo() {
    }

    public WeatherTodayInfo(String icon, long dateLong, String description, double temp, double humidity) {
        this.icon = icon;
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
