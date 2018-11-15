package com.appdevgenie.shuttleservice.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.appdevgenie.shuttleservice.R;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import static com.appdevgenie.shuttleservice.utils.Constants.BUNDLE_FROM_SPINNER;
import static com.appdevgenie.shuttleservice.utils.Constants.BUNDLE_TO_SPINNER;
import static com.appdevgenie.shuttleservice.utils.Constants.BUNDLE_TRIP_DATE;
import static com.appdevgenie.shuttleservice.utils.Constants.HOP_COST;

public class BookingAvailabilityQueryFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private TextView tvSelectDate;
    private TextView tvPriceValue;
    private TextView tvPriceValueInfo;
    private Button bMakeBooking;
    //private TextView tvDate;
    private String departureTime;
    private String arrivalTime;
    private int fromInt;
    private int toInt;
    private Spinner spFrom;
    private Spinner spTo;
    private Context context;
    private View view;
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;
    private boolean tripSelected;
    //private DatePickerDialog.OnDateSetListener dateSetListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_booking_availability_query, container, false);

        setupVariables();

        Bundle bundle = getArguments();
        if(bundle != null){
            spFrom.setSelection(bundle.getInt(BUNDLE_FROM_SPINNER));
            spTo.setSelection(bundle.getInt(BUNDLE_TO_SPINNER));
            tvSelectDate.setText(bundle.getString(BUNDLE_TRIP_DATE, context.getString(R.string.select_date)));
        }

        return view;
    }

    private void setupVariables() {

        context = getActivity();

        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        spFrom = view.findViewById(R.id.spPriceFrom);
        ArrayAdapter<CharSequence> spFromAdapter =
                ArrayAdapter.createFromResource(context, R.array.town_names, android.R.layout.simple_spinner_item);
        spFromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFrom.setAdapter(spFromAdapter);
        spFrom.setOnItemSelectedListener(this);

        spTo = view.findViewById(R.id.spPriceTo);
        ArrayAdapter<CharSequence> spToAdapter =
                ArrayAdapter.createFromResource(context, R.array.town_names, android.R.layout.simple_spinner_item);
        spToAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTo.setAdapter(spToAdapter);
        spTo.setOnItemSelectedListener(this);

        tvPriceValue = view.findViewById(R.id.tvPriceValue);
        tvPriceValueInfo = view.findViewById(R.id.tvPriceValueInfo);
        tvPriceValue.setVisibility(View.INVISIBLE);
        tvPriceValueInfo.setVisibility(View.INVISIBLE);
        bMakeBooking = view.findViewById(R.id.bMakeBooking);
        bMakeBooking.setVisibility(View.INVISIBLE);
        bMakeBooking.setOnClickListener(this);

        /*calendar = Calendar.getInstance();
        dateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };*/

        //tvDate = view.findViewById(R.id.tvTripDetailsDateValue);
        tvSelectDate = view.findViewById(R.id.tvSelectDate);
        tvSelectDate.setOnClickListener(this);
        /*tvSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(context,
                        dateSetListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();


                *//*DialogFragment newFragment = new DatePickerDialogFragment();
                newFragment.show(getFragmentManager(), "datePicker");*//*
            }
        });*/
    }

    /*private void updateLabel() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        tvSelectDate.setText(simpleDateFormat.format(calendar.getTime()));
    }*/

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Spinner spinner = (Spinner) parent;

        switch (spinner.getId()) {

            case R.id.spPriceFrom:
                fromInt = position;
                break;

            case R.id.spPriceTo:
                toInt = position;
                break;

        }

        calculateSeatCost(fromInt, toInt);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.tvSelectDate:
                new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, month);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                tvSelectDate.setText(simpleDateFormat.format(calendar.getTime()));
                                if(tripSelected) {
                                    bMakeBooking.setVisibility(View.VISIBLE);
                                }
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
                break;

            case R.id.bMakeBooking:
                MakeBookingFragment makeBookingFragment = new MakeBookingFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(BUNDLE_FROM_SPINNER, spFrom.getSelectedItemPosition());
                bundle.putInt(BUNDLE_TO_SPINNER, spTo.getSelectedItemPosition());
                bundle.putString(BUNDLE_TRIP_DATE, tvSelectDate.getText().toString());
                makeBookingFragment.setArguments(bundle);
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainActivityContainer, makeBookingFragment)
                        .commit();
                break;
        }
    }

    private void calculateSeatCost(int fromInt, int toInt) {

        int upstreamDownStream = toInt - fromInt;

        double costPerSeatDouble = Math.abs((upstreamDownStream) * HOP_COST);
        if (costPerSeatDouble > 0) {
            tvPriceValue.setText(TextUtils.concat("R ", String.format(Locale.ENGLISH, "%.2f", costPerSeatDouble)));

            if(upstreamDownStream >= 0){
                departureTime = Arrays.asList(getResources().getStringArray(R.array.route_stops_time_morning)).get(fromInt);
                arrivalTime = Arrays.asList(getResources().getStringArray(R.array.route_stops_time_morning)).get(toInt);
            }else{
                departureTime = Arrays.asList(getResources().getStringArray(R.array.route_stops_time_afternoon)).get(fromInt);
                arrivalTime = Arrays.asList(getResources().getStringArray(R.array.route_stops_time_afternoon)).get(toInt);
            }

            tvPriceValue.setVisibility(View.VISIBLE);
            tvPriceValueInfo.setVisibility(View.VISIBLE);

            tripSelected = true;
            if(!TextUtils.equals(tvSelectDate.getText().toString(), context.getString(R.string.select_date))) {
                bMakeBooking.setVisibility(View.VISIBLE);
            }

        } else {
            tvPriceValue.setVisibility(View.INVISIBLE);
            tvPriceValueInfo.setVisibility(View.INVISIBLE);
            tvPriceValue.setText("");
            bMakeBooking.setVisibility(View.INVISIBLE);
        }
    }
}
