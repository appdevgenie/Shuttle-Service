package com.appdevgenie.shuttleservice.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appdevgenie.shuttleservice.R;
import com.appdevgenie.shuttleservice.activities.LoginActivity;
import com.appdevgenie.shuttleservice.activities.MainSelectionActivity;
import com.appdevgenie.shuttleservice.adapters.MainIconListAdapter;
import com.appdevgenie.shuttleservice.model.MainGridIcon;
import com.appdevgenie.shuttleservice.utils.CreateAdminSignedInArrayList;
import com.appdevgenie.shuttleservice.utils.CreateCommuterSignedInArrayList;
import com.appdevgenie.shuttleservice.utils.CreateMainSignedOutArrayList;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.appdevgenie.shuttleservice.utils.Constants.BUNDLE_CLICKED_ICON;
import static com.appdevgenie.shuttleservice.utils.Constants.BUNDLE_IS_DUAL_PANE;
import static com.appdevgenie.shuttleservice.utils.Constants.BUNDLE_IS_SIGNED_IN;
import static com.appdevgenie.shuttleservice.utils.Constants.SAVED_DUAL_PANE;
import static com.appdevgenie.shuttleservice.utils.Constants.SAVED_SELECTED_ICON;
import static com.appdevgenie.shuttleservice.utils.Constants.USER_ADMIN;

public class MainIconListFragment extends Fragment implements MainIconListAdapter.ListItemClickListener {

