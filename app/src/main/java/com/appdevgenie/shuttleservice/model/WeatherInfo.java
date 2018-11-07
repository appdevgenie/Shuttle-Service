package com.appdevgenie.shuttleservice.model;

public class WeatherInfo {

    private String icon;
    private String date;
    private String description;
    private double temp;
    private double humidity;

    public WeatherInfo() {
    }

    public WeatherInfo(String icon, String date, String description, double temp, double humidity) {
        this.icon = icon;
        this.date = date;
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
