package com.appdevgenie.shuttleservice.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import static com.appdevgenie.shuttleservice.utils.Constants.BUNDLE_FROM_SPINNER;
import static com.appdevgenie.shuttleservice.utils.Constants.BUNDLE_TO_SPINNER;
import static com.appdevgenie.shuttleservice.utils.Constants.HOP_COST;

public class MainPriceCheckFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private Spinner spFrom;
    private Spinner spTo;
    private TextView tvPriceValue;
    private TextView tvPriceValueInfo;
    private TextView tvDepartureCode;
    private TextView tvDepartureTown;
    private TextView tvDepartureTime;
    private TextView tvDepartureLabel;
    private TextView tvArrivalCode;
    private TextView tvArrivalTown;
    private TextView tvArrivalTime;
    private TextView tvArrivalLabel;
    private Button bCheckAvailability;
    private View view;
    private int fromInt;
    private int toInt;
    private Context context;
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_price_check, container, false);
        setupVariables();
        return view;
    }

    private void setupVariables() {

        context = getActivity();

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        AppCompatActivity appCompatActivity = (AppCompatActivity)getActivity();
        if (appCompatActivity != null) {
            appCompatActivity.setSupportActionBar(toolbar);
            appCompatActivity.getSupportActionBar().setTitle(R.string.price_check);
            appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
        }

        firebaseAuth = FirebaseAuth.getInstance();

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
        tvDepartureCode = view.findViewById(R.id.tvTripDetailsDepartureCode);
        tvDepartureCode.setOnClickListener(this);
        tvDepartureTown = view.findViewById(R.id.tvTripDetailsDepartureTown);
        tvDepartureTime = view.findViewById(R.id.tvTripDetailsDepartureTime);
        tvDepartureLabel = view.findViewById(R.id.tvTripDetailsDepartureLabel);
        tvArrivalCode = view.findViewById(R.id.tvTripDetailsArrivalCode);
        tvArrivalCode.setOnClickListener(this);
        tvArrivalTown = view.findViewById(R.id.tvTripDetailsArrivalTown);
        tvArrivalTime = view.findViewById(R.id.tvTripDetailsArrivalTime);
        tvArrivalLabel = view.findViewById(R.id.tvTripDetailsArrivalLabel);
        bCheckAvailability = view.findViewById(R.id.bCheckAvailability);
        bCheckAvailability.setVisibility(View.GONE);
        bCheckAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookingAvailabilityQueryFragment bookingQueryFragment = new BookingAvailabilityQueryFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(BUNDLE_FROM_SPINNER, spFrom.getSelectedItemPosition());
                bundle.putInt(BUNDLE_TO_SPINNER, spTo.getSelectedItemPosition());
                bookingQueryFragment.setArguments(bundle);

                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainActivityContainer, bookingQueryFragment)
                        //.addToBackStack(null)
                        .commit();
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

            tvPriceValue.setVisibility(View.VISIBLE);
            tvPriceValueInfo.setVisibility(View.VISIBLE);
            tvDepartureCode.setText(code.get(fromInt));
            tvDepartureTown.setText(spFrom.getSelectedItem().toString());
            tvDepartureTime.setText(departure);
            tvDepartureLabel.setVisibility(View.VISIBLE);
            tvArrivalCode.setText(code.get(toInt));
            tvArrivalTown.setText(spTo.getSelectedItem().toString());
            tvArrivalTime.setText(arrival);
            tvArrivalLabel.setVisibility(View.VISIBLE);
            if (firebaseAuth.getCurrentUser() != null) {
                bCheckAvailability.setVisibility(View.VISIBLE);
            }

        } else {
            tvPriceValue.setVisibility(View.INVISIBLE);
            tvPriceValueInfo.setVisibility(View.INVISIBLE);
            tvDepartureCode.setText("");
            tvDepartureTown.setText("");
            tvDepartureTime.setText("");
            tvDepartureLabel.setVisibility(View.INVISIBLE);
            tvArrivalCode.setText("");
            tvArrivalTown.setText("");
            tvArrivalTime.setText("");
            tvArrivalLabel.setVisibility(View.INVISIBLE);
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
}
