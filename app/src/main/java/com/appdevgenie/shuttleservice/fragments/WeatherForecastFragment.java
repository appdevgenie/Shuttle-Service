package com.appdevgenie.shuttleservice.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appdevgenie.shuttleservice.R;
import com.appdevgenie.shuttleservice.adapters.WeatherForecastAdapter;
import com.appdevgenie.shuttleservice.model.WeatherInfo;
import com.appdevgenie.shuttleservice.model.WeatherInfoList;
import com.appdevgenie.shuttleservice.model.WeatherTodayInfo;
import com.appdevgenie.shuttleservice.utils.CheckNetworkConnection;
import com.appdevgenie.shuttleservice.utils.NetworkUtils;
import com.appdevgenie.shuttleservice.utils.WeatherIconLoader;
import com.appdevgenie.shuttleservice.utils.WeatherJsonUtils;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.appdevgenie.shuttleservice.utils.Constants.TEMP_KELVIN;
import static com.appdevgenie.shuttleservice.utils.Constants.WEATHER_API_KEY;
import static com.appdevgenie.shuttleservice.utils.Constants.WEATHER_COUNTRY;


public class WeatherForecastFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private Context context;
    @BindView(R.id.rvWeather)
    RecyclerView rvWeather;
    private WeatherForecastAdapter weatherForecastAdapter;
    @BindView(R.id.spWeatherSelectTown)
    Spinner spSelectTown;
    @BindView(R.id.pbWeather)
    ProgressBar progressBar;
    @BindView(R.id.pbWeatherToday)
    ProgressBar progressBarToday;
    @BindView(R.id.tvWeatherTodayInfo)
    TextView tvTodayInfo;
    @BindView(R.id.tvWeatherTodayTemp)
    TextView tvTodayTemp;
    @BindView(R.id.tvWeatherTodayHumidity)
    TextView tvTodayHumidity;
    @BindView(R.id.ivWeatherToday)
    ImageView ivTodayIcon;
    @BindView(R.id.tvWeatherTodayHeader)
    TextView tvTodayTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather_forecast, container, false);
        ButterKnife.bind(this, view);
        setupVariables();
        return view;
    }

    private void setupVariables() {

        context = getActivity();
        AppCompatActivity appCompatActivity = (AppCompatActivity)getActivity();
        if (appCompatActivity != null) {
            appCompatActivity.getSupportActionBar().setTitle(R.string.weather);
        }

        ArrayAdapter<CharSequence> spFromAdapter =
                ArrayAdapter.createFromResource(context, R.array.town_names_weather, R.layout.spinner_item);
        spFromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSelectTown.setAdapter(spFromAdapter);
        spSelectTown.setOnItemSelectedListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        weatherForecastAdapter = new WeatherForecastAdapter(context);
        rvWeather.setLayoutManager(linearLayoutManager);
        rvWeather.setHasFixedSize(true);
        rvWeather.setAdapter(weatherForecastAdapter);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(CheckNetworkConnection.isNetworkConnected(context)) {
            new LoadWeatherTodayAsyncTask().execute(spSelectTown.getSelectedItem().toString().trim() + WEATHER_COUNTRY);
            new LoadWeatherForecastAsyncTask().execute(spSelectTown.getSelectedItem().toString().trim() + WEATHER_COUNTRY);
        }else{
            Toast.makeText(context, getString(R.string.not_connected_to_network), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @SuppressLint("StaticFieldLeak")
    private class LoadWeatherTodayAsyncTask extends AsyncTask<String, Void, WeatherTodayInfo> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarToday.setVisibility(View.VISIBLE);
        }

        @Override
        protected WeatherTodayInfo doInBackground(String... strings) {

            URL url = NetworkUtils.buildWeatherTodayUrl(WEATHER_API_KEY, strings[0]);

            try {
                String jsonString = null;
                if (url != null) {
                    jsonString = NetworkUtils.getResponseFromHttpUrl(url);
                }
                return WeatherJsonUtils.parseWeatherTodayJson(jsonString);

            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(WeatherTodayInfo weatherTodayInfo) {
            super.onPostExecute(weatherTodayInfo);

            progressBarToday.setVisibility(View.GONE);

            if (weatherTodayInfo != null) {
                long dateLong = weatherTodayInfo.getDateLong() * 1000;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format_time), Locale.getDefault());
                //simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                String timeString = context.getString(R.string.today) + ", " + simpleDateFormat.format(dateLong);
                tvTodayTime.setText(timeString);

                tvTodayInfo.setText(weatherTodayInfo.getDescription());
                tvTodayTemp.setText(TextUtils
                        .concat(String.valueOf(Math.round(weatherTodayInfo.getTemp() - TEMP_KELVIN))
                                , context.getString(R.string.temperature_degree_symbol)));
                tvTodayHumidity.setText(TextUtils
                        .concat(String.valueOf(Math.round(weatherTodayInfo.getHumidity()))
                                , context.getString(R.string.humidity_percentage_symbol)));
                ivTodayIcon.setImageResource(WeatherIconLoader.getImage(weatherTodayInfo.getIcon()));
            } else {
                Toast.makeText(context, R.string.error_today_weather, Toast.LENGTH_SHORT).show();
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class LoadWeatherForecastAsyncTask extends AsyncTask<String, Void, List<WeatherInfo>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<WeatherInfo> doInBackground(String... strings) {

            URL url = NetworkUtils.buildWeatherUrl(WEATHER_API_KEY, strings[0]);

            try {
                String jsonString = null;
                if (url != null) {
                    jsonString = NetworkUtils.getResponseFromHttpUrl(url);
                }
                WeatherInfoList weatherInfoList = WeatherJsonUtils.parseWeatherJson(jsonString);
                return weatherInfoList.getWeatherInfoList();

            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<WeatherInfo> weatherInfoDetails) {
            progressBar.setVisibility(View.GONE);
            if (weatherInfoDetails != null) {
                weatherForecastAdapter.setAdapterData(weatherInfoDetails);
            } else {
                Toast.makeText(context, R.string.error_loading_weather, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
