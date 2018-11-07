package com.appdevgenie.shuttleservice.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appdevgenie.shuttleservice.R;
import com.appdevgenie.shuttleservice.model.WeatherInfo;

import java.util.List;

import static com.appdevgenie.shuttleservice.utils.Constants.TEMP_KELVIN;

public class WeatherForecastAdapter extends RecyclerView.Adapter<WeatherForecastAdapter.WeatherViewHolder> {

    private Context context;
    private List<WeatherInfo> weatherInfoList;

    public WeatherForecastAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public WeatherForecastAdapter.WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_weather_forecast, parent, false);

        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherForecastAdapter.WeatherViewHolder holder, int position) {

        int itemPosition = holder.getAdapterPosition();

        String date = weatherInfoList.get(itemPosition).getDate();

        holder.ivIcon.setImageResource(getImage(weatherInfoList.get(itemPosition).getIcon()));
        holder.tvDate.setText(date.substring(0, date.length() -3));
        holder.tvDescription.setText(weatherInfoList.get(itemPosition).getDescription());
        holder.tvHumidity.setText(String.valueOf(Math.round(weatherInfoList.get(itemPosition).getHumidity())) + "\u0025");
        holder.tvTemp.setText(String.valueOf(Math.round(weatherInfoList.get(itemPosition).getTemp() - TEMP_KELVIN)) + "\u00b0");
    }

    private int getImage(String icon) {

        int imageResource = 0;

        switch (icon){
            case "01d": 
                imageResource = (R.drawable.weather_clear_day);
                break;
                
            case "02d":
                imageResource = (R.drawable.weather_few_clouds_day);
                break;
                
            case "03d":
                imageResource = (R.drawable.weather_scat_clouds_day);
                break;

            case "04d":
                imageResource = (R.drawable.weather_broke_clouds_day);
                break;

            case "09d":
                imageResource = (R.drawable.weather_shower_rain_day);
                break;

            case "10d":
                imageResource = (R.drawable.weather_rain_day);
                break;

            case "11d":
                imageResource = (R.drawable.weather_thunderstorm_day);
                break;

            case "13d":
                imageResource = (R.drawable.weather_snow_day);
                break;

            case "50d":
                imageResource = (R.drawable.weather_mist_day);
                break;

            case "01n":
                imageResource = (R.drawable.weather_clear_night);
                break;

            case "02n":
                imageResource = (R.drawable.weather_few_clouds_night);
                break;

            case "03n":
                imageResource = (R.drawable.weather_scat_clouds_night);
                break;

            case "04n":
                imageResource = (R.drawable.weather_broke_clouds_night);
                break;

            case "09n":
                imageResource = (R.drawable.weather_shower_rain_night);
                break;

            case "10n":
                imageResource = (R.drawable.weather_rain_night);
                break;

            case "11n":
                imageResource = (R.drawable.weather_thunderstorm_night);
                break;

            case "13n":
                imageResource = (R.drawable.weather_snow_night);
                break;

            case "50n":
                imageResource = (R.drawable.weather_mist_night);
                break;
        }

        return imageResource;
    }

    @Override
    public int getItemCount() {

        if(weatherInfoList == null){
            return 0;
        }
        return weatherInfoList.size();
    }

    public void setAdapterData(List<WeatherInfo> weatherInfo){
        weatherInfoList = weatherInfo;
        notifyDataSetChanged();
    }

    public class WeatherViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivIcon;
        private TextView tvDate;
        private TextView tvDescription;
        private TextView tvTemp;
        private TextView tvHumidity;

        public WeatherViewHolder(View itemView) {
            super(itemView);

            ivIcon = itemView.findViewById(R.id.ivWeatherIcon);
            tvDate = itemView.findViewById(R.id.tvWeatherDate);
            tvDescription = itemView.findViewById(R.id.tvWeatherDescription);
            tvTemp = itemView.findViewById(R.id.tvWeatherTemp);
            tvHumidity = itemView.findViewById(R.id.tvWeatherHumidity);
        }
    }
}
