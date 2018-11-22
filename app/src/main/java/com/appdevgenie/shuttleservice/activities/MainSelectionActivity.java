package com.appdevgenie.shuttleservice.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.appdevgenie.shuttleservice.R;
import com.appdevgenie.shuttleservice.fragments.AccountUserInfoFragment;
import com.appdevgenie.shuttleservice.fragments.AdminTravelInfoFragment;
import com.appdevgenie.shuttleservice.fragments.AdminUserAccountsFragment;
import com.appdevgenie.shuttleservice.fragments.BookingAvailabilityQueryFragment;
import com.appdevgenie.shuttleservice.fragments.BookingHistoryFragment;
import com.appdevgenie.shuttleservice.fragments.ContactUsFragment;
import com.appdevgenie.shuttleservice.fragments.MainPriceCheckFragment;
import com.appdevgenie.shuttleservice.fragments.MainRouteStopsFragment;
import com.appdevgenie.shuttleservice.fragments.MakeBookingFragment;
import com.appdevgenie.shuttleservice.fragments.WeatherForecastFragment;

import static com.appdevgenie.shuttleservice.utils.Constants.BUNDLE_CLICKED_ICON;

public class MainSelectionActivity extends AppCompatActivity {

    //only visible on phones

    //private boolean isLoggedIn;
    //private String toolbarTitle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                //Toast.makeText(context, "back1", Toast.LENGTH_SHORT).show();
            }
        });

        if(savedInstanceState == null) {
            Intent intent = getIntent();
            if(intent != null) {
                //MainGridIcon mainGridIcon = intent.getParcelableExtra("extra_selection");
                int position = intent.getIntExtra(BUNDLE_CLICKED_ICON, 1);
                FragmentManager fragmentManager = getSupportFragmentManager();
                //isLoggedIn = intent.getBooleanExtra("isLoggedIn", isLoggedIn);
                switch (position){

                    case 1:
                        MainRouteStopsFragment mainRouteStopsFragment = new MainRouteStopsFragment();
                        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, mainRouteStopsFragment).commit();
                        //toolbarTitle = getString(R.string.route_and_stops);
                        break;

                    case 2:
                        MainPriceCheckFragment mainPriceCheckFragment = new MainPriceCheckFragment();
                        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, mainPriceCheckFragment).commit();
                        //toolbarTitle = getString(R.string.price_check);
                        break;

                    case 3:
                        ContactUsFragment contactUsFragment = new ContactUsFragment();
                        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, contactUsFragment).commit();
                        //toolbarTitle = getString(R.string.contact_info);
                        break;

                    case 4:
                        BookingAvailabilityQueryFragment bookingAvailabilityQueryFragment = new BookingAvailabilityQueryFragment();
                        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, bookingAvailabilityQueryFragment).commit();
                        //toolbarTitle = getString(R.string.availability);
                        break;

                    case 5:
                        MakeBookingFragment makeBookingFragment = new MakeBookingFragment();
                        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, makeBookingFragment).commit();
                        //toolbarTitle = getString(R.string.make_booking);
                        break;

                    case 6:
                        BookingHistoryFragment bookingHistoryFragment = new BookingHistoryFragment();
                        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, bookingHistoryFragment).commit();
                        //toolbarTitle = getString(R.string.booking_history);
                        break;

                    case 7:
                        WeatherForecastFragment weatherForecastFragment = new WeatherForecastFragment();
                        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, weatherForecastFragment).commit();
                        //toolbarTitle = getString(R.string.weather);
                        break;

                    case 8:
                        AccountUserInfoFragment accountUserInfoFragment = new AccountUserInfoFragment();
                        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, accountUserInfoFragment).commit();
                        //toolbarTitle = getString(R.string.user_account);
                        break;

                    case 9:
                        AdminTravelInfoFragment adminTravelInfoFragment = new AdminTravelInfoFragment();
                        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, adminTravelInfoFragment).commit();
                        //toolbarTitle = getString(R.string.travel_info);
                        break;

                    case 10:
                        AdminUserAccountsFragment adminUserAccountsFragment = new AdminUserAccountsFragment();
                        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, adminUserAccountsFragment).commit();
                        //toolbarTitle = getString(R.string.user_accounts);
                        break;
                }

                //getSupportActionBar().setTitle(toolbarTitle);

            }

        }
    }
}
