package com.appdevgenie.shuttleservice.fragments;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.appdevgenie.shuttleservice.R;
import com.appdevgenie.shuttleservice.adapters.BookingHistoryAdapter;
import com.appdevgenie.shuttleservice.model.BookingInfo;
import com.appdevgenie.shuttleservice.utils.Constants;
import com.appdevgenie.shuttleservice.widget.ShuttleAppWidgetProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.appdevgenie.shuttleservice.utils.Constants.FIRESTORE_BOOKING_DATE_FIELD;
import static com.appdevgenie.shuttleservice.utils.Constants.FIRESTORE_TRAVEL_INFO_COLLECTION;
import static com.appdevgenie.shuttleservice.utils.Constants.FIRESTORE_USER_EMAIL_FIELD;
import static com.appdevgenie.shuttleservice.utils.Constants.SHARED_PREFS_DATE;
import static com.appdevgenie.shuttleservice.utils.Constants.SHARED_PREFS_DEPART_TIME;
import static com.appdevgenie.shuttleservice.utils.Constants.SHARED_PREFS_FROM_TOWN;
import static com.appdevgenie.shuttleservice.utils.Constants.SHARED_PREFS_SEATS;
import static com.appdevgenie.shuttleservice.utils.Constants.SHARED_PREFS_ARRIVE_TIME;
import static com.appdevgenie.shuttleservice.utils.Constants.SHARED_PREFS_TO_TOWN;

public class BookingHistoryFragment extends Fragment implements BookingHistoryAdapter.ItemClickWidgetListener {

    private View view;
    private Context context;
    private BookingHistoryAdapter bookingHistoryAdapter;
    private RecyclerView recyclerView;
    private ArrayList<BookingInfo> bookingInfoArrayList;
    private ProgressBar progressBar;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_default_recyclerview, container, false);
        setupVariables();
        return view;
    }

    private void setupVariables() {

        context = getActivity();

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        AppCompatActivity appCompatActivity = (AppCompatActivity)getActivity();
        appCompatActivity.setSupportActionBar(toolbar);
        appCompatActivity.getSupportActionBar().setTitle("Booking history");
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        bookingInfoArrayList = new ArrayList<>();

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView = view.findViewById(R.id.recyclerViewDefault);
        bookingHistoryAdapter = new BookingHistoryAdapter(context, bookingInfoArrayList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(bookingHistoryAdapter);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        /*CollectionReference collectionReference = firebaseFirestore.collection("users");
        DocumentReference documentReference = collectionReference.document(firebaseAuth.getUid());*/

        CollectionReference collectionReference = firebaseFirestore.collection(FIRESTORE_TRAVEL_INFO_COLLECTION);
        collectionReference.whereEqualTo(FIRESTORE_USER_EMAIL_FIELD, firebaseAuth.getCurrentUser().getEmail())
        //collectionReference
                //.orderBy(FIRESTORE_BOOKING_DATE_FIELD, Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        progressBar.setVisibility(View.GONE);

                        if (!queryDocumentSnapshots.isEmpty()) {

                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot documentSnapshot : list) {
                                BookingInfo bookingInfo = documentSnapshot.toObject(BookingInfo.class);
                                bookingInfoArrayList.add(bookingInfo);
                            }
                            bookingHistoryAdapter.notifyDataSetChanged();
                        }
                    }
                });

        /*DocumentReference usersDocumentReference =
                firebaseFirestore.collection("users").document(firebaseAuth.getUid());

        usersDocumentReference.collection("bookings")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                progressBar.setVisibility(View.GONE);

                if (!queryDocumentSnapshots.isEmpty()) {

                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                    for (DocumentSnapshot documentSnapshot : list) {

                        BookingInfo bookingInfo = documentSnapshot.toObject(BookingInfo.class);
                        bookingInfoArrayList.add(bookingInfo);

                    }
                    bookingHistoryAdapter.notifyDataSetChanged();
                }
            }
        });*/
    }

    @Override
    public void onItemWidgetClick(int pos) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        Bundle extras = new Bundle();
        int widgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        SharedPreferences.Editor prefs = context.getSharedPreferences(Constants.SHARED_PREFS, 0).edit();
        prefs.putString(SHARED_PREFS_FROM_TOWN, bookingInfoArrayList.get(pos).getFromTown());
        prefs.putString(SHARED_PREFS_TO_TOWN, bookingInfoArrayList.get(pos).getToTown());
        prefs.putString(SHARED_PREFS_DEPART_TIME, bookingInfoArrayList.get(pos).getDepartureTime());
        prefs.putString(SHARED_PREFS_ARRIVE_TIME, bookingInfoArrayList.get(pos).getArrivalTime());
        prefs.putString(SHARED_PREFS_DATE, bookingInfoArrayList.get(pos).getDate());
        prefs.putString(SHARED_PREFS_SEATS, String.valueOf(bookingInfoArrayList.get(pos).getSeats()));
        prefs.apply();

        ShuttleAppWidgetProvider.updateAppWidget(context, appWidgetManager, widgetId);

        Toast.makeText(context, "added to widget", Toast.LENGTH_SHORT).show();
    }

   /* @Override
    public void onItemLongClick(int pos) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        Bundle extras = new Bundle();
        int widgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        SharedPreferences.Editor prefs = context.getSharedPreferences(Constants.SHARED_PREFS, 0).edit();
        prefs.putString(SHARED_PREFS_FROM_TOWN, bookingInfoArrayList.get(pos).getFromTown());
        prefs.putString(SHARED_PREFS_TO_TOWN, bookingInfoArrayList.get(pos).getToTown());
        prefs.putString(SHARED_PREFS_DEPART_TIME, bookingInfoArrayList.get(pos).getDepartureTime());
        prefs.putString(SHARED_PREFS_ARRIVE_TIME, bookingInfoArrayList.get(pos).getArrivalTime());
        prefs.putString(SHARED_PREFS_DATE, bookingInfoArrayList.get(pos).getDate());
        prefs.putString(SHARED_PREFS_SEATS, bookingInfoArrayList.get(pos).getSeats());
        prefs.apply();

        ShuttleAppWidgetProvider.updateAppWidget(context, appWidgetManager, widgetId);

        Toast.makeText(context, "added to widget", Toast.LENGTH_SHORT).show();
    }*/
}
