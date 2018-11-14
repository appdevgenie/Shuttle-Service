package com.appdevgenie.shuttleservice.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.appdevgenie.shuttleservice.R;
import com.appdevgenie.shuttleservice.model.BookingInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.appdevgenie.shuttleservice.utils.Constants.VIEW_TYPE_BOOKING_DEFAULT;
import static com.appdevgenie.shuttleservice.utils.Constants.VIEW_TYPE_BOOKING_SELECTED;

public class BookingHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<BookingInfo> bookingInfoArrayList = new ArrayList<>();
    //private ItemLongClickListener itemLongClickListener;
    private ItemClickWidgetListener itemClickWidgetListener;

    public BookingHistoryAdapter(Context context, ArrayList<BookingInfo> bookingInfoArrayList, ItemClickWidgetListener itemClickWidgetListener) {
        this.context = context;
        this.bookingInfoArrayList = bookingInfoArrayList;
        this.itemClickWidgetListener = itemClickWidgetListener;
        //this.itemLongClickListener = itemLongClickListener;
    }

    /*@NonNull
    @Override
    public BookingHistoryAdapter.BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        int layoutId;

        switch (viewType) {
            case VIEW_TYPE_BOOKING_DEFAULT:
                layoutId = R.layout.list_item_booking_history;
                break;

            case VIEW_TYPE_BOOKING_SELECTED:
                layoutId = R.layout.list_item_booking_history_selected;
                break;

            default:
                throw new IllegalArgumentException("Invalid view type: " + viewType);
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new BookingViewHolder(view);
    }*/

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;

        switch (viewType) {
            case VIEW_TYPE_BOOKING_DEFAULT:
                View defaultView = LayoutInflater
                        .from(context)
                        .inflate(R.layout.list_item_booking_history_default, parent, false);
                viewHolder = new DefaultViewHolder(defaultView);
                break;

            case VIEW_TYPE_BOOKING_SELECTED:
                View selectedView = LayoutInflater
                        .from(context)
                        .inflate(R.layout.list_item_booking_history_selected, parent, false);
                viewHolder = new SelectedViewHolder(selectedView);
                break;

            default:
                throw new IllegalArgumentException("Invalid view type: " + viewType);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        int viewType = holder.getItemViewType();
        BookingInfo bookingInfo = bookingInfoArrayList.get(holder.getAdapterPosition());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM YYYY", Locale.getDefault());
        Date date = bookingInfo.getBookingDate();
        String bookingDateString = "Booking: " + simpleDateFormat.format(date);

        switch (viewType) {
            case VIEW_TYPE_BOOKING_DEFAULT:
                ((DefaultViewHolder) holder).tvBookingDate.setText(bookingDateString);
                ((DefaultViewHolder) holder).tvSeats.setText(String.valueOf(bookingInfo.getSeats()));
                ((DefaultViewHolder) holder).tvDate.setText(bookingInfo.getDate());
                break;

            case VIEW_TYPE_BOOKING_SELECTED:
                /*SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, dd MMM, HH:mm", Locale.getDefault());
                Date date = bookingInfo.getBookingDate();
                String bookingDateString = simpleDateFormat.format(date);*/
                ((SelectedViewHolder) holder).tvBookingDate.setText(bookingDateString);
                ((SelectedViewHolder) holder).tvSeats.setText(String.valueOf(bookingInfo.getSeats()));
                ((SelectedViewHolder) holder).tvDate.setText(bookingInfo.getDate());
                ((SelectedViewHolder) holder).tvFrom.setText(bookingInfo.getFromTown());
                ((SelectedViewHolder) holder).tvTo.setText(bookingInfo.getToTown());
                ((SelectedViewHolder) holder).tvDepartTime.setText(bookingInfo.getDepartureTime());
                ((SelectedViewHolder) holder).tvArriveTime.setText(bookingInfo.getArrivalTime());
                ((SelectedViewHolder) holder).tvFromCode.setText(bookingInfo.getFromTownCode());
                ((SelectedViewHolder) holder).tvToCode.setText(bookingInfo.getToTownCode());
                break;
        }

    }

    @Override
    public int getItemViewType(int position) {

        BookingInfo bookingInfo = bookingInfoArrayList.get(position);
        int type = bookingInfo.getViewType();

        if (type == 0) {
            return VIEW_TYPE_BOOKING_DEFAULT;
        } else {
            return VIEW_TYPE_BOOKING_SELECTED;
        }

    }

    @Override
    public int getItemCount() {
        if (bookingInfoArrayList == null) {
            return 0;
        }
        return bookingInfoArrayList.size();
    }

    public class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvBookingDate;
        private TextView tvDate;
        private TextView tvSeats;
        private ImageButton ibExpand;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            tvBookingDate = itemView.findViewById(R.id.tvBookingMadeDate);
            tvDate = itemView.findViewById(R.id.tvTripDetailsDateValue);
            tvSeats = itemView.findViewById(R.id.tvTripDetailsSeatsValue);
            ibExpand = itemView.findViewById(R.id.ibBookingHistoryReduceExpand);
            ibExpand.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //int clickedItem = getAdapterPosition();
            BookingInfo bookingInfo = bookingInfoArrayList.get(getAdapterPosition());
            bookingInfo.setViewType(VIEW_TYPE_BOOKING_SELECTED);
            notifyDataSetChanged();
        }
    }

    public class SelectedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvBookingDate;
        private TextView tvDate;
        private TextView tvSeats;
        private TextView tvFrom;
        private TextView tvDepartTime;
        private TextView tvArriveTime;
        private TextView tvTo;
        private TextView tvFromCode;
        private TextView tvToCode;
        private ImageButton ibReduce;
        private Button bAddToWidget;
        private Button bCancel;

        public SelectedViewHolder(View itemView) {
            super(itemView);

            tvBookingDate = itemView.findViewById(R.id.tvBookingMadeDate);
            tvDate = itemView.findViewById(R.id.tvTripDetailsDateValue);
            tvSeats = itemView.findViewById(R.id.tvTripDetailsSeatsValue);
            tvFrom = itemView.findViewById(R.id.tvTripDetailsDepartureTown);
            tvTo = itemView.findViewById(R.id.tvTripDetailsArrivalTown);
            tvDepartTime = itemView.findViewById(R.id.tvTripDetailsDepartureTime);
            tvArriveTime = itemView.findViewById(R.id.tvTripDetailsArrivalTime);
            tvFromCode = itemView.findViewById(R.id.tvTripDetailsDepartureCode);
            tvToCode = itemView.findViewById(R.id.tvTripDetailsArrivalCode);
            ibReduce = itemView.findViewById(R.id.ibBookingHistoryReduceExpand);
            ibReduce.setOnClickListener(this);
            ibReduce.setImageResource(R.drawable.ic_reduce);
            bAddToWidget = itemView.findViewById(R.id.bAddToWidget);
            bAddToWidget.setOnClickListener(this);
            bCancel = itemView.findViewById(R.id.bCancelBooking);
            bCancel.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.ibBookingHistoryReduceExpand:
                    BookingInfo bookingInfo = bookingInfoArrayList.get(getAdapterPosition());
                    bookingInfo.setViewType(VIEW_TYPE_BOOKING_DEFAULT);
                    notifyDataSetChanged();
                    break;

                case R.id.bAddToWidget:
                    itemClickWidgetListener.onItemWidgetClick(getAdapterPosition());
                    break;

                case R.id.bCancelBooking:

                    break;
            }
        }
    }

    public interface ItemClickWidgetListener {
        void onItemWidgetClick(int pos);
    }

    /*@Override
    public void onBindViewHolder(@NonNull BookingHistoryAdapter.BookingViewHolder holder, int position) {

        BookingInfo bookingInfo = bookingInfoArrayList.get(holder.getAdapterPosition());

        holder.tvSeats.setText(bookingInfo.getSeats());
        holder.tvDate.setText(bookingInfo.getDate());
        holder.tvFrom.setText(bookingInfo.getFromTown());
        holder.tvTo.setText(bookingInfo.getToTown());
        holder.tvDepartTime.setText(bookingInfo.getDepartureTime());
        holder.tvArriveTime.setText(bookingInfo.getArrivalTime());
        holder.tvFromCode.setText(bookingInfo.getFromTownCode());
        holder.tvToCode.setText(bookingInfo.getToTownCode());
    }*/

    /*public class BookingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView tvDate;
        private TextView tvSeats;
        private TextView tvFrom;
        private TextView tvDepartTime;
        private TextView tvArriveTime;
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
            tvDepartTime = itemView.findViewById(R.id.tvTripDetailsDepartureTime);
            tvArriveTime = itemView.findViewById(R.id.tvTripDetailsArrivalTime);
            tvFromCode = itemView.findViewById(R.id.tvTripDetailsDepartureCode);
            tvToCode = itemView.findViewById(R.id.tvTripDetailsArrivalCode);
        }

        @Override
        public void onClick(View v) {

            int clickedItem = getAdapterPosition();

            BookingInfo bookingInfo = bookingInfoArrayList.get(getAdapterPosition());
            if(bookingInfo.getViewType() == VIEW_TYPE_BOOKING_SELECTED){
                bookingInfo.setViewType(VIEW_TYPE_BOOKING_DEFAULT);
            }else{
                bookingInfo.setViewType(VIEW_TYPE_BOOKING_SELECTED);
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
    }*/
}
