package com.appdevgenie.shuttleservice.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appdevgenie.shuttleservice.R;
import com.appdevgenie.shuttleservice.adapters.AdminTravelInfoAdapter;
import com.appdevgenie.shuttleservice.model.BookingInfo;
import com.appdevgenie.shuttleservice.model.TravelInfo;
import com.appdevgenie.shuttleservice.utils.CreateTravelInfoArrayList;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static com.appdevgenie.shuttleservice.utils.Constants.FIRESTORE_TRAVEL_INFO_COLLECTION;

public class AdminTravelInfoFragment extends Fragment implements View.OnClickListener {

    private Context context;
    private View view;
    private RecyclerView recyclerView;
    private AdminTravelInfoAdapter adminTravelInfoAdapter;
    private ArrayList<BookingInfo> bookingInfoArrayList;
    private ArrayList<TravelInfo> travelInfoArrayList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    private TextView tvSelectDate;
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_admin_travel_info, container, false);
        setupVariables();
        return view;
    }

    private void setupVariables() {

        context = getActivity();
        //Log.d("AdminTravel", "travel started");
        bookingInfoArrayList = new ArrayList<>();
        travelInfoArrayList = new ArrayList<>();

        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        recyclerView = view.findViewById(R.id.rvTravelInfo);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        adminTravelInfoAdapter = new AdminTravelInfoAdapter(context, travelInfoArrayList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adminTravelInfoAdapter);

        progressBar = view.findViewById(R.id.pbTravelInfo);
        //progressBar.setVisibility(View.VISIBLE);

        tvSelectDate = view.findViewById(R.id.tvSelectDate);
        tvSelectDate.setOnClickListener(this);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        /*CollectionReference collectionReference = firebaseFirestore.collection(FIRESTORE_TRAVEL_INFO_COLLECTION);
        collectionReference.whereEqualTo("date", tvSelectDate.getText().toString())
                .get()
                *//*.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d("AdminTravel", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d("AdminTravel", "Error getting documents: ", task.getException());
                        }
                    }
                });*//*
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressBar.setVisibility(View.GONE);

                        if(!queryDocumentSnapshots.isEmpty()){
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                                BookingInfo bookingInfo = documentSnapshot.toObject(BookingInfo.class);
                                bookingInfoArrayList.add(bookingInfo);
                                *//*TravelInfo travelInfo = documentSnapshot.toObject(TravelInfo.class);
                                travelInfoArrayList.add(travelInfo);*//*

                                //Log.d("AdminTravel", documentSnapshot.getId() + " => " + documentSnapshot.getData());
                            }

                            travelInfoArrayList = CreateTravelInfoArrayList.createTravelInfoList(context, bookingInfoArrayList);
                            Log.d("AdminTravel", "travelInfoArrayList " + travelInfoArrayList.size());
                            Log.d("AdminTravel", "bookingInfoArrayList " + bookingInfoArrayList.size());
                            //adminTravelInfoAdapter.notifyDataSetChanged();
                            adminTravelInfoAdapter.setAdapterData(travelInfoArrayList);
                        }
                    }
                });*/

        /*DocumentReference usersDocumentReference =
                firebaseFirestore.collection("users").document();
        usersDocumentReference
                //.whereEqualTo("date", "10 Nov 2018")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                                Log.d("AdminTravel", task.getResult().getId() + " => " + task.getResult().getData());

                        } else {
                            Log.d("AdminTravel", "error getting documents: ", task.getException());
                        }
                    }
                });*/
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
                                loadTravelInfo(simpleDateFormat.format(calendar.getTime()));
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();

                break;
        }
    }

    private void loadTravelInfo(String date) {

        progressBar.setVisibility(View.VISIBLE);

        CollectionReference collectionReference = firebaseFirestore.collection(FIRESTORE_TRAVEL_INFO_COLLECTION);
        collectionReference.whereEqualTo("date", date)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressBar.setVisibility(View.GONE);

                        //if(!queryDocumentSnapshots.isEmpty()){
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                                BookingInfo bookingInfo = documentSnapshot.toObject(BookingInfo.class);
                                bookingInfoArrayList.add(bookingInfo);
                            }
                            travelInfoArrayList = CreateTravelInfoArrayList.createTravelInfoList(context, bookingInfoArrayList);
                            adminTravelInfoAdapter.setAdapterData(travelInfoArrayList);
                        //}else{
                            //adminTravelInfoAdapter.setAdapterData(null);
                        //}
                    }
                });
    }
}
