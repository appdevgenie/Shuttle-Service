package com.appdevgenie.shuttleservice.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.appdevgenie.shuttleservice.R;
import com.appdevgenie.shuttleservice.adapters.BookingHistoryAdapter;
import com.appdevgenie.shuttleservice.model.BookingInfo;
import com.appdevgenie.shuttleservice.utils.Constants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.appdevgenie.shuttleservice.utils.Constants.SHARED_PREFS_DATE;
import static com.appdevgenie.shuttleservice.utils.Constants.SHARED_PREFS_FROM_TIME;
import static com.appdevgenie.shuttleservice.utils.Constants.SHARED_PREFS_FROM_TOWN;
import static com.appdevgenie.shuttleservice.utils.Constants.SHARED_PREFS_SEATS;
import static com.appdevgenie.shuttleservice.utils.Constants.SHARED_PREFS_TO_TIME;
import static com.appdevgenie.shuttleservice.utils.Constants.SHARED_PREFS_TO_TOWN;

public class BookingHistoryFragment extends Fragment implements BookingHistoryAdapter.ItemLongClickListener {

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

        view = inflater.inflate(R.layout.fragment_booking_history, container, false);
        setupVariables();
        return view;
    }

    private void setupVariables() {

        context = getActivity();
        bookingInfoArrayList = new ArrayList<>();

        progressBar = view.findViewById(R.id.pbBookings);

        recyclerView = view.findViewById(R.id.rvBookingHistory);
        bookingHistoryAdapter = new BookingHistoryAdapter(context, bookingInfoArrayList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(bookingHistoryAdapter);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        /*CollectionReference collectionReference = firebaseFirestore.collection("users");
        DocumentReference documentReference = collectionReference.document(firebaseAuth.getUid());*/
        DocumentReference usersDocumentReference =
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
        });
    }

    @Override
    public void onItemLongClick(int pos) {

        SharedPreferences.Editor prefs = context.getSharedPreferences(Constants.SHARED_PREFS, 0).edit();
        prefs.putString(SHARED_PREFS_FROM_TOWN, bookingInfoArrayList.get(pos).getFromTown());
        prefs.putString(SHARED_PREFS_TO_TOWN, bookingInfoArrayList.get(pos).getToTown());
        //prefs.putString(SHARED_PREFS_FROM_TIME, bookingInfoArrayList.get(pos).getFromTown());
        //prefs.putString(SHARED_PREFS_TO_TIME, bookingInfoArrayList.get(pos).getToTown());
        prefs.putString(SHARED_PREFS_DATE, bookingInfoArrayList.get(pos).getDate());
        prefs.putString(SHARED_PREFS_SEATS, bookingInfoArrayList.get(pos).getSeats());
        prefs.apply();

        Toast.makeText(context, "added to widget", Toast.LENGTH_SHORT).show();
    }
}
