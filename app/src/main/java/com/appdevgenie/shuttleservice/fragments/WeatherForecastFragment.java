package com.appdevgenie.shuttleservice.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.appdevgenie.shuttleservice.R;
import com.appdevgenie.shuttleservice.adapters.WeatherForecastAdapter;
import com.appdevgenie.shuttleservice.model.WeatherInfo;
import com.appdevgenie.shuttleservice.model.WeatherInfoList;
import com.appdevgenie.shuttleservice.utils.NetworkUtils;
import com.appdevgenie.shuttleservice.utils.WeatherJsonUtils;

import java.net.URL;
import java.util.List;

import static com.appdevgenie.shuttleservice.utils.Constants.WEATHER_API_KEY;


public class WeatherForecastFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private View view;
    private Context context;
    private RecyclerView rvWeather;
    private WeatherForecastAdapter weatherForecastAdapter;
    private Spinner spSelectTown;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_weather_forcast, container, false);

        setupVariables();

        return view;

    }

    private void setupVariables() {

        context = getActivity();

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

        //String cityString = "Sabie,za";

        //new LoadWetherAsyncTask().execute(cityString);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        new LoadWetherAsyncTask().execute(spSelectTown.getSelectedItem().toString().trim() + ",za");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private class LoadWetherAsyncTask extends AsyncTask<String, Void, List<WeatherInfo>> {

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
