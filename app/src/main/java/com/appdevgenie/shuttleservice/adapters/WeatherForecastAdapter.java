package com.appdevgenie.shuttleservice.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appdevgenie.shuttleservice.R;
import com.appdevgenie.shuttleservice.model.WeatherInfo;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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


        //String date = weatherInfoList.get(itemPosition).getDate();
        long dateLong = weatherInfoList.get(itemPosition).getDateLong() * 1000;
        //String dateString = DateFormat.format("EEEE HH:mm", new Date(dateLong)).toString();

        //SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        //String dateString = simpleDateFormatDate.format(dateLong);

        SimpleDateFormat simpleDayFormat = new SimpleDateFormat(context.getString(R.string.date_format_weekday), Locale.getDefault());
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat(context.getString(R.string.date_format_time), Locale.getDefault());
        simpleTimeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String dayWeekString;
        if(DateUtils.isToday(dateLong)){
            dayWeekString = context.getString(R.string.today) + " " + simpleTimeFormat.format(dateLong);
        }else {
            dayWeekString = simpleDayFormat.format(dateLong) + " " + simpleTimeFormat.format(dateLong);
        }

        holder.ivIcon.setImageResource(getImage(weatherInfoList.get(itemPosition).getIcon()));
        //holder.tvDate.setText(date.substring(0, date.length() -3));
        holder.tvDate.setText(dayWeekString);
        holder.tvDescription.setText(weatherInfoList.get(itemPosition).getDescription());
        holder.tvHumidity
                .setText(TextUtils
                        .concat(String.valueOf(Math.round(weatherInfoList.get(itemPosition).getHumidity()))
                                , context.getString(R.string.humidity_percentage_symbol)));
        holder.tvTemp
                .setText(TextUtils
                        .concat(String.valueOf(Math.round(weatherInfoList.get(itemPosition).getTemp() - TEMP_KELVIN))
                        , context.getString(R.string.temperature_degree_symbol)));
    }

    private int getImage(String icon) {

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
                imageResource = (R.drawable.weather_mist_day);
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
