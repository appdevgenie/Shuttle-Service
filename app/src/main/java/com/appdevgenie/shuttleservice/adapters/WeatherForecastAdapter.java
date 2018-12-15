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
import com.appdevgenie.shuttleservice.model.WeatherForecastModel;
import com.appdevgenie.shuttleservice.utils.WeatherIconLoader;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.appdevgenie.shuttleservice.utils.Constants.TEMP_KELVIN;

public class WeatherForecastAdapter extends RecyclerView.Adapter<WeatherForecastAdapter.WeatherViewHolder> {

    private Context context;
    private List<WeatherForecastModel> weatherInfoList;

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
        
        long dateLong = weatherInfoList.get(itemPosition).getDt() * 1000;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(context.getString(R.string.date_format_day_month_year_time), Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(context.getString(R.string.gmt_time_zome)));

        holder.ivIcon.setImageResource(WeatherIconLoader.getImage(weatherInfoList.get(itemPosition).getWeather().get(0).getIcon()));
        holder.tvDate.setText(simpleDateFormat.format(dateLong));
        //holder.tvDate.setText(weatherInfoList.get(itemPosition).getDtTxt());
        holder.tvDescription.setText(weatherInfoList.get(itemPosition).getWeather().get(0).getDescription());
        holder.tvHumidity
                .setText(TextUtils
                        .concat(String.valueOf(Math.round(weatherInfoList.get(itemPosition).getMain().getHumidity()))
                                , context.getString(R.string.humidity_percentage_symbol)));
        holder.tvTemp
                .setText(TextUtils
                        .concat(String.valueOf(Math.round(weatherInfoList.get(itemPosition).getMain().getTemp() - TEMP_KELVIN))
                                , context.getString(R.string.temperature_degree_symbol)));
    }

    @Override
    public int getItemCount() {

        if (weatherInfoList == null) {
            return 0;
        }
        return weatherInfoList.size();
    }

    public void setAdapterData(List<WeatherForecastModel> weatherInfo) {

        weatherInfoList = weatherInfo;

        for (int i = 0; i < weatherInfoList.size(); i++) {
            if(DateUtils.isToday(weatherInfoList.get(i).getDt() * 1000)) {
                weatherInfoList.remove(i);
            }
        }

        notifyDataSetChanged();
    }

    class WeatherViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivIcon;
        private TextView tvDate;
        private TextView tvDescription;
        private TextView tvTemp;
        private TextView tvHumidity;

        WeatherViewHolder(View itemView) {
            super(itemView);

            ivIcon = itemView.findViewById(R.id.ivWeatherIcon);
            tvDate = itemView.findViewById(R.id.tvWeatherDate);
            tvDescription = itemView.findViewById(R.id.tvWeatherDescription);
            tvTemp = itemView.findViewById(R.id.tvWeatherTemp);
            tvHumidity = itemView.findViewById(R.id.tvWeatherHumidity);
        }
    }
}
