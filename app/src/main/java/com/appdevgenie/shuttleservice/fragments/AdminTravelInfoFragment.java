package com.appdevgenie.shuttleservice.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appdevgenie.shuttleservice.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static com.appdevgenie.shuttleservice.utils.Constants.FIRESTORE_TRAVEL_INFO_COLLECTION;

public class AdminTravelInfoFragment extends Fragment {

    private Context context;
    private View view;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_admin_travel_info, container, false);
        setupVariables();
        return view;
    }

    private void setupVariables() {

        context = getActivity();
        Log.d("AdminTravel", "travel started");

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        CollectionReference collectionReference = firebaseFirestore.collection(FIRESTORE_TRAVEL_INFO_COLLECTION);
        collectionReference.whereEqualTo("viewType", 0)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d("AdminTravel", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d("AdminTravel", "Error getting documents: ", task.getException());
                        }
                    }
                });

        /*DocumentReference usersDocumentReference =
                firebaseFirestore.collection("users").document();
        usersDocumentReference
                //.whereEqualTo("date", "10 Nov 2018")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                                Log.d("AdminTravel", task.getResult().getId() + " => " + task.getResult().getData());

                        } else {
                            Log.d("AdminTravel", "error getting documents: ", task.getException());
                        }
                    }
                });*/
    }
}
