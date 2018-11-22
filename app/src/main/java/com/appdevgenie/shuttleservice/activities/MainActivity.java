package com.appdevgenie.shuttleservice.activities;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appdevgenie.shuttleservice.R;
import com.appdevgenie.shuttleservice.fragments.MainIconListFragment;
import com.appdevgenie.shuttleservice.model.User;
import com.appdevgenie.shuttleservice.utils.CheckNetworkConnection;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.appdevgenie.shuttleservice.utils.Constants.BUNDLE_IS_DUAL_PANE;
import static com.appdevgenie.shuttleservice.utils.Constants.BUNDLE_IS_SIGNED_IN;
import static com.appdevgenie.shuttleservice.utils.Constants.USER_ADMIN;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference usersDocumentReference;
    private User user;
    private TextView tvSignedInInfo;
    private ImageView ivNetworkConnectivity;
    private boolean isSignedIn;
    private boolean dualPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_container);

        /*if(savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            MainIconListFragment fragment = new MainIconListFragment();
            fragmentManager.beginTransaction().replace(R.id.mainActivityContainer, fragment).commit();
        }*/

        FrameLayout frameLayout = findViewById(R.id.fragmentInfoContainer);
        if (frameLayout != null && frameLayout.getVisibility() == View.VISIBLE) {
            dualPane = true;
        }

        //Toast.makeText(this, "dual pane " + dualPane, Toast.LENGTH_LONG).show();

        tvSignedInInfo = findViewById(R.id.tvSignedInInfo);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        /*usersDocumentReference =
                firebaseFirestore.collection("users").document(firebaseAuth.getUid());*/

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    /*// user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();*/
                    isSignedIn = false;
                }else{
                    isSignedIn = true;
                    checkUserInfo();
                }

                Bundle bundle = new Bundle();
                bundle.putBoolean(BUNDLE_IS_SIGNED_IN, isSignedIn);
                bundle.putBoolean(BUNDLE_IS_DUAL_PANE, dualPane);
                FragmentManager fragmentManager = getSupportFragmentManager();
                MainIconListFragment mainIconListFragment = new MainIconListFragment();
                mainIconListFragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.mainActivityContainer, mainIconListFragment).commit();

                if(!isSignedIn) {
                    tvSignedInInfo.setText("not signed in");
                }else if(TextUtils.equals(firebaseAuth.getCurrentUser().getEmail(), USER_ADMIN)){
                    tvSignedInInfo.setText("admin");
                }else{
                    tvSignedInInfo.setText("user");
                }
            }
        };

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ivNetworkConnectivity = findViewById(R.id.ivNetworkConnectivity);
        if(CheckNetworkConnection.isNetworkConnected(this)){
            ivNetworkConnectivity.setImageResource(R.drawable.ic_cloud_on);
        }else{
            ivNetworkConnectivity.setImageResource(R.drawable.ic_cloud_off);
        }

    }

    private void checkUserInfo() {
        usersDocumentReference =
                firebaseFirestore.collection("users").document(firebaseAuth.getUid());
        usersDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(!documentSnapshot.exists()){
                    setUserData();
                    //usersDocumentReference.set(user);
                    Toast.makeText(MainActivity.this, "updating user info", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setUserData() {
        user = new User("", firebaseAuth.getCurrentUser().getEmail(), "");
        usersDocumentReference.set(user);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemID = item.getItemId();

        switch (itemID) {

            case R.id.menu_sign_out:

                firebaseAuth.signOut();
                finish();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }



     /*private boolean checkIfTablet() {

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);

        double width = displayMetrics.widthPixels / displayMetrics.densityDpi;
        double heigth = displayMetrics.heightPixels / displayMetrics.densityDpi;

        double diagonal = Math.sqrt(Math.pow(width, 2) + Math.pow(heigth, 2));
        return diagonal >= 7.0;
    }*/
}
