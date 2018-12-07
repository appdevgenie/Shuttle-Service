package com.appdevgenie.shuttleservice.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appdevgenie.shuttleservice.R;
import com.appdevgenie.shuttleservice.adapters.AdminTravelInfoAdapter;
import com.appdevgenie.shuttleservice.model.BookingInfo;
import com.appdevgenie.shuttleservice.model.TravelInfo;
import com.appdevgenie.shuttleservice.utils.CheckNetworkConnection;
import com.appdevgenie.shuttleservice.utils.CreateTravelInfoArrayList;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.appdevgenie.shuttleservice.utils.Constants.FIRESTORE_TRAVEL_DATE_FIELD;
import static com.appdevgenie.shuttleservice.utils.Constants.FIRESTORE_TRAVEL_INFO_COLLECTION;

public class AdminTravelInfoFragment extends Fragment implements View.OnClickListener {

    private Context context;
    private AdminTravelInfoAdapter adminTravelInfoAdapter;
    private ArrayList<BookingInfo> bookingInfoArrayList;
    private ArrayList<TravelInfo> travelInfoArrayList;
    private FirebaseFirestore firebaseFirestore;
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;
    @BindView(R.id.pbTravelInfo)
    ProgressBar progressBar;
    @BindView(R.id.tvSelectDate)
    TextView tvSelectDate;
    @BindView(R.id.rvTravelInfo)
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_travel_info, container, false);
        ButterKnife.bind(this, view);
        setupVariables();
        return view;
    }

    private void setupVariables() {

        context = getActivity();
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        if (appCompatActivity != null) {
            appCompatActivity.getSupportActionBar().setTitle(R.string.travel_info);
        }
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat(getString(R.string.date_format_day_month_year), Locale.getDefault());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        adminTravelInfoAdapter = new AdminTravelInfoAdapter(context);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adminTravelInfoAdapter);

        tvSelectDate.setOnClickListener(this);

        firebaseFirestore = FirebaseFirestore.getInstance();

        Date date = new Date();
        String dateString = simpleDateFormat.format(date);
        tvSelectDate.setText(dateString);
        loadTravelInfo(dateString);
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

        if (!CheckNetworkConnection.isNetworkConnected(context)) {
            Toast.makeText(context, R.string.loading_cache_info, Toast.LENGTH_SHORT).show();
        }

        bookingInfoArrayList = new ArrayList<>();
        travelInfoArrayList = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);

        CollectionReference collectionReference = firebaseFirestore.collection(FIRESTORE_TRAVEL_INFO_COLLECTION);
        collectionReference.whereEqualTo(FIRESTORE_TRAVEL_DATE_FIELD, date)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressBar.setVisibility(View.GONE);
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            BookingInfo bookingInfo = documentSnapshot.toObject(BookingInfo.class);
                            bookingInfoArrayList.add(bookingInfo);
                        }
                        travelInfoArrayList = CreateTravelInfoArrayList.createTravelInfoList(context, bookingInfoArrayList);
                        adminTravelInfoAdapter.setAdapterData(travelInfoArrayList);
                    }
                });
    }
}
