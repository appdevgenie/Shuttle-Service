package com.appdevgenie.shuttleservice.model;

public class BookingInfo {

    private String date;
    private String fromTown;
    private String toTown;
    private String fromTownCode;
    private String toTownCode;
    private String seats;
    private String cost;
    private int viewType;

    public BookingInfo() {
    }

    public BookingInfo
            (String date, String fromTown, String toTown, String fromTownCode, String toTownCode, String seats, String cost, int viewType) {

        this.date = date;
        this.fromTown = fromTown;
        this.toTown = toTown;
        this.fromTownCode = fromTownCode;
        this.toTownCode = toTownCode;
        this.seats = seats;
        this.cost = cost;
        this.viewType = viewType;
    }

    public String getDate() {
        return date;
    }

    public String getFromTown() {
        return fromTown;
    }

    public String getToTown() {
        return toTown;
    }

    public String getFromTownCode() {
        return fromTownCode;
    }

    public String getToTownCode() {
        return toTownCode;
    }

    public String getSeats() {
        return seats;
    }

    public String getCost() {
        return cost;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}
