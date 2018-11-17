package com.appdevgenie.shuttleservice.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import com.appdevgenie.shuttleservice.R;
import com.appdevgenie.shuttleservice.adapters.AdminUserAccountsAdapter;
import com.appdevgenie.shuttleservice.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.appdevgenie.shuttleservice.utils.Constants.FIRESTORE_USER_COLLECTION;

public class AdminUserAccountsFragment extends Fragment implements AdminUserAccountsAdapter.EmailClickListener, AdminUserAccountsAdapter.PhoneClickListener {

    private View view;
    private Context context;
    private AdminUserAccountsAdapter adminUserAccountsAdapter;
    private ProgressBar progressBar;
    private ArrayList<User> userArrayList;

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
        if (appCompatActivity != null) {
            appCompatActivity.setSupportActionBar(toolbar);
            appCompatActivity.getSupportActionBar().setTitle(R.string.user_accounts);
            appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
        }

        userArrayList = new ArrayList<>();

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewDefault);
        adminUserAccountsAdapter = new AdminUserAccountsAdapter(context, userArrayList, this, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adminUserAccountsAdapter);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        CollectionReference usersCollection = firebaseFirestore.collection(FIRESTORE_USER_COLLECTION);

        usersCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                progressBar.setVisibility(View.GONE);
                if(!queryDocumentSnapshots.isEmpty()){

                    List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();

                    for(DocumentSnapshot documentSnapshot : documentSnapshotList){
                        User user = documentSnapshot.toObject(User.class);
                        userArrayList.add(user);
                    }

                    adminUserAccountsAdapter.notifyDataSetChanged();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onEmailClicked(String email) {
        //Toast.makeText(context, email, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_EMAIL, email);
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));

        startActivity(Intent.createChooser(intent, "Email"));
    }

    @Override
    public void onPhoneClicked(String phone) {
        //Toast.makeText(context, phone, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }
}