    private Context context;
    @BindView(R.id.rvMainLoggedOut)
    RecyclerView recyclerView;
    private boolean isSignedIn;
    private boolean dualPane;
    private FirebaseAuth firebaseAuth;
    private int selectedPosition = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_grid_recyclerview, container, false);
        ButterKnife.bind(this, view);
        context = getActivity();
        firebaseAuth = FirebaseAuth.getInstance();

        if(savedInstanceState == null){
            Bundle bundle = getArguments();
            if(bundle != null) {
                isSignedIn = bundle.getBoolean(BUNDLE_IS_SIGNED_IN);
                dualPane = bundle.getBoolean(BUNDLE_IS_DUAL_PANE);
            }
        }else{
            selectedPosition = savedInstanceState.getInt(SAVED_SELECTED_ICON);
            dualPane = savedInstanceState.getBoolean(SAVED_DUAL_PANE);
            /*if(dualPane){
                onItemClicked(selectedPosition);
            }*/
        }

        ArrayList<MainGridIcon> mainGridIcons;
        if(!isSignedIn) {
            mainGridIcons = CreateMainSignedOutArrayList.createMainLoggedOutIcons(context);
        }else if(TextUtils.equals(firebaseAuth.getCurrentUser().getEmail(), USER_ADMIN)){
            mainGridIcons = CreateAdminSignedInArrayList.createAdminSignedInIcons(context);
        }else{
            mainGridIcons = CreateCommuterSignedInArrayList.createCommuterSignedInIcons(context);
        }
        MainIconListAdapter mainIconListAdapter = new MainIconListAdapter(context, this);
        mainIconListAdapter.setItems(mainGridIcons);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 4);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(mainIconListAdapter);

        return view;

    }

    /*private int calculateSpan() {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 90);
    }*/

    /*private Date getDateNearest(List<Date> dates, Date targetDate) {
        Date returnDate = null; // Nearest before.
        for (Date dateSetListener : dates) {
            if (dateSetListener.after(targetDate) || dateSetListener.equals(targetDate)) {
                if (returnDate == null || returnDate.after(dateSetListener)) {
                    returnDate = dateSetListener;
                }
            }
        }
        return returnDate;
    }*/

    @Override
    public void onItemClicked(int position) {

        selectedPosition = position;

        Bundle bundle = new Bundle();
        bundle.putBoolean(BUNDLE_IS_DUAL_PANE, dualPane);

        FragmentManager fragmentManager = getFragmentManager();

        if(!dualPane) {
            switch (position) {
                case 0:
                    if (!isSignedIn) {
                        startActivity(new Intent(context, LoginActivity.class));
                        getActivity().finish();
                    } else {
                        firebaseAuth.signOut();
                    }
                    break;

                default:
                    Intent intent = new Intent(context, MainSelectionActivity.class);
                    intent.putExtra(BUNDLE_CLICKED_ICON, position);
                    startActivity(intent);
                    break;
            }
        }else{
            switch (position) {
                case 0:
                    if (!isSignedIn) {
                        startActivity(new Intent(context, LoginActivity.class));
                        getActivity().finish();
                    } else {
                        firebaseAuth.signOut();
                    }
                    break;

                case 1:
                    MainRouteStopsFragment mainRouteStopsFragment = new MainRouteStopsFragment();
                    if (fragmentManager != null) {
                        fragmentManager.beginTransaction().replace(R.id.fragmentInfoContainer, mainRouteStopsFragment).commit();
                    }
                    break;

                case 2:
                    MainPriceCheckFragment mainPriceCheckFragment = new MainPriceCheckFragment();
                    mainPriceCheckFragment.setArguments(bundle);
                    if (fragmentManager != null) {
                        fragmentManager.beginTransaction().replace(R.id.fragmentInfoContainer, mainPriceCheckFragment).commit();
                    }
                    break;

                case 3:
                    ContactUsFragment contactUsFragment = new ContactUsFragment();
                    if (fragmentManager != null) {
                        fragmentManager.beginTransaction().replace(R.id.fragmentInfoContainer, contactUsFragment).commit();
                    }
                    break;

                case 4:
                    BookingAvailabilityQueryFragment bookingAvailabilityQueryFragment = new BookingAvailabilityQueryFragment();
                    bookingAvailabilityQueryFragment.setArguments(bundle);
                    if (fragmentManager != null) {
                        fragmentManager.beginTransaction().replace(R.id.fragmentInfoContainer, bookingAvailabilityQueryFragment).commit();
                    }
                    break;

                case 5:
                    MakeBookingFragment makeBookingFragment = new MakeBookingFragment();
                    makeBookingFragment.setArguments(bundle);
                    if (fragmentManager != null) {
                        fragmentManager.beginTransaction().replace(R.id.fragmentInfoContainer, makeBookingFragment).commit();
                    }
                    break;

                case 6:
                    BookingHistoryFragment bookingHistoryFragment = new BookingHistoryFragment();
                    if (fragmentManager != null) {
                        fragmentManager.beginTransaction().replace(R.id.fragmentInfoContainer, bookingHistoryFragment).commit();
                    }
                    break;

                case 7:
                    WeatherForecastFragment weatherForecastFragment = new WeatherForecastFragment();
                    if (fragmentManager != null) {
                        fragmentManager.beginTransaction().replace(R.id.fragmentInfoContainer, weatherForecastFragment).commit();
                    }
                    break;

                case 8:
                    AccountUserInfoFragment accountUserInfoFragment = new AccountUserInfoFragment();
                    if (fragmentManager != null) {
                        fragmentManager.beginTransaction().replace(R.id.fragmentInfoContainer, accountUserInfoFragment).commit();
                    }
                    break;

                case 9:
                    AdminTravelInfoFragment adminTravelInfoFragment = new AdminTravelInfoFragment();
                    if (fragmentManager != null) {
                        fragmentManager.beginTransaction().replace(R.id.fragmentInfoContainer, adminTravelInfoFragment).commit();
                    }
                    break;

                case 10:
                    AdminUserAccountsFragment adminUserAccountsFragment = new AdminUserAccountsFragment();
                    if (fragmentManager != null) {
                        fragmentManager.beginTransaction().replace(R.id.fragmentInfoContainer, adminUserAccountsFragment).commit();
                    }
                    break;
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_SELECTED_ICON, selectedPosition);
        outState.putBoolean(SAVED_DUAL_PANE, dualPane);
    }
}
