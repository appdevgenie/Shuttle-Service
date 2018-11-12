package com.appdevgenie.shuttleservice.model;

public class TravelInfo {

    private String townName;
    private String townCode;
    private String departureTime;
    private int totalPassengers;
    private int onPassengers;
    private int offPassengers;

    public TravelInfo() {
    }

    public TravelInfo(String townName, String townCode, String departureTime, int totalPassengers, int onPassengers, int offPassengers) {
        this.townName = townName;
        this.townCode = townCode;
        this.departureTime = departureTime;
        this.totalPassengers = totalPassengers;
        this.onPassengers = onPassengers;
        this.offPassengers = offPassengers;
    }

    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    public String getTownCode() {
        return townCode;
    }

    public void setTownCode(String townCode) {
        this.townCode = townCode;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public int getTotalPassengers() {
        return totalPassengers;
    }

    public void setTotalPassengers(int totalPassengers) {
        this.totalPassengers = totalPassengers;
    }

    public int getOnPassengers() {
        return onPassengers;
    }

    public void setOnPassengers(int onPassengers) {
        this.onPassengers = onPassengers;
    }

    public int getOffPassengers() {
        return offPassengers;
    }

    public void setOffPassengers(int offPassengers) {
        this.offPassengers = offPassengers;
    }
}
