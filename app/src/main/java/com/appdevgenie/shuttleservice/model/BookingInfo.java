package com.appdevgenie.shuttleservice.model;

public class BookingInfo {

    private String date;
    private String fromTown;
    private String toTown;
    private String departureTime;
    private String arrivalTime;
    private String fromTownCode;
    private String toTownCode;
    private String seats;
    private String cost;
    private int viewType;

    public BookingInfo() {
    }

    public BookingInfo(
            String date,
            String fromTown,
            String toTown,
            String departureTime,
            String arrivalTime,
            String fromTownCode,
            String toTownCode,
            String seats,
            String cost,
            int viewType) {

        this.date = date;
        this.fromTown = fromTown;
        this.toTown = toTown;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.fromTownCode = fromTownCode;
        this.toTownCode = toTownCode;
        this.seats = seats;
        this.cost = cost;
        this.viewType = viewType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFromTown() {
        return fromTown;
    }

    public void setFromTown(String fromTown) {
        this.fromTown = fromTown;
    }

    public String getToTown() {
        return toTown;
    }

    public void setToTown(String toTown) {
        this.toTown = toTown;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getFromTownCode() {
        return fromTownCode;
    }

    public void setFromTownCode(String fromTownCode) {
        this.fromTownCode = fromTownCode;
    }

    public String getToTownCode() {
        return toTownCode;
    }

    public void setToTownCode(String toTownCode) {
        this.toTownCode = toTownCode;
    }

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}
