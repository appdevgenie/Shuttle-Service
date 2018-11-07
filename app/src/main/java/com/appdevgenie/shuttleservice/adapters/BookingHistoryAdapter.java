package com.appdevgenie.shuttleservice.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appdevgenie.shuttleservice.R;
import com.appdevgenie.shuttleservice.model.BookingInfo;

import java.util.ArrayList;

import static com.appdevgenie.shuttleservice.utils.Constants.VIEW_TYPE_BOOKING_DEFAULT;
import static com.appdevgenie.shuttleservice.utils.Constants.VIEW_TYPE_BOOKING_EXTENDED;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.BookingViewHolder> {

    private Context context;
    private ArrayList<BookingInfo> bookingInfoArrayList = new ArrayList<>();
    private ItemLongClickListener itemLongClickListener;

    public BookingHistoryAdapter(Context context, ArrayList<BookingInfo> bookingInfoArrayList, ItemLongClickListener itemLongClickListener) {
        this.context = context;
        this.bookingInfoArrayList = bookingInfoArrayList;
        this.itemLongClickListener = itemLongClickListener;
    }

    @NonNull
    @Override
    public BookingHistoryAdapter.BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        int layoutId;

        switch (viewType) {
            case VIEW_TYPE_BOOKING_DEFAULT:
                layoutId = R.layout.list_item_booking_history;
                break;

            case VIEW_TYPE_BOOKING_EXTENDED:
                layoutId = R.layout.list_item_booking_history_selected;
                break;

            default:
                throw new IllegalArgumentException("Invalid view type: " + viewType);
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {

        BookingInfo bookingInfo = bookingInfoArrayList.get(position);
        int type = bookingInfo.getViewType();

        if(type == 0){
            return VIEW_TYPE_BOOKING_DEFAULT;
        }else{
            return VIEW_TYPE_BOOKING_EXTENDED;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull BookingHistoryAdapter.BookingViewHolder holder, int position) {

        BookingInfo bookingInfo = bookingInfoArrayList.get(holder.getAdapterPosition());

        holder.tvSeats.setText(bookingInfo.getSeats());
        holder.tvDate.setText(bookingInfo.getDate());
        holder.tvFrom.setText(bookingInfo.getFromTown());
        holder.tvTo.setText(bookingInfo.getToTown());
        holder.tvFromCode.setText(bookingInfo.getFromTownCode());
        holder.tvToCode.setText(bookingInfo.getToTownCode());
    }

    @Override
    public int getItemCount() {
        if(bookingInfoArrayList == null) {
            return 0;
        }
        return bookingInfoArrayList.size();
    }

    public class BookingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView tvDate;
        private TextView tvSeats;
        private TextView tvFrom;
        private TextView tvTo;
        private TextView tvFromCode;
        private TextView tvToCode;

        public BookingViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            tvDate = itemView.findViewById(R.id.tvTripDetailsDateValue);
            tvSeats = itemView.findViewById(R.id.tvTripDetailsSeatsValue);
            tvFrom = itemView.findViewById(R.id.tvTripDetailsDepartureTown);
            tvTo = itemView.findViewById(R.id.tvTripDetailsArrivalTown);
            tvFromCode = itemView.findViewById(R.id.tvTripDetailsDepartureCode);
            tvToCode = itemView.findViewById(R.id.tvTripDetailsArrivalCode);
        }

        @Override
        public void onClick(View v) {

            int clickedItem = getAdapterPosition();

            BookingInfo bookingInfo = bookingInfoArrayList.get(getAdapterPosition());
            if(bookingInfo.getViewType() == 1){
                bookingInfo.setViewType(0);
            }else{
                bookingInfo.setViewType(1);
            }

            notifyItemChanged(clickedItem);
        }

        @Override
        public boolean onLongClick(View v) {
            itemLongClickListener.onItemLongClick(getAdapterPosition());
            return false;
        }
    }

    public interface ItemLongClickListener {
        void onItemLongClick(int pos);
    }
}
