package com.appdevgenie.shuttleservice.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import java.util.Calendar;
import java.util.Locale;

public class BookingAvailabilityQueryFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private TextView tvSelectDate;
    //private TextView tvDate;
    private Spinner spFrom;
    private Spinner spTo;
    private Context context;
    private View view;
    private Calendar calendar;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_booking_availability_query, container, false);
        setupVariables();
        return view;
    }

    private void setupVariables() {

        context = getActivity();

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

        calendar = Calendar.getInstance();
        dateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        //tvDate = view.findViewById(R.id.tvTripDetailsDateValue);
        tvSelectDate = view.findViewById(R.id.tvSelectDate);
        tvSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(context,
                        dateSetListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();


                /*DialogFragment newFragment = new DatePickerDialogFragment();
                newFragment.show(getFragmentManager(), "datePicker");*/
            }
        });
    }

    private void updateLabel() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        tvSelectDate.setText(simpleDateFormat.format(calendar.getTime()));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
