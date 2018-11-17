package com.appdevgenie.shuttleservice.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.appdevgenie.shuttleservice.utils.NetworkUtils;
import com.appdevgenie.shuttleservice.utils.WeatherIconLoader;
import com.appdevgenie.shuttleservice.utils.WeatherJsonUtils;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.appdevgenie.shuttleservice.utils.Constants.TEMP_KELVIN;
import static com.appdevgenie.shuttleservice.utils.Constants.WEATHER_API_KEY;


public class WeatherForecastFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private View view;
    private Context context;
    private RecyclerView rvWeather;
    private WeatherForecastAdapter weatherForecastAdapter;
    private Spinner spSelectTown;
    private ProgressBar progressBar;
    private ProgressBar progressBarToday;
    private TextView tvTodayInfo;
    private TextView tvTodayTemp;
    private TextView tvTodayHumidity;
    private ImageView ivTodayIcon;
    private TextView tvTodayTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_weather_forecast, container, false);

        setupVariables();

        return view;

    }

    private void setupVariables() {

        context = getActivity();

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        AppCompatActivity appCompatActivity = (AppCompatActivity)getActivity();
        appCompatActivity.setSupportActionBar(toolbar);
        appCompatActivity.getSupportActionBar().setTitle("Weather");
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        spSelectTown = view.findViewById(R.id.spWeatherSelectTown);
        ArrayAdapter<CharSequence> spFromAdapter =
                ArrayAdapter.createFromResource(context, R.array.town_names_weather, R.layout.spinner_weather_item);
        spFromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSelectTown.setAdapter(spFromAdapter);
        spSelectTown.setOnItemSelectedListener(this);

        rvWeather = view.findViewById(R.id.rvWeather);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        weatherForecastAdapter = new WeatherForecastAdapter(context);
        rvWeather.setLayoutManager(linearLayoutManager);
        rvWeather.setHasFixedSize(true);
        rvWeather.setAdapter(weatherForecastAdapter);

        progressBar = view.findViewById(R.id.pbWeather);
        progressBarToday = view.findViewById(R.id.pbWeatherToday);

        tvTodayInfo = view.findViewById(R.id.tvWeatherTodayInfo);
        tvTodayTemp = view.findViewById(R.id.tvWeatherTodayTemp);
        tvTodayHumidity = view.findViewById(R.id.tvWeatherTodayHumidity);
        ivTodayIcon = view.findViewById(R.id.ivWeatherToday);
        tvTodayTime = view.findViewById(R.id.tvWeatherTodayHeader);

        //String cityString = "Sabie,za";

        //new LoadWeatherForecastAsyncTask().execute(cityString);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        new LoadWeatherTodayAsyncTask().execute(spSelectTown.getSelectedItem().toString().trim() + ",za");
        new LoadWeatherForecastAsyncTask().execute(spSelectTown.getSelectedItem().toString().trim() + ",za");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class LoadWeatherTodayAsyncTask extends AsyncTask<String, Void, WeatherTodayInfo>{

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

            if(weatherTodayInfo != null) {
                long dateLong = weatherTodayInfo.getDateLong() * 1000;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
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
            }else{
                Toast.makeText(context, "Error loading today`s weather", Toast.LENGTH_SHORT).show();
            }
        }
    }


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
                Toast.makeText(context, "Error loading weather", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
