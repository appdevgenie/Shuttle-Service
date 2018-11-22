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
import android.util.DisplayMetrics;
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

import static com.appdevgenie.shuttleservice.utils.Constants.BUNDLE_CLICKED_ICON;
import static com.appdevgenie.shuttleservice.utils.Constants.BUNDLE_IS_DUAL_PANE;
import static com.appdevgenie.shuttleservice.utils.Constants.BUNDLE_IS_SIGNED_IN;
import static com.appdevgenie.shuttleservice.utils.Constants.SAVED_DUAL_PANE;
import static com.appdevgenie.shuttleservice.utils.Constants.SAVED_SELECTED_ICON;
import static com.appdevgenie.shuttleservice.utils.Constants.USER_ADMIN;

public class MainIconListFragment extends Fragment implements MainIconListAdapter.ListItemClickListener {

    private View view;
    /*private TextView tvSignedInInfo;
    private ImageView ivNetworkConnectivity;*/
    private Context context;
    private MainIconListAdapter mainIconListAdapter;
    private RecyclerView recyclerView;
    private boolean isSignedIn;
    private boolean dualPane;
    private String userEmail;
    private FirebaseAuth firebaseAuth;
    private FragmentManager fragmentManager;
    private int selectedPosition = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_main_grid_recyclerview, container, false);
        context = getActivity();
        firebaseAuth = FirebaseAuth.getInstance();
        fragmentManager = getFragmentManager();

        if(savedInstanceState == null){
            Bundle bundle = getArguments();
            if(bundle != null) {
                isSignedIn = bundle.getBoolean(BUNDLE_IS_SIGNED_IN);
                dualPane = bundle.getBoolean(BUNDLE_IS_DUAL_PANE);
            }
                /*if (dualPane) {
                    MainRouteStopsFragment mainRouteStopsFragment = new MainRouteStopsFragment();
                    fragmentManager.beginTransaction().replace(R.id.fragmentInfoContainer, mainRouteStopsFragment).commit();
                    //onItemClicked(1);
                }*/
        }else{
            //onItemClicked(savedInstanceState.getInt("selectedPosition"));
            selectedPosition = savedInstanceState.getInt(SAVED_SELECTED_ICON);
            dualPane = savedInstanceState.getBoolean(SAVED_DUAL_PANE);
            //Toast.makeText(getActivity(), String.valueOf(dualPane), Toast.LENGTH_SHORT).show();
            if(dualPane){
                onItemClicked(selectedPosition);
            }
        }

        /*tvSignedInInfo = view.findViewById(R.id.tvSignedInInfo);*/


        //userEmail = firebaseAuth.getCurrentUser().getEmail();

        recyclerView = view.findViewById(R.id.rvMainLoggedOut);
        ArrayList<MainGridIcon> mainGridIcons = new ArrayList<>();
        if(!isSignedIn) {
            //tvSignedInInfo.setText("not signed in");
            mainGridIcons = CreateMainSignedOutArrayList.createMainLoggedOutIcons(context);
        }else if(TextUtils.equals(firebaseAuth.getCurrentUser().getEmail(), USER_ADMIN)){
            //tvSignedInInfo.setText("admin");
            mainGridIcons = CreateAdminSignedInArrayList.createAdminSignedInIcons(context);
            //Toast.makeText(context, "admin", Toast.LENGTH_SHORT).show();
        }else{
            //tvSignedInInfo.setText("user");
            mainGridIcons = CreateCommuterSignedInArrayList.createCommuterSignedInIcons(context);
            //Toast.makeText(context, "commuter", Toast.LENGTH_SHORT).show();
        }
        mainIconListAdapter = new MainIconListAdapter(context, this);
        mainIconListAdapter.setItems(mainGridIcons);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 4);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(mainIconListAdapter);

        /*ivNetworkConnectivity = view.findViewById(R.id.ivNetworkConnectivity);
        if(isNetworkConnected()){
            ivNetworkConnectivity.setImageResource(R.drawable.ic_cloud_on);
        }else{
            ivNetworkConnectivity.setImageResource(R.drawable.ic_cloud_off);
        }*/

        return view;

    }

    private int calculateSpan() {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 90);
    }

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
                    fragmentManager.beginTransaction().replace(R.id.fragmentInfoContainer, mainRouteStopsFragment).commit();
                    break;

                case 2:
                    MainPriceCheckFragment mainPriceCheckFragment = new MainPriceCheckFragment();
                    mainPriceCheckFragment.setArguments(bundle);
                    fragmentManager.beginTransaction().replace(R.id.fragmentInfoContainer, mainPriceCheckFragment).commit();
                    break;

                case 3:
                    ContactUsFragment contactUsFragment = new ContactUsFragment();
                    fragmentManager.beginTransaction().replace(R.id.fragmentInfoContainer, contactUsFragment).commit();
                    break;

                case 4:
                    BookingAvailabilityQueryFragment bookingAvailabilityQueryFragment = new BookingAvailabilityQueryFragment();
                    bookingAvailabilityQueryFragment.setArguments(bundle);
                    fragmentManager.beginTransaction().replace(R.id.fragmentInfoContainer, bookingAvailabilityQueryFragment).commit();
                    break;

                case 5:
                    MakeBookingFragment makeBookingFragment = new MakeBookingFragment();
                    makeBookingFragment.setArguments(bundle);
                    fragmentManager.beginTransaction().replace(R.id.fragmentInfoContainer, makeBookingFragment).commit();
                    break;

                case 6:
                    BookingHistoryFragment bookingHistoryFragment = new BookingHistoryFragment();
                    fragmentManager.beginTransaction().replace(R.id.fragmentInfoContainer, bookingHistoryFragment).commit();
                    break;

                case 7:
                    WeatherForecastFragment weatherForecastFragment = new WeatherForecastFragment();
                    fragmentManager.beginTransaction().replace(R.id.fragmentInfoContainer, weatherForecastFragment).commit();
                    break;

                case 8:
                    AccountUserInfoFragment accountUserInfoFragment = new AccountUserInfoFragment();
                    fragmentManager.beginTransaction().replace(R.id.fragmentInfoContainer, accountUserInfoFragment).commit();
                    break;

                case 9:
                    AdminTravelInfoFragment adminTravelInfoFragment = new AdminTravelInfoFragment();
                    fragmentManager.beginTransaction().replace(R.id.fragmentInfoContainer, adminTravelInfoFragment).commit();
                    break;

                case 10:
                    AdminUserAccountsFragment adminUserAccountsFragment = new AdminUserAccountsFragment();
                    fragmentManager.beginTransaction().replace(R.id.fragmentInfoContainer, adminUserAccountsFragment).commit();
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

    /*private boolean isNetworkConnected() {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }*/
}
