package com.appdevgenie.shuttleservice.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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
import com.appdevgenie.shuttleservice.utils.CreateTravelInfoArrayList;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.appdevgenie.shuttleservice.utils.Constants.BUNDLE_FROM_SPINNER;
import static com.appdevgenie.shuttleservice.utils.Constants.BUNDLE_IS_DUAL_PANE;
import static com.appdevgenie.shuttleservice.utils.Constants.BUNDLE_MAX_SEATS;
import static com.appdevgenie.shuttleservice.utils.Constants.BUNDLE_TO_SPINNER;
import static com.appdevgenie.shuttleservice.utils.Constants.BUNDLE_TRIP_DATE;
import static com.appdevgenie.shuttleservice.utils.Constants.DIRECTION_DOWNSTREAM;
import static com.appdevgenie.shuttleservice.utils.Constants.DIRECTION_UPSTREAM;
import static com.appdevgenie.shuttleservice.utils.Constants.DOWNSTREAM_DIFFERENCE;
import static com.appdevgenie.shuttleservice.utils.Constants.FIRESTORE_TRAVEL_DATE_FIELD;
import static com.appdevgenie.shuttleservice.utils.Constants.FIRESTORE_TRAVEL_INFO_COLLECTION;
import static com.appdevgenie.shuttleservice.utils.Constants.HOP_COST;
import static com.appdevgenie.shuttleservice.utils.Constants.SAVED_DATE;
import static com.appdevgenie.shuttleservice.utils.Constants.SAVED_DUAL_PANE;
import static com.appdevgenie.shuttleservice.utils.Constants.SAVED_SEATS;
import static com.appdevgenie.shuttleservice.utils.Constants.SHUTTLE_MAX;

public class BookingAvailabilityQueryFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    @BindView(R.id.tvSelectDate)
    TextView tvSelectDate;
    @BindView(R.id.tvPriceValue)
    TextView tvPriceValue;
    @BindView(R.id.tvPriceValueInfo)
    TextView tvPriceValueInfo;
    @BindView(R.id.tvSeatsAmountAvailable)
    TextView tvSeatsAmountAvailable;
    @BindView(R.id.bMakeBooking)
    Button bMakeBooking;
    @BindView(R.id.bCheckAvailability)
    Button bCheckAvailability;
    @BindView(R.id.pbCheckAvailability)
    ProgressBar pbCheckAvailability;
    @BindView(R.id.spPriceFrom)
    Spinner spFrom;
    @BindView(R.id.spPriceTo)
    Spinner spTo;
    private Context context;
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;
    private ArrayList<BookingInfo> bookingInfoArrayList;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<Integer> intArray;
    private ArrayList<Integer> intRangeArray;
    private int direction;
    private boolean dualPane;
    private int maxSeats;
    private int fromInt;
    private int toInt;

    public BookingAvailabilityQueryFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking_availability_query, container, false);
        ButterKnife.bind(this, view);
        setupVariables();

        setRetainInstance(true);

        if (savedInstanceState == null) {
            Bundle bundle = getArguments();
            if (bundle != null) {
                spFrom.setSelection(bundle.getInt(BUNDLE_FROM_SPINNER, 0));
                spTo.setSelection(bundle.getInt(BUNDLE_TO_SPINNER, 0));
                tvSelectDate.setText(bundle.getString(BUNDLE_TRIP_DATE, context.getString(R.string.select_date)));
                dualPane = bundle.getBoolean(BUNDLE_IS_DUAL_PANE);
            }
        } else {
            dualPane = savedInstanceState.getBoolean(SAVED_DUAL_PANE);
            tvSelectDate.setText(savedInstanceState.getString(SAVED_DATE));
            tvSeatsAmountAvailable.setText(savedInstanceState.getString(SAVED_SEATS));
        }

        return view;
    }

    private void setupVariables() {

        context = getActivity();
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        if (appCompatActivity != null) {
            appCompatActivity.getSupportActionBar().setTitle(R.string.availability);
        }

        intArray = new ArrayList<>();
        intRangeArray = new ArrayList<>();

        firebaseFirestore = FirebaseFirestore.getInstance();

        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format_day_month_year), Locale.getDefault());

        ArrayAdapter<CharSequence> spFromAdapter =
                ArrayAdapter.createFromResource(context, R.array.town_names, R.layout.spinner_item);
        spFromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFrom.setAdapter(spFromAdapter);
        spFrom.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> spToAdapter =
                ArrayAdapter.createFromResource(context, R.array.town_names, R.layout.spinner_item);
        spToAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTo.setAdapter(spToAdapter);
        spTo.setOnItemSelectedListener(this);

        tvPriceValue.setVisibility(View.GONE);
        tvPriceValueInfo.setVisibility(View.GONE);
        bMakeBooking.setOnClickListener(this);
        bCheckAvailability.setOnClickListener(this);
        tvSelectDate.setOnClickListener(this);
    }

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

        switch (v.getId()) {
            case R.id.tvSelectDate:
                new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, month);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                tvSelectDate.setText(simpleDateFormat.format(calendar.getTime()));
                                tvSeatsAmountAvailable.setText("");
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
                bundle.putBoolean(BUNDLE_IS_DUAL_PANE, dualPane);
                bundle.putInt(BUNDLE_MAX_SEATS, maxSeats);
                makeBookingFragment.setArguments(bundle);

                if (dualPane) {
                    getActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentInfoContainer, makeBookingFragment)
                            .commit();
                } else {
                    getActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainer, makeBookingFragment)
                            .commit();
                }
                break;

            case R.id.bCheckAvailability:

                if (TextUtils.equals(tvSelectDate.getText(), context.getString(R.string.select_date))) {
                    Toast.makeText(context, R.string.select_valid_date, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(tvPriceValue.getText())) {
                    Toast.makeText(context, R.string.select_valid_trip, Toast.LENGTH_SHORT).show();
                    return;
                }
                loadTravelInfo(tvSelectDate.getText().toString());
                break;
        }
    }

    private void calculateSeatCost(int fromInt, int toInt) {

        int upstreamDownStream = toInt - fromInt;

        double costPerSeatDouble = Math.abs((upstreamDownStream) * HOP_COST);
        if (costPerSeatDouble > 0) {
            tvPriceValue.setText(TextUtils.concat("R ", String.format(Locale.ENGLISH, "%.2f", costPerSeatDouble)));

            if (upstreamDownStream >= 0) {
                direction = DIRECTION_UPSTREAM;
            } else {
                direction = DIRECTION_DOWNSTREAM;
            }

            tvPriceValue.setVisibility(View.VISIBLE);
            tvPriceValueInfo.setVisibility(View.VISIBLE);
            bMakeBooking.setVisibility(View.VISIBLE);
            bCheckAvailability.setVisibility(View.VISIBLE);

        } else {
            tvPriceValue.setVisibility(View.GONE);
            tvPriceValueInfo.setVisibility(View.GONE);
            tvPriceValue.setText("");
            tvSeatsAmountAvailable.setText("");
            bMakeBooking.setVisibility(View.GONE);
            bCheckAvailability.setVisibility(View.GONE);

        }
    }

    private void loadTravelInfo(String date) {

        bookingInfoArrayList = new ArrayList<>();
        pbCheckAvailability.setVisibility(View.VISIBLE);
        tvSeatsAmountAvailable.setText("");

        CollectionReference collectionReference = firebaseFirestore.collection(FIRESTORE_TRAVEL_INFO_COLLECTION);
        collectionReference.whereEqualTo(FIRESTORE_TRAVEL_DATE_FIELD, date)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        pbCheckAvailability.setVisibility(View.GONE);

                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            BookingInfo bookingInfo = documentSnapshot.toObject(BookingInfo.class);
                            bookingInfoArrayList.add(bookingInfo);
                        }
                        intRangeArray = CreateTravelInfoArrayList.createPassengerMaxList(context, bookingInfoArrayList);
                        if (direction == DIRECTION_UPSTREAM) {
                            getMaxSeats(fromInt, toInt);
                        }
                        if (direction == DIRECTION_DOWNSTREAM) {
                            getMaxSeats(DOWNSTREAM_DIFFERENCE - fromInt, DOWNSTREAM_DIFFERENCE - toInt);
                        }
                    }
                });
    }

    private void getMaxSeats(int fromInt, int toInt) {

        //create array list between requested stops
        intArray = new ArrayList<>(intRangeArray.subList(fromInt, toInt));
        //get highest value in intArray
        maxSeats = Collections.max(intArray);
        /*if (maxSeats == SHUTTLE_MAX) {
            bMakeBooking.setVisibility(View.INVISIBLE);
        } else {
            bMakeBooking.setVisibility(View.VISIBLE);
        }*/
        CharSequence seats = TextUtils.concat(String.valueOf(SHUTTLE_MAX - maxSeats), " ", getString(R.string.seats_available));
        tvSeatsAmountAvailable.setText(seats);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_DUAL_PANE, dualPane);
        outState.putString(SAVED_DATE, tvSelectDate.getText().toString());
        outState.putString(SAVED_SEATS, tvSeatsAmountAvailable.getText().toString());
    }
}
