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
import com.appdevgenie.shuttleservice.model.TravelInfo;
import com.appdevgenie.shuttleservice.model.WeatherInfo;

import java.util.ArrayList;
import java.util.List;

public class AdminTravelInfoAdapter extends RecyclerView.Adapter<AdminTravelInfoAdapter.TravelViewHolder> {

    private Context context;
    private ArrayList<TravelInfo> travelInfoArrayList;

    public AdminTravelInfoAdapter(Context context, ArrayList<TravelInfo> travelInfoArrayList) {
        this.context = context;
        this.travelInfoArrayList = travelInfoArrayList;
    }

    @NonNull
    @Override
    public AdminTravelInfoAdapter.TravelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_admin_travel_info, parent, false);
        return new TravelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminTravelInfoAdapter.TravelViewHolder holder, int position) {

        TravelInfo travelInfo = travelInfoArrayList.get(holder.getAdapterPosition());

        holder.town.setText(travelInfo.getTownName());
        holder.time.setText(travelInfo.getDepartureTime());
        holder.on.setText(TextUtils.concat("On: ", String.valueOf(travelInfo.getOnPassengers())));
        holder.off.setText(TextUtils.concat("Off: ", String.valueOf(travelInfo.getOffPassengers())));
        holder.total.setText(TextUtils.concat("Passengers: ", String.valueOf(travelInfo.getTotalPassengers())));

    }

    public void setAdapterData(ArrayList<TravelInfo> travelInfoList) {
        travelInfoArrayList = travelInfoList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(travelInfoArrayList == null) {
            return 0;
        }
        return travelInfoArrayList.size();
    }

    class TravelViewHolder extends RecyclerView.ViewHolder{

        private TextView time;
        private TextView town;
        private TextView on;
        private TextView off;
        private TextView total;

        public TravelViewHolder(View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.tvRouteItemTime);
            town = itemView.findViewById(R.id.tvRouteItemTown);
            on = itemView.findViewById(R.id.tvTravelInfoOn);
            off = itemView.findViewById(R.id.tvTravelInfoOff);
            total = itemView.findViewById(R.id.tvRouteItemTotal);
        }
    }
}
