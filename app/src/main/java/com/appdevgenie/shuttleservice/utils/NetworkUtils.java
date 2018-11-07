package com.appdevgenie.shuttleservice.utils;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static com.appdevgenie.shuttleservice.utils.Constants.WEATHER_API_QUERY_KEY;

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();
    //public static final String BASE_DAY_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    private static final String BASE_FORECAST_URL = "http://api.openweathermap.org/data/2.5/forecast?q=";

    public static URL buildWeatherUrl(String apiKeyString, String city) {

        Uri weatherQueryUri = Uri.parse(BASE_FORECAST_URL + city).buildUpon()
                .appendQueryParameter(WEATHER_API_QUERY_KEY, apiKeyString)
                .build();

        try {
            URL weatherUrl = new URL(weatherQueryUri.toString());
            Log.d(TAG, "buildWeatherUrl: " + weatherUrl);
            return weatherUrl;
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {

        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        try {

            InputStream inputStream = httpURLConnection.getInputStream();

            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;

        } finally {
            httpURLConnection.disconnect();
        }
    }

    /*public byte[] getImage(String iconCode) {

        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;

        try {
            httpURLConnection =
                    (HttpURLConnection) (new URL(BASE_IMAGE_URL + iconCode + ".png")).openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            while (inputStream.read(buffer) != -1)
                byteArrayOutputStream.write(buffer);

            return byteArrayOutputStream.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }*/
}
