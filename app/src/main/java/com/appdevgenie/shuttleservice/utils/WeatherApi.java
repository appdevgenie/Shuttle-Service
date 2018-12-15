package com.appdevgenie.shuttleservice.utils;

import com.appdevgenie.shuttleservice.model.WeatherCurrentModel;
import com.appdevgenie.shuttleservice.model.WeatherForecastList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {

    @GET("data/2.5/weather?")
    Call<WeatherCurrentModel> getCurrentWeather(@Query("q") String cityCountryName, @Query("appid") String app_id);

    @GET("data/2.5/forecast?")
    Call<WeatherForecastList> getForecastWeather(@Query("q") String cityCountryName, @Query("appid") String app_id);

}
