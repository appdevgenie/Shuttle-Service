package com.appdevgenie.shuttleservice.utils;

import com.appdevgenie.shuttleservice.R;

public class WeatherIconLoader {

    public static int getImage(String icon) {

        int imageResource = 0;

        switch (icon){
            case "01d"://clear day
                imageResource = (R.drawable.ic_weather_clear);
                break;

            case "02d"://few clouds day
                imageResource = (R.drawable.ic_weather_few_clouds);
                break;

            case "03d"://scatted clouds day
                imageResource = (R.drawable.ic_weather_overcast);
                break;

            case "04d"://broken clouds day
                imageResource = (R.drawable.ic_weather_overcast);
                break;

            case "09d"://shower rain day
                imageResource = (R.drawable.ic_weather_showers);
                break;

            case "10d"://rain day
                imageResource = (R.drawable.ic_weather_showers_scattered);
                break;

            case "11d"://storm day
                imageResource = (R.drawable.ic_weather_storm);
                break;

            case "13d"://snow day
                imageResource = (R.drawable.ic_weather_snow);
                break;

            case "50d"://mist day
                imageResource = (R.drawable.ic_weather_mist);
                break;

            case "01n"://clear night
                imageResource = (R.drawable.ic_weather_clear_night);
                break;

            case "02n"://few clouds night
                imageResource = (R.drawable.ic__weather_few_clouds_night);
                break;

            case "03n"://scatted clouds night
                imageResource = (R.drawable.ic_weather_overcast);
                break;

            case "04n"://broken clouds night
                imageResource = (R.drawable.ic_weather_overcast);
                break;

            case "09n"://shower rain night
                imageResource = (R.drawable.ic_weather_showers);
                break;

            case "10n"://rain night
                imageResource = (R.drawable.ic_weather_showers_scattered);
                break;

            case "11n"://thunderstorm night
                imageResource = (R.drawable.ic_weather_storm);
                break;

            case "13n"://snow night
                imageResource = (R.drawable.ic_weather_snow);
                break;

            case "50n"://mist night
                imageResource = (R.drawable.ic_weather_mist);
                break;
        }

        return imageResource;
    }

}
