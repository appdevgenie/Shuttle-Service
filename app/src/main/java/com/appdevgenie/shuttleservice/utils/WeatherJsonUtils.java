package com.appdevgenie.shuttleservice.utils;

import android.text.format.DateUtils;

import com.appdevgenie.shuttleservice.model.WeatherInfo;
import com.appdevgenie.shuttleservice.model.WeatherInfoList;
import com.appdevgenie.shuttleservice.model.WeatherTodayInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeatherJsonUtils {

    public static WeatherInfoList parseWeatherJson(String json) {

        /*String icon;
        String date;
        String description;
        double temp;
        double humidity;*/
        List<WeatherInfo> weatherInfoModelList = new ArrayList<>();
        WeatherInfoList weatherInfoList = new WeatherInfoList();

        try {

            JSONObject jsonObject = new JSONObject(json);
            //single info
            /*
            JSONObject mainObject = jsonObject.getJSONObject("main");
            WeatherInfo weatherInfo = new WeatherInfo(
                    0,
                    "",
                    "",
                    mainObject.getDouble("temp_min"),
                    mainObject.getDouble("temp_max")
            );
            weatherInfoModelList.add(weatherInfo);*/

            JSONArray jsonArray = jsonObject.getJSONArray("list");
            //JsonArray weatherArray = jsonObject1.getJSONArray("weather");
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObjectForecast = jsonArray.getJSONObject(i);
                JSONObject mainObject = jsonObjectForecast.getJSONObject("main");

                JSONArray weatherArray = jsonObjectForecast.getJSONArray("weather");
                JSONObject weatherObject = weatherArray.getJSONObject(0);

                /*icon = weatherObject.getString("icon");
                date = jsonObjectForecast.getString("dt_txt");
                description = weatherObject.getString("description");
                temp = mainObject.getDouble("temp");
                humidity = mainObject.getDouble("humidity");*/

                long dateLong = jsonObjectForecast.getLong("dt") * 1000;
                //ignore today`s weather
                if(!DateUtils.isToday(dateLong)) {

                    WeatherInfo weatherInfo = new WeatherInfo(
                            weatherObject.getString("icon"),
                            jsonObjectForecast.getString("dt_txt"),
                            jsonObjectForecast.getLong("dt"),
                            weatherObject.getString("description"),
                            mainObject.getDouble("temp"),
                            mainObject.getDouble("humidity")
                    );
                    weatherInfoModelList.add(weatherInfo);

                }
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
        weatherInfoList.setWeatherInfoList(weatherInfoModelList);
        return weatherInfoList;
    }

    public static WeatherTodayInfo parseWeatherTodayJson(String json) {

        WeatherTodayInfo weatherTodayInfo = new WeatherTodayInfo();

        try {

            JSONObject jsonObject = new JSONObject(json);
            JSONObject mainObject = jsonObject.getJSONObject("main");
            JSONArray weatherArray = jsonObject.getJSONArray("weather");
            JSONObject weatherObject = weatherArray.getJSONObject(0);

        weatherTodayInfo = new WeatherTodayInfo(
                weatherObject.getString("icon"),
                jsonObject.getLong("dt"),
                weatherObject.getString("description"),
                mainObject.getDouble("temp"),
                mainObject.getDouble("humidity")
        );

        }catch (JSONException e){
            e.printStackTrace();
        }

        return weatherTodayInfo;
    }
}
