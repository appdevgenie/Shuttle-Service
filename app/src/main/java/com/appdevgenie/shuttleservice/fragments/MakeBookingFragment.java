package com.appdevgenie.shuttleservice.fragments;

import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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
import com.appdevgenie.shuttleservice.model.User;
import com.appdevgenie.shuttleservice.utils.AlarmReceiver;
import com.appdevgenie.shuttleservice.utils.Constants;
import com.appdevgenie.shuttleservice.utils.CreateTravelInfoArrayList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.appdevgenie.shuttleservice.utils.Constants.BUNDLE_FROM_SPINNER;
import static com.appdevgenie.shuttleservice.utils.Constants.BUNDLE_IS_DUAL_PANE;
import static com.appdevgenie.shuttleservice.utils.Constants.BUNDLE_MAX_SEATS;
import static com.appdevgenie.shuttleservice.utils.Constants.BUNDLE_RECEIVER_DESTINATION;
import static com.appdevgenie.shuttleservice.utils.Constants.BUNDLE_TO_SPINNER;
import static com.appdevgenie.shuttleservice.utils.Constants.BUNDLE_TRIP_DATE;
import static com.appdevgenie.shuttleservice.utils.Constants.DIRECTION_DOWNSTREAM;
import static com.appdevgenie.shuttleservice.utils.Constants.DIRECTION_UPSTREAM;
import static com.appdevgenie.shuttleservice.utils.Constants.DOWNSTREAM_DIFFERENCE;
import static com.appdevgenie.shuttleservice.utils.Constants.FIRESTORE_TRAVEL_DATE_FIELD;
import static com.appdevgenie.shuttleservice.utils.Constants.FIRESTORE_TRAVEL_INFO_COLLECTION;
import static com.appdevgenie.shuttleservice.utils.Constants.FIRESTORE_USER_COLLECTION;
import static com.appdevgenie.shuttleservice.utils.Constants.HOP_COST;
import static com.appdevgenie.shuttleservice.utils.Constants.SAVED_DATE;
import static com.appdevgenie.shuttleservice.utils.Constants.SAVED_DUAL_PANE;
import static com.appdevgenie.shuttleservice.utils.Constants.SAVED_SEATS_ARRAY;
import static com.appdevgenie.shuttleservice.utils.Constants.SHARED_PREFS_SEATS;
import static com.appdevgenie.shuttleservice.utils.Constants.SHUTTLE_MAX;
import static com.appdevgenie.shuttleservice.utils.Constants.THIRTY_MINUTES;

