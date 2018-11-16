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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appdevgenie.shuttleservice.R;
import com.appdevgenie.shuttleservice.model.BookingInfo;
import com.appdevgenie.shuttleservice.model.TravelInfo;
import com.appdevgenie.shuttleservice.utils.CreateTravelInfoArrayList;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import static com.appdevgenie.shuttleservice.utils.Constants.BUNDLE_FROM_SPINNER;
import static com.appdevgenie.shuttleservice.utils.Constants.BUNDLE_TO_SPINNER;
import static com.appdevgenie.shuttleservice.utils.Constants.BUNDLE_TRIP_DATE;
import static com.appdevgenie.shuttleservice.utils.Constants.FIRESTORE_TRAVEL_DATE_FIELD;
import static com.appdevgenie.shuttleservice.utils.Constants.FIRESTORE_TRAVEL_INFO_COLLECTION;
import static com.appdevgenie.shuttleservice.utils.Constants.HOP_COST;

public class BookingAvailabilityQueryFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    public static final int DIRECTION_UPSTREAM = 0;
    public static final int DIRECTION_DOWNSTREAM = 1;

    private TextView tvSelectDate;
    private TextView tvPriceValue;
    private TextView tvPriceValueInfo;
    private TextView tvSeatsAmountAvailable;
    private Button bMakeBooking;
    private ProgressBar pbCheckAvailability;
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
    private ArrayList<BookingInfo> bookingInfoArrayList;
    //private ArrayList<TravelInfo> travelInfoArrayList;
    private FirebaseFirestore firebaseFirestore;
    private int seats;
    private ArrayList<Integer> intArray;
    private ArrayList<Integer> intRangeArray;
    private int direction;
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

        intArray = new ArrayList<>();
        intRangeArray = new ArrayList<>();

        firebaseFirestore = FirebaseFirestore.getInstance();

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
        tvSeatsAmountAvailable = view.findViewById(R.id.tvSeatsAmountAvailable);
        tvPriceValue.setVisibility(View.INVISIBLE);
        tvPriceValueInfo.setVisibility(View.INVISIBLE);
        bMakeBooking = view.findViewById(R.id.bMakeBooking);
        bMakeBooking.setVisibility(View.INVISIBLE);
        bMakeBooking.setOnClickListener(this);

        pbCheckAvailability = view.findViewById(R.id.pbCheckAvailability);

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
                direction = DIRECTION_UPSTREAM;
            }else{
                departureTime = Arrays.asList(getResources().getStringArray(R.array.route_stops_time_afternoon)).get(fromInt);
                arrivalTime = Arrays.asList(getResources().getStringArray(R.array.route_stops_time_afternoon)).get(toInt);
                direction = DIRECTION_DOWNSTREAM;
            }

            tvPriceValue.setVisibility(View.VISIBLE);
            tvPriceValueInfo.setVisibility(View.VISIBLE);

            tripSelected = true;
            if(!TextUtils.equals(tvSelectDate.getText().toString(), context.getString(R.string.select_date))) {
                bMakeBooking.setVisibility(View.VISIBLE);
                loadTravelInfo(tvSelectDate.getText().toString());

            }

        } else {
            tvPriceValue.setVisibility(View.INVISIBLE);
            tvPriceValueInfo.setVisibility(View.INVISIBLE);
            tvPriceValue.setText("");
            tvSeatsAmountAvailable.setText("");
            bMakeBooking.setVisibility(View.INVISIBLE);

        }
    }




    private void loadTravelInfo(String date) {

        bookingInfoArrayList = new ArrayList<>();
        //travelInfoArrayList = new ArrayList<>();


        pbCheckAvailability.setVisibility(View.VISIBLE);

        CollectionReference collectionReference = firebaseFirestore.collection(FIRESTORE_TRAVEL_INFO_COLLECTION);
        collectionReference.whereEqualTo(FIRESTORE_TRAVEL_DATE_FIELD, date)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        pbCheckAvailability.setVisibility(View.GONE);

                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                           // seats = documentSnapshot.getLong("seats").intValue();;
                           // intArray.add(seats);
                            BookingInfo bookingInfo = documentSnapshot.toObject(BookingInfo.class);
                            bookingInfoArrayList.add(bookingInfo);
                        }
                        //travelInfoArrayList = CreateTravelInfoArrayList.createTravelInfoList(context, bookingInfoArrayList);

                        intRangeArray = CreateTravelInfoArrayList.createPassengerMaxList(context, bookingInfoArrayList);
                        //intRangeArray = new ArrayList<>(intArray.subList(1, 5));
                        /*intArray = new ArrayList<>(intRangeArray.subList(fromInt, toInt));
                        int max = Collections.max(intArray);
                        //Toast.makeText(context, String.valueOf(intRangeArray.size()), Toast.LENGTH_SHORT).show();
                        Toast.makeText(context, String.valueOf(max), Toast.LENGTH_SHORT).show();*/
                        if(direction == DIRECTION_UPSTREAM){
                            getMaxSeats(fromInt, toInt);
                        }
                        if(direction == DIRECTION_DOWNSTREAM){
                            getMaxSeats(17 - fromInt, 17 - toInt);
                        }
                    }
                });
    }

    private void getMaxSeats(int fromInt, int toInt) {

        intArray = new ArrayList<>(intRangeArray.subList(fromInt, toInt));
        int max = Collections.max(intArray);

        tvSeatsAmountAvailable.setText((String.valueOf(30 - max)) + " seats available");

        //Toast.makeText(context, String.valueOf(intRangeArray.size()), Toast.LENGTH_SHORT).show();
        //Toast.makeText(context, String.valueOf(max), Toast.LENGTH_SHORT).show();
        /*intRangeArray = new ArrayList<>(intArray.subList(fromInt, toInt));
        int max = Collections.max(intRangeArray);
        Toast.makeText(context, String.valueOf(max), Toast.LENGTH_SHORT).show();*/
    }

    /*// Method for getting the maximum value
    public static int getMax(int[] inputArray){
        int maxValue = inputArray[0];
        for(int i = 1; i < inputArray.length; i++){
            if(inputArray[i] > maxValue){
                maxValue = inputArray[i];
            }
        }
        return maxValue;
    }*/
}
