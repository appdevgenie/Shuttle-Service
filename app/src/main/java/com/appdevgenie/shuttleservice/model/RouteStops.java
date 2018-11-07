package com.appdevgenie.shuttleservice.model;

public class RouteStops {

    private String townName;
    private String townCode;
    private String stopTime;

    public RouteStops(String townName, String townCode, String stopTime) {
        this.townName = townName;
        this.townCode = townCode;
        this.stopTime = stopTime;
    }

    public String getTownName() {
        return townName;
    }

    public String getTownCode() {
        return townCode;
    }

    public String getStopTime() {
        return stopTime;
    }

}