public class MakeBookingFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private Context context;
    @BindView(R.id.tvPassengerInfoName)
    TextView name;
    @BindView(R.id.tvPassengerInfoEmail)
    TextView email;
    @BindView(R.id.tvPassengerInfoContactNum)
    TextView contactNumber;
    @BindView(R.id.tvPassengerInfoNDate)
    TextView date;
    @BindView(R.id.tvTravelCostPerSeat)
    TextView costPerSeat;
    @BindView(R.id.tvTravelCostTotal)
    TextView totalCost;
    @BindView(R.id.spPassengerInfoFrom)
    Spinner spFrom;
    @BindView(R.id.spPassengerInfoTo)
    Spinner spTo;
    @BindView(R.id.spTravelInfoSeats)
    Spinner spSeats;
    @BindView(R.id.bMakeBooking)
    Button bMakeBooking;
    @BindView(R.id.bCheckAvailability)
    Button bCheckAvailability;
    private int fromInt;
    private int toInt;
    private int seatsInt;
    private boolean tripSelected;
    private String departureTime;
    private String arrivalTime;
    private boolean dualPane;
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;
    @BindView(R.id.pbMakeBooking)
    ProgressBar progressBar;
    @BindView(R.id.pbTravelInfo)
    ProgressBar pbTravelInfo;
    private ArrayList<Integer> intArray;
    private ArrayList<Integer> intRangeArray;
    private int direction;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference usersDocumentReference;

    public MakeBookingFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking, container, false);
        ButterKnife.bind(this, view);
        setupVariables();

        if (savedInstanceState == null) {
            Bundle bundle = getArguments();
            if (bundle != null) {
                spFrom.setSelection(bundle.getInt(BUNDLE_FROM_SPINNER, 0));
                spTo.setSelection(bundle.getInt(BUNDLE_TO_SPINNER, 0));
                date.setText(bundle.getString(BUNDLE_TRIP_DATE, context.getString(R.string.select_date)));
                dualPane = bundle.getBoolean(BUNDLE_IS_DUAL_PANE);
                populateSeatsSpinner(SHUTTLE_MAX - bundle.getInt(BUNDLE_MAX_SEATS));

                if (!TextUtils.equals(date.getText(), context.getString(R.string.select_date))) {
                    calculateSeatCost(bundle.getInt(BUNDLE_FROM_SPINNER), bundle.getInt(BUNDLE_TO_SPINNER));
                    loadAvailableSeats(date.getText().toString());
                }
            }
        } else {
            dualPane = savedInstanceState.getBoolean(SAVED_DUAL_PANE);
            date.setText(savedInstanceState.getString(SAVED_DATE));

            SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFS, 0);
            populateSeatsSpinner(prefs.getInt(SHARED_PREFS_SEATS, 0));
            spSeats.setSelection(savedInstanceState.getInt(SAVED_SEATS_ARRAY));
        }

        return view;

    }

    private void setupVariables() {

        context = getActivity();
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        if (appCompatActivity != null) {
            appCompatActivity.getSupportActionBar().setTitle(R.string.make_booking);
        }

        intArray = new ArrayList<>();
        intRangeArray = new ArrayList<>();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersDocumentReference =
                firebaseFirestore.collection(FIRESTORE_USER_COLLECTION).document(firebaseAuth.getUid());

        simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format_day_month_year), Locale.getDefault());
        date.setOnClickListener(this);
        bMakeBooking.setVisibility(View.INVISIBLE);
        bMakeBooking.setOnClickListener(this);
        bCheckAvailability.setOnClickListener(this);
        ObjectAnimator animBCheck = ObjectAnimator.ofFloat(bCheckAvailability, "translationX", -400f, 0f);
        animBCheck.setDuration(400);
        animBCheck.start();

        calendar = Calendar.getInstance();
        getUserInfo();

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

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Spinner spinner = (Spinner) parent;

        switch (spinner.getId()) {

            case R.id.spPassengerInfoFrom:
                fromInt = position;
                break;

            case R.id.spPassengerInfoTo:
                toInt = position;
                break;

            case R.id.spTravelInfoSeats:
                seatsInt = position + 1;
                break;
        }

        calculateSeatCost(fromInt, toInt);
    }

    private void calculateSeatCost(int fromInt, int toInt) {

        int upstreamDownStream = toInt - fromInt;

        double costPerSeatDouble = Math.abs((upstreamDownStream) * HOP_COST);
        if (costPerSeatDouble > 0) {
            costPerSeat.setText(TextUtils.concat("R ", String.format(Locale.ENGLISH, "%.2f", costPerSeatDouble)));
            totalCost.setText(TextUtils.concat("R ", String.format(Locale.ENGLISH, "%.2f", costPerSeatDouble * seatsInt)));

            if (upstreamDownStream >= 0) {
                direction = DIRECTION_UPSTREAM;
                departureTime = Arrays.asList(getResources().getStringArray(R.array.route_stops_time_morning)).get(fromInt);
                arrivalTime = Arrays.asList(getResources().getStringArray(R.array.route_stops_time_morning)).get(toInt);
            } else {
                direction = DIRECTION_DOWNSTREAM;
                departureTime = Arrays.asList(getResources().getStringArray(R.array.route_stops_time_afternoon)).get(fromInt);
                arrivalTime = Arrays.asList(getResources().getStringArray(R.array.route_stops_time_afternoon)).get(toInt);
            }

            tripSelected = true;
            if (!TextUtils.equals(date.getText().toString(), context.getString(R.string.select_date))) {
                //bMakeBooking.setVisibility(View.VISIBLE);
                //animateBookingButton();
            }

        } else {
            costPerSeat.setText("");
            totalCost.setText("");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tvPassengerInfoNDate:
                new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, month);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                date.setText(simpleDateFormat.format(calendar.getTime()));
                                if (tripSelected) {
                                    //bMakeBooking.setVisibility(View.VISIBLE);
                                    //animateBookingButton();
                                }
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
                break;

            case R.id.bMakeBooking:

                if (spSeats.getAdapter() == null) {
                    Toast.makeText(context, R.string.first_check_availability, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (spSeats.getAdapter().isEmpty()) {
                    Toast.makeText(context, R.string.no_seats_available, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.equals(date.getText(), context.getString(R.string.select_date))) {
                    Toast.makeText(context, R.string.select_valid_date, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(totalCost.getText())) {
                    Toast.makeText(context, R.string.select_valid_trip, Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                String[] codes = context.getResources().getStringArray(R.array.route_stops_town_code);
                ArrayList<String> code = new ArrayList<>(Arrays.asList(codes));

                Date bookingDate = new Date();

                BookingInfo bookingInfo = new BookingInfo(
                        bookingDate,
                        name.getText().toString(),
                        email.getText().toString(),
                        contactNumber.getText().toString(),
                        date.getText().toString(),
                        spFrom.getSelectedItem().toString(),
                        spTo.getSelectedItem().toString(),
                        departureTime,
                        arrivalTime,
                        code.get(fromInt),
                        code.get(toInt),
                        spSeats.getSelectedItemPosition() + 1,
                        totalCost.getText().toString(),
                        0,
                        false
                );

                DocumentReference documentReference = firebaseFirestore.collection(FIRESTORE_TRAVEL_INFO_COLLECTION).document();
                documentReference
                        .set(bookingInfo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                loadComplete(getString(R.string.booking_success));
                                setAlarmNotification(date.getText().toString(), departureTime, spTo.getSelectedItem().toString());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loadComplete(getString(R.string.booking_failed));
                            }
                        });

                break;

            case R.id.bCheckAvailability:
                loadAvailableSeats(date.getText().toString());
                break;
        }
    }

    private void animateBookingButton() {
        ObjectAnimator animBMake = ObjectAnimator.ofFloat(bMakeBooking, "translationX", 400f, 0f);
        animBMake.setDuration(400);
        animBMake.start();
    }

    private void setAlarmNotification(String departureDate, String departureTime, String destination) {

        long triggerTime = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
            Date date = sdf.parse(departureDate + " " + departureTime);
            triggerTime = date.getTime() - THIRTY_MINUTES;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(BUNDLE_RECEIVER_DESTINATION, destination);

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }
        }
    }

    private void getUserInfo() {

        progressBar.setVisibility(View.VISIBLE);

        usersDocumentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                progressBar.setVisibility(View.GONE);

                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null) {
                        User user = task.getResult().toObject(User.class);
                        if (user != null) {
                            email.setText(user.getEmail());
                            name.setText(user.getName());
                            contactNumber.setText(user.getContactNumber());
                        }
                    }
                }
            }
        });
    }

    private void populateSeatsSpinner(int seats) {
        ArrayList<String> seatsList = new ArrayList<>();

        for (int i = 0; i < seats; i++) {
            seatsList.add(String.valueOf(i + 1));
        }

        ArrayAdapter<String> spSeatsAdapter = new ArrayAdapter<>(context, R.layout.spinner_item, seatsList);
        spSeatsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSeats.setAdapter(spSeatsAdapter);
        spSeats.setOnItemSelectedListener(this);
    }

    private void loadAvailableSeats(String date) {

        if (TextUtils.equals(date, context.getString(R.string.select_date))) {
            Toast.makeText(context, R.string.select_valid_date, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(totalCost.getText())) {
            Toast.makeText(context, R.string.select_valid_trip, Toast.LENGTH_SHORT).show();
            return;
        }

        final ArrayList<BookingInfo> bookingInfoArrayList = new ArrayList<>();

        pbTravelInfo.setVisibility(View.VISIBLE);

        CollectionReference collectionReference = firebaseFirestore.collection(FIRESTORE_TRAVEL_INFO_COLLECTION);
        collectionReference.whereEqualTo(FIRESTORE_TRAVEL_DATE_FIELD, date)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        pbTravelInfo.setVisibility(View.GONE);
                        bMakeBooking.setVisibility(View.VISIBLE);
                        animateBookingButton();

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
        int bookedSeats = Collections.max(intArray);
        int availableSeats = SHUTTLE_MAX - bookedSeats;
        Toast.makeText(context, String.valueOf(availableSeats) + " " + getString(R.string.seats_available), Toast.LENGTH_SHORT).show();
        //return max;
        populateSeatsSpinner(availableSeats);

        SharedPreferences.Editor prefs = context.getSharedPreferences(Constants.SHARED_PREFS, 0).edit();
        prefs.putInt(SHARED_PREFS_SEATS, availableSeats);
        prefs.apply();
    }

    private void loadComplete(String toastString) {
        Toast.makeText(context, toastString, Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_DUAL_PANE, dualPane);
        outState.putString(SAVED_DATE, date.getText().toString());
        outState.putInt(SAVED_SEATS_ARRAY, spSeats.getSelectedItemPosition());
    }


}
