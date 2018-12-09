package com.appdevgenie.shuttleservice.fragments;

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
import android.widget.Spinner;
import android.widget.TextView;

import com.appdevgenie.shuttleservice.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.appdevgenie.shuttleservice.utils.Constants.BUNDLE_FROM_SPINNER;
import static com.appdevgenie.shuttleservice.utils.Constants.BUNDLE_IS_DUAL_PANE;
import static com.appdevgenie.shuttleservice.utils.Constants.BUNDLE_TO_SPINNER;
import static com.appdevgenie.shuttleservice.utils.Constants.HOP_COST;
import static com.appdevgenie.shuttleservice.utils.Constants.SAVED_DUAL_PANE;

public class MainPriceCheckFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    @BindView(R.id.spPriceFrom)
    Spinner spFrom;
    @BindView(R.id.spPriceTo)
    Spinner spTo;
    @BindView(R.id.tvPriceValue)
    TextView tvPriceValue;
    @BindView(R.id.tvPriceValueInfo)
    TextView tvPriceValueInfo;
    @BindView(R.id.tvTripDetailsDepartureCode)
    TextView tvDepartureCode;
    @BindView(R.id.tvTripDetailsDepartureTown)
    TextView tvDepartureTown;
    @BindView(R.id.tvTripDetailsDepartureTime)
    TextView tvDepartureTime;
    @BindView(R.id.tvTripDetailsArrivalCode)
    TextView tvArrivalCode;
    @BindView(R.id.tvTripDetailsArrivalTown)
    TextView tvArrivalTown;
    @BindView(R.id.tvTripDetailsArrivalTime)
    TextView tvArrivalTime;
    @BindView(R.id.bCheckAvailability)
    Button bCheckAvailability;
    private int fromInt;
    private int toInt;
    private Context context;
    private FirebaseAuth firebaseAuth;
    private boolean dualPane;
    @BindView(R.id.include_layout_trip_details)
    View vTripDetails;

    public MainPriceCheckFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_price_check, container, false);
        ButterKnife.bind(this, view);

        if(savedInstanceState == null) {
            Bundle bundle = getArguments();
            if (bundle != null) {
                dualPane = bundle.getBoolean(BUNDLE_IS_DUAL_PANE);
            }
        }else{
            dualPane = savedInstanceState.getBoolean(SAVED_DUAL_PANE);
        }

        setupVariables();

        return view;
    }

    private void setupVariables() {

        context = getActivity();
        AppCompatActivity appCompatActivity = (AppCompatActivity)getActivity();
        if (appCompatActivity != null) {
            appCompatActivity.getSupportActionBar().setTitle(R.string.price_check);
        }

        firebaseAuth = FirebaseAuth.getInstance();

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

        tvDepartureCode.setOnClickListener(this);
        tvArrivalCode.setOnClickListener(this);
        bCheckAvailability.setVisibility(View.GONE);
        bCheckAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookingAvailabilityQueryFragment bookingQueryFragment = new BookingAvailabilityQueryFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(BUNDLE_FROM_SPINNER, spFrom.getSelectedItemPosition());
                bundle.putInt(BUNDLE_TO_SPINNER, spTo.getSelectedItemPosition());
                bundle.putBoolean(BUNDLE_IS_DUAL_PANE, dualPane);
                bookingQueryFragment.setArguments(bundle);

                if(dualPane){
                    getActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentInfoContainer, bookingQueryFragment)
                            //.addToBackStack(null)
                            .commit();

                }else{
                    getActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainer, bookingQueryFragment)
                            //.addToBackStack(null)
                            .commit();
                }
            }
        });

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

        calculateCost(fromInt, toInt);
    }

    private void calculateCost(int fromInt, int toInt) {

        int upstreamDownStream = toInt - fromInt;

        String departure;
        String arrival;

        double costDouble = Math.abs((upstreamDownStream) * HOP_COST);
        if (costDouble > 0) {
            tvPriceValue.setText(TextUtils.concat("R ", String.format(Locale.ENGLISH, "%.2f", costDouble)));

            if (upstreamDownStream >= 0) {
                departure = Arrays.asList(getResources().getStringArray(R.array.route_stops_time_morning)).get(fromInt);
                arrival = Arrays.asList(getResources().getStringArray(R.array.route_stops_time_morning)).get(toInt);
            } else {
                departure = Arrays.asList(getResources().getStringArray(R.array.route_stops_time_afternoon)).get(fromInt);
                arrival = Arrays.asList(getResources().getStringArray(R.array.route_stops_time_afternoon)).get(toInt);
            }

            String[] codes = context.getResources().getStringArray(R.array.route_stops_town_code);
            ArrayList<String> code = new ArrayList<>(Arrays.asList(codes));

            vTripDetails.setVisibility(View.VISIBLE);
            tvPriceValue.setVisibility(View.VISIBLE);
            tvPriceValueInfo.setVisibility(View.VISIBLE);
            tvDepartureCode.setText(code.get(fromInt));
            tvDepartureTown.setText(spFrom.getSelectedItem().toString());
            tvDepartureTime.setText(departure);
            tvArrivalCode.setText(code.get(toInt));
            tvArrivalTown.setText(spTo.getSelectedItem().toString());
            tvArrivalTime.setText(arrival);
            if (firebaseAuth.getCurrentUser() != null) {
                bCheckAvailability.setVisibility(View.VISIBLE);
            }
        } else {
            vTripDetails.setVisibility(View.INVISIBLE);

            tvPriceValue.setVisibility(View.INVISIBLE);
            tvPriceValueInfo.setVisibility(View.INVISIBLE);
            bCheckAvailability.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.tvTripDetailsDepartureCode:
                spFrom.performClick();
                break;

            case R.id.tvTripDetailsArrivalCode:
                spTo.performClick();
                break;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_DUAL_PANE, dualPane);
    }
}
