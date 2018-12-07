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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.appdevgenie.shuttleservice.R;
import com.appdevgenie.shuttleservice.adapters.AdminUserAccountsAdapter;
import com.appdevgenie.shuttleservice.model.User;
import com.appdevgenie.shuttleservice.utils.CheckNetworkConnection;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.appdevgenie.shuttleservice.utils.Constants.FIRESTORE_USER_COLLECTION;

public class AdminUserAccountsFragment extends Fragment
        implements AdminUserAccountsAdapter.EmailClickListener, AdminUserAccountsAdapter.PhoneClickListener {

    private View view;
    private Context context;
    private AdminUserAccountsAdapter adminUserAccountsAdapter;
    private ArrayList<User> userArrayList;
    @BindView(R.id.recyclerViewDefault)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_default_recyclerview, container, false);
        ButterKnife.bind(this, view);
        setupVariables();
        return view;
    }

    private void setupVariables() {

        context = getActivity();
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        if (appCompatActivity != null) {
            appCompatActivity.getSupportActionBar().setTitle(R.string.user_account);
        }

        userArrayList = new ArrayList<>();

        progressBar.setVisibility(View.VISIBLE);
        adminUserAccountsAdapter = new AdminUserAccountsAdapter(context, userArrayList, this, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adminUserAccountsAdapter);

        if (!CheckNetworkConnection.isNetworkConnected(context)) {
            Toast.makeText(context, R.string.loading_cache_info, Toast.LENGTH_SHORT).show();
        }

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = firebaseFirestore.collection(FIRESTORE_USER_COLLECTION);
        usersCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                progressBar.setVisibility(View.GONE);
                if (!queryDocumentSnapshots.isEmpty()) {

                    List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
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
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(getString(R.string.intent_type_email));
        intent.putExtra(Intent.EXTRA_EMAIL, email);
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));

        startActivity(Intent.createChooser(intent, context.getString(R.string.email)));
    }

    @Override
    public void onPhoneClicked(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }
}
