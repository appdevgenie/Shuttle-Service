package com.appdevgenie.shuttleservice.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appdevgenie.shuttleservice.R;
import com.appdevgenie.shuttleservice.adapters.RouteListAdapter;
import com.appdevgenie.shuttleservice.model.RouteStops;
import com.appdevgenie.shuttleservice.utils.CreateRouteStopArrayList;

import java.util.ArrayList;

import static android.widget.LinearLayout.VERTICAL;

public class MainRouteStopsFragment extends Fragment {

    private static final int UPDATE_INTERVAL = 1000;

    private View view;
    private Handler handler = new Handler();
    private Runnable runnable;
    private RouteListAdapter routeListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_list_route_and_stops, container, false);
        setupVariables();
        return view;

    }

    private void setupVariables() {

        final Context context = getActivity();

        ArrayList<RouteStops> route = null;
        //DividerItemDecoration dividerItemDecoration = null;
        if (context != null) {
            //dividerItemDecoration = new DividerItemDecoration(context, VERTICAL);
            //dividerItemDecoration.setDrawable(context.getResources().getDrawable(R.drawable.line_divider));
            route = CreateRouteStopArrayList.createArrayList(context);
        }

        RecyclerView rvRoute = view.findViewById(R.id.rvRoute);
        routeListAdapter = new RouteListAdapter(context, route);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        //rvRoute.addItemDecoration(dividerItemDecoration);
        rvRoute.setLayoutManager(linearLayoutManager);
        rvRoute.setHasFixedSize(true);
        rvRoute.setAdapter(routeListAdapter);

    }

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
