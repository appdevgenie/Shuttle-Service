package com.appdevgenie.shuttleservice.fragments;

import android.content.Context;
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
import com.appdevgenie.shuttleservice.model.WeatherCurrentModel;
import com.appdevgenie.shuttleservice.model.WeatherForecastList;
import com.appdevgenie.shuttleservice.model.WeatherForecastModel;
import com.appdevgenie.shuttleservice.utils.CheckNetworkConnection;
import com.appdevgenie.shuttleservice.utils.WeatherApi;
import com.appdevgenie.shuttleservice.utils.WeatherIconLoader;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.appdevgenie.shuttleservice.utils.Constants.TEMP_KELVIN;
import static com.appdevgenie.shuttleservice.utils.Constants.WEATHER_API_KEY;
import static com.appdevgenie.shuttleservice.utils.Constants.WEATHER_BASE_URL;
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
            loadCurrentWeather(spSelectTown.getSelectedItem().toString().trim() + WEATHER_COUNTRY);
            loadForecastWeather(spSelectTown.getSelectedItem().toString().trim() + WEATHER_COUNTRY);
        }else{
            Toast.makeText(context, getString(R.string.not_connected_to_network), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void loadCurrentWeather(String s) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WEATHER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        progressBarToday.setVisibility(View.VISIBLE);

        WeatherApi weatherApi = retrofit.create(WeatherApi.class);

        Call<WeatherCurrentModel> weatherModelCall = weatherApi.getCurrentWeather(s, WEATHER_API_KEY);

        weatherModelCall.enqueue(new Callback<WeatherCurrentModel>() {
            @Override
            public void onResponse(@NonNull Call<WeatherCurrentModel> call, @NonNull Response<WeatherCurrentModel> response) {

                progressBarToday.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    WeatherCurrentModel weatherTodayInfo = response.body();

                    if (weatherTodayInfo != null) {
                        long dateLong = weatherTodayInfo.getDt() * 1000;
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format_time), Locale.getDefault());
                        //simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                        String timeString = context.getString(R.string.today) + ", " + simpleDateFormat.format(dateLong);
                        tvTodayTime.setText(timeString);

                        tvTodayInfo.setText(String.valueOf(weatherTodayInfo.getWeather().get(0).getDescription()));
                        tvTodayTemp.setText(TextUtils
                                .concat(String.valueOf(Math.round(weatherTodayInfo.getMain().getTemp() - TEMP_KELVIN))
                                        , context.getString(R.string.temperature_degree_symbol)));
                        tvTodayHumidity.setText(TextUtils
                                .concat(String.valueOf(Math.round(weatherTodayInfo.getMain().getHumidity()))
                                        , context.getString(R.string.humidity_percentage_symbol)));
                        ivTodayIcon.setImageResource(WeatherIconLoader.getImage(weatherTodayInfo.getWeather().get(0).getIcon()));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherCurrentModel> call, @NonNull Throwable t) {
                progressBarToday.setVisibility(View.GONE);
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadForecastWeather(String s) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WEATHER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        progressBar.setVisibility(View.VISIBLE);

        WeatherApi weatherApi = retrofit.create(WeatherApi.class);
        Call<WeatherForecastList> arrayListCall = weatherApi.getForecastWeather(s, WEATHER_API_KEY);

        arrayListCall.enqueue(new Callback<WeatherForecastList>() {
            @Override
            public void onResponse(@NonNull Call<WeatherForecastList> call, @NonNull Response<WeatherForecastList> response) {

                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {

                    if (response.body() != null) {
                        List<WeatherForecastModel> weatherForecastModelArrayList = response.body().getWeatherForecastModelArrayList();
                        weatherForecastAdapter.setAdapterData(weatherForecastModelArrayList);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherForecastList> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
