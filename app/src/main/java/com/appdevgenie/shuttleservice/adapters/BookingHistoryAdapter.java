package com.appdevgenie.shuttleservice.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    private ArrayList<BookingInfo> bookingInfoArrayList;
    private ItemClickWidgetListener itemClickWidgetListener;
    private ItemClickShareListener itemClickShareListener;
    private Animation rotate;

    public BookingHistoryAdapter(
            Context context,
            ArrayList<BookingInfo> bookingInfoArrayList,
            ItemClickWidgetListener itemClickWidgetListener,
            ItemClickShareListener itemClickShareListener) {

        this.context = context;
        this.bookingInfoArrayList = bookingInfoArrayList;
        this.itemClickWidgetListener = itemClickWidgetListener;
        this.itemClickShareListener = itemClickShareListener;
        rotate = AnimationUtils.loadAnimation(context,R.anim.rotate);
    }

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
                throw new IllegalArgumentException(context.getString(R.string.invalid_view_type) + viewType);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        int viewType = holder.getItemViewType();
        BookingInfo bookingInfo = bookingInfoArrayList.get(holder.getAdapterPosition());
        //setAnimation(holder.itemView, position);

        if (bookingInfo != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(context.getString(R.string.date_format_day_month_year_time), Locale.getDefault());
            Date date = bookingInfo.getBookingDate();
            String bookingDateString = context.getString(R.string.booking) + simpleDateFormat.format(date);

            switch (viewType) {
                case VIEW_TYPE_BOOKING_DEFAULT:
                    ((DefaultViewHolder) holder).tvBookingDate.setText(bookingDateString);
                    ((DefaultViewHolder) holder).tvSeats.setText(String.valueOf(bookingInfo.getSeats()));
                    ((DefaultViewHolder) holder).tvDate.setText(bookingInfo.getDate());
                    break;

                case VIEW_TYPE_BOOKING_SELECTED:
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
    }

    @Override
    public int getItemViewType(int position) {

        BookingInfo bookingInfo = bookingInfoArrayList.get(position);
        int type = bookingInfo.getViewType();

        switch (type) {
            case 0:
                return VIEW_TYPE_BOOKING_DEFAULT;

            case 1:
                return VIEW_TYPE_BOOKING_SELECTED;

            default:
                return -1;
        }
        /*if (type == 0) {
            return VIEW_TYPE_BOOKING_DEFAULT;
        } else {
            return VIEW_TYPE_BOOKING_SELECTED;
        }*/
    }

    /*private void setAnimation(View container, int position) {
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        container.startAnimation(animation);
    }

    private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(1000);
        view.startAnimation(anim);
    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000);
        view.startAnimation(anim);
    }*/

    @Override
    public int getItemCount() {
        if (bookingInfoArrayList == null) {
            return 0;
        }
        return bookingInfoArrayList.size();
    }

    class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //private CardView cardView;
        private TextView tvBookingDate;
        private TextView tvDate;
        private TextView tvSeats;
        private ImageButton ibExpand;

        DefaultViewHolder(View itemView) {
            super(itemView);
            //cardView = itemView.findViewById(R.id.cardView);
            tvBookingDate = itemView.findViewById(R.id.tvBookingMadeDate);
            tvDate = itemView.findViewById(R.id.tvTripDetailsDateValue);
            tvSeats = itemView.findViewById(R.id.tvTripDetailsSeatsValue);
            ibExpand = itemView.findViewById(R.id.ibBookingHistoryReduceExpand);
            ibExpand.setOnClickListener(this);
            ibExpand.setImageResource(R.drawable.ic_expand);
        }

        @Override
        public void onClick(View v) {
            //int clickedItem = getAdapterPosition();
            //setAnimation(cardView, getAdapterPosition());
            ibExpand.startAnimation(rotate);
            rotate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    BookingInfo bookingInfo = bookingInfoArrayList.get(getAdapterPosition());
                    bookingInfo.setViewType(VIEW_TYPE_BOOKING_SELECTED);
                    notifyItemChanged(getAdapterPosition());
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

    class SelectedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //private CardView cardView;
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
        private Button bShare;

        SelectedViewHolder(View itemView) {
            super(itemView);
            //cardView = itemView.findViewById(R.id.cardView);
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
            bShare = itemView.findViewById(R.id.bShareBooking);
            bShare.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.ibBookingHistoryReduceExpand:
                    //setAnimation(cardView, getAdapterPosition());
                    ibReduce.startAnimation(rotate);
                    rotate.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            BookingInfo bookingInfo = bookingInfoArrayList.get(getAdapterPosition());
                            bookingInfo.setViewType(VIEW_TYPE_BOOKING_DEFAULT);
                            notifyItemChanged(getAdapterPosition());
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    break;

                case R.id.bAddToWidget:
                    itemClickWidgetListener.onItemWidgetClick(getAdapterPosition());
                    break;

                case R.id.bShareBooking:
                    itemClickShareListener.onItemShareClick(getAdapterPosition());
                    break;
            }
        }
    }

    public interface ItemClickWidgetListener {
        void onItemWidgetClick(int pos);
    }

    public interface ItemClickShareListener {
        void onItemShareClick(int pos);
    }
}
