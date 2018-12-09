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

import static com.appdevgenie.shuttleservice.utils.Constants.JSON_ARRAY_LIST;
import static com.appdevgenie.shuttleservice.utils.Constants.JSON_ARRAY_WEATHER;
import static com.appdevgenie.shuttleservice.utils.Constants.JSON_ITEM_DATE_LONG;
import static com.appdevgenie.shuttleservice.utils.Constants.JSON_ITEM_DATE_TEXT;
import static com.appdevgenie.shuttleservice.utils.Constants.JSON_ITEM_DESCRIPTION;
import static com.appdevgenie.shuttleservice.utils.Constants.JSON_ITEM_HUMIDITY;
import static com.appdevgenie.shuttleservice.utils.Constants.JSON_ITEM_ICON;
import static com.appdevgenie.shuttleservice.utils.Constants.JSON_ITEM_TEMP;
import static com.appdevgenie.shuttleservice.utils.Constants.JSON_OBJECT_MAIN;

public class WeatherJsonUtils {

    public static WeatherInfoList parseWeatherJson(String json) {

        List<WeatherInfo> weatherInfoModelList = new ArrayList<>();
        WeatherInfoList weatherInfoList = new WeatherInfoList();

        try {

            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray(JSON_ARRAY_LIST);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObjectForecast = jsonArray.getJSONObject(i);
                JSONObject mainObject = jsonObjectForecast.getJSONObject(JSON_OBJECT_MAIN);

                JSONArray weatherArray = jsonObjectForecast.getJSONArray(JSON_ARRAY_WEATHER);
                JSONObject weatherObject = weatherArray.getJSONObject(0);

                long dateLong = jsonObjectForecast.getLong(JSON_ITEM_DATE_LONG) * 1000;
                //ignore today`s weather
                if(!DateUtils.isToday(dateLong)) {

                    WeatherInfo weatherInfo = new WeatherInfo(
                            weatherObject.getString(JSON_ITEM_ICON),
                            jsonObjectForecast.getString(JSON_ITEM_DATE_TEXT),
                            jsonObjectForecast.getLong(JSON_ITEM_DATE_LONG),
                            weatherObject.getString(JSON_ITEM_DESCRIPTION),
                            mainObject.getDouble(JSON_ITEM_TEMP),
                            mainObject.getDouble(JSON_ITEM_HUMIDITY)
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
            JSONObject mainObject = jsonObject.getJSONObject(JSON_OBJECT_MAIN);
            JSONArray weatherArray = jsonObject.getJSONArray(JSON_ARRAY_WEATHER);
            JSONObject weatherObject = weatherArray.getJSONObject(0);

        weatherTodayInfo = new WeatherTodayInfo(
                weatherObject.getString(JSON_ITEM_ICON),
                jsonObject.getLong(JSON_ITEM_DATE_LONG),
                weatherObject.getString(JSON_ITEM_DESCRIPTION),
                mainObject.getDouble(JSON_ITEM_TEMP),
                mainObject.getDouble(JSON_ITEM_HUMIDITY)
        );

        }catch (JSONException e){
            e.printStackTrace();
        }

        return weatherTodayInfo;
    }
}
