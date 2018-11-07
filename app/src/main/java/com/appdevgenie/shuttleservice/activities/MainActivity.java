package com.appdevgenie.shuttleservice.activities;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.appdevgenie.shuttleservice.R;
import com.appdevgenie.shuttleservice.fragments.MainIconListFragment;
import com.appdevgenie.shuttleservice.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.appdevgenie.shuttleservice.utils.Constants.EXTRA_PARSE_IS_SIGNED_IN;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference usersDocumentReference;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_container);

        /*if(savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            MainIconListFragment fragment = new MainIconListFragment();
            fragmentManager.beginTransaction().replace(R.id.mainActivityContainer, fragment).commit();
        }*/


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        /*usersDocumentReference =
                firebaseFirestore.collection("users").document(firebaseAuth.getUid());*/

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                boolean isSignedIn;
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
                bundle.putBoolean(EXTRA_PARSE_IS_SIGNED_IN, isSignedIn);
                FragmentManager fragmentManager = getSupportFragmentManager();
                MainIconListFragment mainIconListFragment = new MainIconListFragment();
                mainIconListFragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.mainActivityContainer, mainIconListFragment).commit();
            }
        };

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

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
