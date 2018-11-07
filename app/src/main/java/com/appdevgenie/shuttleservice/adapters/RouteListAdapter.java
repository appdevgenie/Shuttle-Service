package com.appdevgenie.shuttleservice.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appdevgenie.shuttleservice.R;
import com.appdevgenie.shuttleservice.model.RouteStops;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RouteListAdapter extends RecyclerView.Adapter<RouteListAdapter.RouteViewHolder> {

    private static final int VIEW_TYPE_NEXT_STOP = 1;
    private static final int VIEW_TYPE_DEFAULT = 0;

    private Context context;
    private ArrayList<RouteStops> routeStops;
    private boolean isNextStop;

    public RouteListAdapter(Context context, ArrayList<RouteStops> routeStops) {
        this.context = context;
        this.routeStops = routeStops;
    }

    @NonNull
    @Override
    public RouteListAdapter.RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        int layoutId;

        switch (viewType) {

            case VIEW_TYPE_NEXT_STOP:
                layoutId = R.layout.list_item_route_next_stop;
                break;

            case VIEW_TYPE_DEFAULT:
                layoutId = R.layout.list_item_route;
                break;

            default:
                throw new IllegalArgumentException("Invalid view type: " + viewType);
        }


        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        view.setFocusable(true);
        return new RouteViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {

        ArrayList<String> time = new ArrayList<>
                (Arrays.asList(context.getResources().getStringArray(R.array.route_stops_time)));

        RouteStops route = routeStops.get(position);
        String nowStop = route.getStopTime();

        if ((nowStop.compareTo(getCurrentTime()) >= 0) &&
                (nowStop.compareTo(getNearestTime(time, getCurrentTime())) <= 0)) {
            isNextStop = true;
            return VIEW_TYPE_NEXT_STOP;
        } else {
            isNextStop = false;
            return VIEW_TYPE_DEFAULT;
        }
    }

    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return simpleDateFormat.format(calendar.getTime());
    }

    private String getNearestTime(List<String> times, String timeNow) {
        String returnTime = null;
        for (String time : times) {
            if (time.compareTo(timeNow) >= 0) {
                if (returnTime == null || returnTime.compareTo(time) >= 0) {
                    returnTime = time;
                }
            }
        }
        return returnTime == null ? timeNow : returnTime;
        //return returnTime;
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {

        RouteStops stops = routeStops.get(holder.getAdapterPosition());

        holder.tvTownName.setText(stops.getTownName());
        holder.tvStopTime.setText(stops.getStopTime());
        //holder.tvTownCode.setText(stops.getTownCode());

        if (isNextStop) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            try {
                Date timeNow = simpleDateFormat.parse(getCurrentTime());
                Date timeStop = simpleDateFormat.parse(stops.getStopTime());
                long timeDiff = timeStop.getTime() - timeNow.getTime();
                long diffMinutes = timeDiff / (60 * 1000) % 60;
                long diffHours = timeDiff / (60 * 60 * 1000) % 24;

                CharSequence hours = "00";
                CharSequence minutes;

                if (diffHours > 0) {
                    hours = TextUtils.concat("0", String.valueOf(diffHours));
                    if(diffMinutes < 10){
                        minutes = TextUtils.concat("0", String.valueOf(diffMinutes));
                    }else {
                        minutes = String.valueOf(diffMinutes);
                    }
                    /*holder.tvNextStop.setText(TextUtils.concat("next stop in ",
                            String.valueOf(diffHours),
                            " hour and ",
                            String.valueOf(diffMinutes),
                            " minutes at:"));*/
                } else {
                    if(diffMinutes < 10){
                        minutes = TextUtils.concat("0", String.valueOf(diffMinutes));
                        //holder.tvNextStop.setText(TextUtils.concat("00:0", String.valueOf(diffMinutes)));
                    }else {
                        minutes = String.valueOf(diffMinutes);
                        //holder.tvNextStop.setText(TextUtils.concat(String.valueOf(diffHours), String.valueOf(diffMinutes)));
                    }
                    /*if (diffMinutes == 0) {
                        holder.tvNextStop
                                .setText("now at:");
                    }
                    else if (diffMinutes == 1) {
                        holder.tvNextStop
                                .setText(TextUtils.concat("next stop in ", String.valueOf(diffMinutes), " minute at:"));
                    }
                    else {
                        holder.tvNextStop
                                .setText(TextUtils.concat("next stop in ", String.valueOf(diffMinutes), " minutes at:"));
                    }*/
                }
                if(diffHours == 0 && diffMinutes == 0){
                    hours = "- -";
                    minutes = "- -";
                }

                holder.tvNextStop.setText(TextUtils.concat(hours, ":", minutes));

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {

        if (routeStops == null) {
            return 0;
        }
        return routeStops.size();
    }

    class RouteViewHolder extends RecyclerView.ViewHolder {

        TextView tvTownName;
        TextView tvStopTime;
        //TextView tvTownCode;
        TextView tvNextStop;

        RouteViewHolder(View itemView) {
            super(itemView);

            tvTownName = itemView.findViewById(R.id.tvRouteItemTown);
            tvStopTime = itemView.findViewById(R.id.tvRouteItemTime);
            //tvTownCode = itemView.findViewById(R.id.tvRouteItemTownCode);
            tvNextStop = itemView.findViewById(R.id.tvRouteItemNextStop);

        }
    }
}
