package com.appdevgenie.shuttleservice.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.appdevgenie.shuttleservice.R;
import com.appdevgenie.shuttleservice.activities.MainActivity;
import com.appdevgenie.shuttleservice.adapters.RouteListAdapter;
import com.appdevgenie.shuttleservice.model.RouteStops;
import com.appdevgenie.shuttleservice.utils.CreateRouteStopArrayList;

import java.util.ArrayList;

public class MainRouteStopsFragment extends Fragment {

    private static final int UPDATE_INTERVAL = 1000;

    private View view;
    private Context context;
    private Handler handler = new Handler();
    private Runnable runnable;
    private RouteListAdapter routeListAdapter;
    //private Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_default_recyclerview, container, false);
        setupVariables();
        return view;

    }

    private void setupVariables() {

        context = getActivity();
        AppCompatActivity appCompatActivity = (AppCompatActivity)getActivity();
        if (appCompatActivity != null) {
            appCompatActivity.getSupportActionBar().setTitle(R.string.route_and_stops);
        }

        /*Toolbar toolbar = view.findViewById(R.id.toolbar);
        AppCompatActivity appCompatActivity = (AppCompatActivity)getActivity();
        if (appCompatActivity != null) {
            appCompatActivity.setSupportActionBar(toolbar);
            appCompatActivity.getSupportActionBar().setTitle(R.string.route_and_stops);
            appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                    //Toast.makeText(context, "back1", Toast.LENGTH_SHORT).show();
                }
            });
        }*/
        /*toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });*/

       // toolbar.setTitle("test");

        ArrayList<RouteStops> route = null;
        //DividerItemDecoration dividerItemDecoration = null;
        if (context != null) {
            //dividerItemDecoration = new DividerItemDecoration(context, VERTICAL);
            //dividerItemDecoration.setDrawable(context.getResources().getDrawable(R.drawable.line_divider));
            route = CreateRouteStopArrayList.createArrayList(context);
        }

        RecyclerView rvRoute = view.findViewById(R.id.recyclerViewDefault);
        routeListAdapter = new RouteListAdapter(context, route);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        //rvRoute.addItemDecoration(dividerItemDecoration);
        rvRoute.setLayoutManager(linearLayoutManager);
        rvRoute.setHasFixedSize(true);
        rvRoute.setAdapter(routeListAdapter);

    }

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                getActivity().onBackPressed();
                Toast.makeText(context, "back", Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onResume() {
        super.onResume();

        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                routeListAdapter.notifyDataSetChanged();
                handler.postDelayed(runnable, UPDATE_INTERVAL);
            }
        }, UPDATE_INTERVAL);
    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }
}
