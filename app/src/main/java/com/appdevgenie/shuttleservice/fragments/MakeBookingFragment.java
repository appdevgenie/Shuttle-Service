package com.appdevgenie.shuttleservice.fragments;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import static com.appdevgenie.shuttleservice.utils.Constants.SHUTTLE_MAX;
import static com.appdevgenie.shuttleservice.utils.Constants.THIRTY_MINUTES;

public class MakeBookingFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private View view;
    private Context context;
    private TextView name;
    private TextView email;
    private TextView contactNumber;
    private TextView date;
    private TextView costPerSeat;
    private TextView totalCost;
    private Spinner spFrom;
    private Spinner spTo;
    private Spinner spSeats;
    private Button bMakeBooking;
    private Button bCheckAvailability;
    private int fromInt;
    private int toInt;
    private int seatsInt;
    private boolean tripSelected;
    private String departureTime;
    private String arrivalTime;
    private double costPerSeatDouble;
    private boolean dualPane;
    private Calendar calendar;
    //private DatePickerDialog.OnDateSetListener dateSetListener;
    private SimpleDateFormat simpleDateFormat;
    private ProgressBar progressBar;
    private ArrayList<Integer> intArray;
    private ArrayList<Integer> intRangeArray;
    private int direction;
    private int availableSeats;
    private int maxAvailableSeats;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference usersDocumentReference;

    //private FirebaseDatabase firebaseDatabase;
    //private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_booking, container, false);

        setupVariables();

        if (savedInstanceState == null) {
            Bundle bundle = getArguments();
            if (bundle != null) {
                spFrom.setSelection(bundle.getInt(BUNDLE_FROM_SPINNER, 0));
                spTo.setSelection(bundle.getInt(BUNDLE_TO_SPINNER, 0));
                date.setText(bundle.getString(BUNDLE_TRIP_DATE, context.getString(R.string.select_date)));
                dualPane = bundle.getBoolean(BUNDLE_IS_DUAL_PANE);
                availableSeats = bundle.getInt(BUNDLE_MAX_SEATS);

                populateSeatsSpinner(SHUTTLE_MAX - availableSeats);
            }
        } else {
            dualPane = savedInstanceState.getBoolean("dualPane");
        }
        /*if(dualPane){
            bCheckAvailability.setVisibility(View.GONE);
        }*/

        return view;

    }

    private void setupVariables() {

        context = getActivity();
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        if (appCompatActivity != null) {
            appCompatActivity.getSupportActionBar().setTitle(R.string.make_booking);
        }

        /*Toolbar toolbar = view.findViewById(R.id.toolbar);
        AppCompatActivity appCompatActivity = (AppCompatActivity)getActivity();
        if (appCompatActivity != null) {
            appCompatActivity.setSupportActionBar(toolbar);
            appCompatActivity.getSupportActionBar().setTitle(R.string.make_booking);
            appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
        }*/

        intArray = new ArrayList<>();
        intRangeArray = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersDocumentReference =
                firebaseFirestore.collection(FIRESTORE_USER_COLLECTION).document(firebaseAuth.getUid());
        //firebaseDatabase = FirebaseDatabase.getInstance();
        //databaseReference = firebaseDatabase.getReference();

        simpleDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        name = view.findViewById(R.id.tvPassengerInfoName);
        email = view.findViewById(R.id.tvPassengerInfoEmail);
        contactNumber = view.findViewById(R.id.tvPassengerInfoContactNum);
        costPerSeat = view.findViewById(R.id.tvTravelCostPerSeat);
        totalCost = view.findViewById(R.id.tvTravelCostTotal);
        progressBar = view.findViewById(R.id.pbMakeBooking);
        date = view.findViewById(R.id.tvPassengerInfoNDate);
        date.setOnClickListener(this);
        bMakeBooking = view.findViewById(R.id.bMakeBooking);
        bMakeBooking.setVisibility(View.INVISIBLE);
        bMakeBooking.setOnClickListener(this);
        bCheckAvailability = view.findViewById(R.id.bCheckAvailability);
        bCheckAvailability.setOnClickListener(this);

        calendar = Calendar.getInstance();
        /*dateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                date.setText(simpleDateFormat.format(calendar.getTime()));
            }

        };*/

        //email.setText(firebaseAuth.getCurrentUser().getEmail());
        getUserInfo();

        spFrom = view.findViewById(R.id.spPassengerInfoFrom);
        ArrayAdapter<CharSequence> spFromAdapter =
                ArrayAdapter.createFromResource(context, R.array.town_names, R.layout.spinner_item);
        spFromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFrom.setAdapter(spFromAdapter);
        spFrom.setOnItemSelectedListener(this);

        spTo = view.findViewById(R.id.spPassengerInfoTo);
        ArrayAdapter<CharSequence> spToAdapter =
                ArrayAdapter.createFromResource(context, R.array.town_names, R.layout.spinner_item);
        spToAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTo.setAdapter(spToAdapter);
        spTo.setOnItemSelectedListener(this);

        spSeats = view.findViewById(R.id.spTravelInfoSeats);
        /*ArrayAdapter<CharSequence> spSeatsAdapter =
                ArrayAdapter.createFromResource(context, R.array.seats, R.layout.spinner_item);
        *//*ArrayList<String> seatsArray = createSeatsArrayList();
        ArrayAdapter<String> spSeatsAdapter = new ArrayAdapter<>(context, R.layout.spinner_item, seatsArray);*//*
        spSeatsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSeats.setAdapter(spSeatsAdapter);
        spSeats.setOnItemSelectedListener(this);*/
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

        costPerSeatDouble = Math.abs((upstreamDownStream) * HOP_COST);
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
            if(!TextUtils.equals(date.getText().toString(), context.getString(R.string.select_date)) && spSeats.getAdapter() != null) {
                bMakeBooking.setVisibility(View.VISIBLE);
                //loadAvailableSeats(date.getText().toString());
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
                                if(tripSelected  && spSeats.getAdapter() != null) {
                                    bMakeBooking.setVisibility(View.VISIBLE);
                                    //loadAvailableSeats(date.getText().toString());
                                }
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
                break;

            case R.id.bMakeBooking:

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

                //CollectionReference usersCollectionReference = firebaseFirestore.collection("users");
                /*DocumentReference usersDocumentReference =
                        firebaseFirestore.collection("users").document(firebaseAuth.getUid());*/

                /*User user = new User(
                        name.getText().toString(),
                        email.getText().toString(),
                        contactNumber.getText().toString()
                );
*/
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

                /*usersDocumentReference.set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                loadComplete("user update success");
                                //Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loadComplete(e.getMessage());
                                //Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });*/

                DocumentReference documentReference = firebaseFirestore.collection(FIRESTORE_TRAVEL_INFO_COLLECTION).document();
                documentReference
                        .set(bookingInfo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                loadComplete("Booking loaded success!");
                                setAlarmNotification(date.getText().toString(), departureTime, spTo.getSelectedItem().toString());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loadComplete("Booking failed!");
                            }
                        });


                /*usersDocumentReference.collection("bookings").document().set(bookingInfo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                loadComplete("booking added successfully!");
                               // Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loadComplete(e.getMessage());
                               // Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });*/

                //databaseReference.push();
                break;

            case R.id.bCheckAvailability:

                loadAvailableSeats(date.getText().toString());
                /*BookingAvailabilityQueryFragment bookingQueryFragment = new BookingAvailabilityQueryFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(BUNDLE_FROM_SPINNER, spFrom.getSelectedItemPosition());
                bundle.putInt(BUNDLE_TO_SPINNER, spTo.getSelectedItemPosition());
                bundle.putString(BUNDLE_TRIP_DATE, date.getText().toString());
                bundle.putBoolean(BUNDLE_IS_DUAL_PANE, dualPane);
                bookingQueryFragment.setArguments(bundle);

                if (dualPane) {
                    getActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentInfoContainer, bookingQueryFragment)
                            //.addToBackStack(null)
                            .commit();
                } else {
                    getActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainer, bookingQueryFragment)
                            //.addToBackStack(null)
                            .commit();
                }*/
                break;
        }
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

        //NotificationUtils.remindCommuterOfTrip(context, destination);
        /*String td = DateFormat.getDateInstance().format(calendar.getTime());
        Toast.makeText(context, td, Toast.LENGTH_SHORT).show();*/
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

    private void populateSeatsSpinner(int seats){
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

        progressBar.setVisibility(View.VISIBLE);

        CollectionReference collectionReference = firebaseFirestore.collection(FIRESTORE_TRAVEL_INFO_COLLECTION);
        collectionReference.whereEqualTo(FIRESTORE_TRAVEL_DATE_FIELD, date)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressBar.setVisibility(View.GONE);

                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                            BookingInfo bookingInfo = documentSnapshot.toObject(BookingInfo.class);
                            bookingInfoArrayList.add(bookingInfo);
                        }
                        intRangeArray = CreateTravelInfoArrayList.createPassengerMaxList(context, bookingInfoArrayList);
                        if(direction == DIRECTION_UPSTREAM){
                            getMaxSeats(fromInt, toInt);
                        }
                        if(direction == DIRECTION_DOWNSTREAM){
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
        availableSeats = SHUTTLE_MAX - bookedSeats;
        Toast.makeText(context, String.valueOf(availableSeats) + " seats available", Toast.LENGTH_SHORT).show();
        //return max;
        populateSeatsSpinner(availableSeats);
    }

    private void loadComplete(String toastString) {
        Toast.makeText(context, toastString, Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("dualPane", dualPane);
    }
}
