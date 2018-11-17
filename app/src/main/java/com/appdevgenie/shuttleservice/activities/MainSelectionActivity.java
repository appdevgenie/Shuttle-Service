package com.appdevgenie.shuttleservice.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.appdevgenie.shuttleservice.R;
import com.appdevgenie.shuttleservice.fragments.AccountUserInfoFragment;
import com.appdevgenie.shuttleservice.fragments.AdminTravelInfoFragment;
import com.appdevgenie.shuttleservice.fragments.AdminUserAccountsFragment;
import com.appdevgenie.shuttleservice.fragments.BookingAvailabilityQueryFragment;
import com.appdevgenie.shuttleservice.fragments.BookingHistoryFragment;
import com.appdevgenie.shuttleservice.fragments.MainPriceCheckFragment;
import com.appdevgenie.shuttleservice.fragments.MainRouteStopsFragment;
import com.appdevgenie.shuttleservice.fragments.MakeBookingFragment;
import com.appdevgenie.shuttleservice.fragments.WeatherForecastFragment;

import static com.appdevgenie.shuttleservice.utils.Constants.EXTRA_PARSE_CLICKED_ICON;

public class MainSelectionActivity extends AppCompatActivity {

    //private boolean isLoggedIn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);

        FragmentManager fragmentManager = getSupportFragmentManager();

        if(savedInstanceState == null) {
            Intent intent = getIntent();
            if(intent != null) {
                //MainGridIcon mainGridIcon = intent.getParcelableExtra("extra_selection");
                int position = intent.getIntExtra(EXTRA_PARSE_CLICKED_ICON, 1);
                //isLoggedIn = intent.getBooleanExtra("isLoggedIn", isLoggedIn);
                switch (position){

                    case 1:
                        MainRouteStopsFragment mainRouteStopsFragment = new MainRouteStopsFragment();
                        fragmentManager.beginTransaction().replace(R.id.mainActivityContainer, mainRouteStopsFragment).commit();
                        break;

                    case 2:
                        MainPriceCheckFragment mainPriceCheckFragment = new MainPriceCheckFragment();
                        fragmentManager.beginTransaction().replace(R.id.mainActivityContainer, mainPriceCheckFragment).commit();
                        break;

                    case 3:
                        break;

                    case 4:
                        BookingAvailabilityQueryFragment bookingAvailabilityQueryFragment = new BookingAvailabilityQueryFragment();
                        fragmentManager.beginTransaction().replace(R.id.mainActivityContainer, bookingAvailabilityQueryFragment).commit();
                        break;

                    case 5:
                        MakeBookingFragment makeBookingFragment = new MakeBookingFragment();
                        fragmentManager.beginTransaction().replace(R.id.mainActivityContainer, makeBookingFragment).commit();
                        break;

                    case 6:
                        BookingHistoryFragment bookingHistoryFragment = new BookingHistoryFragment();
                        fragmentManager.beginTransaction().replace(R.id.mainActivityContainer, bookingHistoryFragment).commit();
                        break;

                    case 7:
                        WeatherForecastFragment weatherForecastFragment = new WeatherForecastFragment();
                        fragmentManager.beginTransaction().replace(R.id.mainActivityContainer, weatherForecastFragment).commit();
                        break;

                    case 8:
                        AccountUserInfoFragment accountUserInfoFragment = new AccountUserInfoFragment();
                        fragmentManager.beginTransaction().replace(R.id.mainActivityContainer, accountUserInfoFragment).commit();
                        break;

                    case 9:
                        AdminTravelInfoFragment adminTravelInfoFragment = new AdminTravelInfoFragment();
                        fragmentManager.beginTransaction().replace(R.id.mainActivityContainer, adminTravelInfoFragment).commit();
                        break;

                    case 10:
                        AdminUserAccountsFragment adminUserAccountsFragment = new AdminUserAccountsFragment();
                        fragmentManager.beginTransaction().replace(R.id.mainActivityContainer, adminUserAccountsFragment).commit();
                        break;
                }

            }

        }
    }
}
