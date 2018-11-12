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
import com.appdevgenie.shuttleservice.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.appdevgenie.shuttleservice.utils.Constants.FIRESTORE_TRAVEL_INFO_COLLECTION;
import static com.appdevgenie.shuttleservice.utils.Constants.HOP_COST;

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
    private int fromInt;
    private int toInt;
    private int seatsInt;
    private String departureTime;
    private String arrivalTime;
    double costPerSeatDouble;
    private Calendar calendar;
    //private DatePickerDialog.OnDateSetListener dateSetListener;
    private SimpleDateFormat simpleDateFormat;
    private ProgressBar progressBar;

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

        return view;

    }

    private void setupVariables() {

        context = getActivity();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        usersDocumentReference =
                firebaseFirestore.collection("users").document(firebaseAuth.getUid());
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
        bMakeBooking.setOnClickListener(this);

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
                ArrayAdapter.createFromResource(context, R.array.town_names, android.R.layout.simple_spinner_item);
        spFromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFrom.setAdapter(spFromAdapter);
        spFrom.setOnItemSelectedListener(this);

        spTo = view.findViewById(R.id.spPassengerInfoTo);
        ArrayAdapter<CharSequence> spToAdapter =
                ArrayAdapter.createFromResource(context, R.array.town_names, android.R.layout.simple_spinner_item);
        spToAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTo.setAdapter(spToAdapter);
        spTo.setOnItemSelectedListener(this);

        spSeats = view.findViewById(R.id.spTravelInfoSeats);
        ArrayAdapter<CharSequence> spSeatsAdapter =
                ArrayAdapter.createFromResource(context, R.array.seats, android.R.layout.simple_spinner_item);
        spSeatsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSeats.setAdapter(spSeatsAdapter);
        spSeats.setOnItemSelectedListener(this);
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

            if(upstreamDownStream >= 0){
                departureTime = Arrays.asList(getResources().getStringArray(R.array.route_stops_time_morning)).get(fromInt);
                arrivalTime = Arrays.asList(getResources().getStringArray(R.array.route_stops_time_morning)).get(toInt);
            }else{
                departureTime = Arrays.asList(getResources().getStringArray(R.array.route_stops_time_afternoon)).get(fromInt);
                arrivalTime = Arrays.asList(getResources().getStringArray(R.array.route_stops_time_afternoon)).get(toInt);
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
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
                break;

            case R.id.bMakeBooking:

                if(TextUtils.equals(date.getText(), "Select date")){
                    Toast.makeText(context, "Select valid date", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(totalCost.getText())){
                    Toast.makeText(context, "Select valid trip", Toast.LENGTH_SHORT).show();
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
                                loadComplete("yes!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loadComplete("no!");
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

    private void loadComplete(String toastString) {
        Toast.makeText(context, toastString, Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
    }
}
