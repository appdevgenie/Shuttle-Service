package com.appdevgenie.shuttleservice.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

import static com.appdevgenie.shuttleservice.utils.Constants.EXTRA_PARSE_CLICKED_ICON;
import static com.appdevgenie.shuttleservice.utils.Constants.EXTRA_PARSE_IS_SIGNED_IN;
import static com.appdevgenie.shuttleservice.utils.Constants.USER_ADMIN;

public class MainIconListFragment extends Fragment implements MainIconListAdapter.ListItemClickListener {

    private View view;
    private TextView tvSignedInInfo;
    private Context context;
    private MainIconListAdapter mainIconListAdapter;
    private RecyclerView recyclerView;
    private boolean isSignedIn;
    private String userEmail;
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.main_grid_recyclerview, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        context = getActivity();

        tvSignedInInfo = view.findViewById(R.id.tvSignedInInfo);

        Bundle bundle = getArguments();
        if(bundle != null){
            isSignedIn = bundle.getBoolean(EXTRA_PARSE_IS_SIGNED_IN);
        }
        //userEmail = firebaseAuth.getCurrentUser().getEmail();

        recyclerView = view.findViewById(R.id.rvMainLoggedOut);
        ArrayList<MainGridIcon> mainGridIcons = new ArrayList<>();
        if(!isSignedIn) {
            tvSignedInInfo.setText("not signed in");
            mainGridIcons = CreateMainSignedOutArrayList.createMainLoggedOutIcons(context);
        }else if(TextUtils.equals(firebaseAuth.getCurrentUser().getEmail(), USER_ADMIN)){
            tvSignedInInfo.setText("admin");
            mainGridIcons = CreateAdminSignedInArrayList.createAdminSignedInIcons(context);
            //Toast.makeText(context, "admin", Toast.LENGTH_SHORT).show();
        }else{
            tvSignedInInfo.setText("user");
            mainGridIcons = CreateCommuterSignedInArrayList.createCommuterSignedInIcons(context);
            //Toast.makeText(context, "commuter", Toast.LENGTH_SHORT).show();
        }
        mainIconListAdapter = new MainIconListAdapter(context, this);
        mainIconListAdapter.setItems(mainGridIcons);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, calculateSpan());
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(mainIconListAdapter);

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

        switch (position){

            case 0:
                if(!isSignedIn) {
                    startActivity(new Intent(context, LoginActivity.class));
                    getActivity().finish();
                }else{
                    firebaseAuth.signOut();
                }
                break;

            default:
                Intent intent = new Intent(context, MainSelectionActivity.class);
                intent.putExtra(EXTRA_PARSE_CLICKED_ICON, position);
                startActivity(intent);
                break;
        }
    }
}
