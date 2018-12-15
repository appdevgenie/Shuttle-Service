package com.appdevgenie.shuttleservice.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class WeatherModel {

    @SerializedName("id")
    private Integer id;
    @SerializedName("name")
    private String name;
    @SerializedName("weather")
    private ArrayList<Weather> weather = new ArrayList<Weather>();
    @SerializedName("main")
    private Main main;
    @SerializedName("list")
    private List list;
    @SerializedName("dt")
    public long dt;
    @SerializedName("dt_txt")
    private String dtTxt;

    public ArrayList<Weather> getWeather() {
        return weather;
    }

    public Main getMain() {
        return main;
    }

    public List getList() {
        return list;
    }

    public class List {

        @SerializedName("dt")
        private long dt;
        @SerializedName("main")
        private Main main;
        @SerializedName("weather")
        private ArrayList<Weather> weather = null;
        @SerializedName("dt_txt")
        private String dtTxt;

        public long getDt() {
            return dt;
        }
    }

    public class Main {

        @SerializedName("temp")
        private Double temp;
        @SerializedName("temp_min")
        private Double tempMin;
        @SerializedName("temp_max")
        private Double tempMax;
        @SerializedName("pressure")
        private Double pressure;
        @SerializedName("sea_level")
        private Double seaLevel;
        @SerializedName("grnd_level")
        private Double grndLevel;
        @SerializedName("humidity")
        private Integer humidity;
        @SerializedName("temp_kf")
        private Integer tempKf;

        public Double getTemp() {
            return temp;
        }

        public Integer getHumidity() {
            return humidity;
        }
    }

    public class Weather {

        @SerializedName("id")
        private Integer id;
        @SerializedName("main")
        private String main;
        @SerializedName("description")
        private String description;
        @SerializedName("icon")
        private String icon;

        public String getDescription() {
            return description;
        }

        public String getIcon() {
            return icon;
        }
    }



}
