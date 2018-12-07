package com.appdevgenie.shuttleservice.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appdevgenie.shuttleservice.R;
import com.appdevgenie.shuttleservice.model.User;
import com.appdevgenie.shuttleservice.utils.CheckNetworkConnection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.appdevgenie.shuttleservice.utils.Constants.FIRESTORE_USER_COLLECTION;
import static com.appdevgenie.shuttleservice.utils.Constants.FIRESTORE_USER_IMAGE_CHILD;
import static com.appdevgenie.shuttleservice.utils.Constants.SELECT_IMAGE_REQUEST;

public class AccountUserInfoFragment extends Fragment implements View.OnClickListener {

    private Context context;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.pbImageLoading)
    ProgressBar pbImageLoading;
    @BindView(R.id.ivUserAccount)
    ImageView ivThumb;
    @BindView(R.id.etUserName)
    EditText etUserName;
    @BindView(R.id.tvUserAccountEmail)
    TextView tvUserEmail;
    @BindView(R.id.etUserContactNumber)
    EditText etContactNumber;
    @BindView(R.id.bUserAccountUpdate)
    Button bUpdate;
    @BindView(R.id.bUserAccountDelete)
    Button bDelete;
    private StorageReference imageStorageReference;
    private FirebaseUser firebaseUser;
    private DocumentReference usersDocumentReference;
    private Uri imageUri;

    public AccountUserInfoFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_user_info, container, false);
        ButterKnife.bind(this, view);
        setupVariables();
        return view;
    }

    private void setupVariables() {

        context = getActivity();
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        if (appCompatActivity != null) {
            appCompatActivity.getSupportActionBar().setTitle(R.string.user_info);
        }

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        imageStorageReference = storageReference.child(firebaseAuth.getUid()).child(FIRESTORE_USER_IMAGE_CHILD);
        usersDocumentReference =
                firebaseFirestore.collection(FIRESTORE_USER_COLLECTION).document(firebaseAuth.getUid());

        ivThumb.setOnClickListener(this);
        bUpdate.setOnClickListener(this);
        bDelete.setOnClickListener(this);

        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            getUserInfo();
        }
    }

    private void getUserInfo() {

        if (!CheckNetworkConnection.isNetworkConnected(context)) {
            Toast.makeText(context, R.string.loading_cache_info, Toast.LENGTH_SHORT).show();
        }

        progressBar.setVisibility(View.VISIBLE);
        usersDocumentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                progressBar.setVisibility(View.GONE);

                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null) {
                        User user = task.getResult().toObject(User.class);
                        if (user != null) {
                            tvUserEmail.setText(user.getEmail());
                            etUserName.setText(user.getName());
                            etContactNumber.setText(user.getContactNumber());
                        }
                    }
                }
            }
        });

        if (CheckNetworkConnection.isNetworkConnected(context)) {
            pbImageLoading.setVisibility(View.VISIBLE);
            imageStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    pbImageLoading.setVisibility(View.GONE);
                    loadImageView(uri);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pbImageLoading.setVisibility(View.GONE);
                    loadComplete(e.getMessage());
                }
            });
        }
    }

    private void selectUserImage() {
        Intent intent = new Intent();
        intent.setType(getString(R.string.intent_type_image));
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, SELECT_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            loadImageView(imageUri);
        }
    }

    private void loadImageView(Uri imageUri) {
        Picasso.with(context)
                .load(imageUri)
                .placeholder(R.drawable.ic_account)
                .into(ivThumb);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.bUserAccountDelete:
                showDeleteConfirmationDialog();
                break;

            case R.id.bUserAccountUpdate:
                updateUserInfo();
                break;

            case R.id.ivUserAccount:
                selectUserImage();
                break;
        }

    }

    private void updateUserInfo() {
        progressBar.setVisibility(View.VISIBLE);
        pbImageLoading.setVisibility(View.VISIBLE);

        User user = new User(
                etUserName.getText().toString(),
                tvUserEmail.getText().toString(),
                etContactNumber.getText().toString());

        usersDocumentReference.set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadComplete(getString(R.string.user_update_success));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadComplete(e.getMessage());
                    }
                });

        if(imageUri != null) {
            imageStorageReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pbImageLoading.setVisibility(View.GONE);
                            loadComplete(getString(R.string.image_uploaded));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            pbImageLoading.setVisibility(View.GONE);
                            Toast.makeText(context, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }

    }

    private void loadComplete(String toastString) {
        Toast.makeText(context, toastString, Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.are_you_sure);
        builder.setPositiveButton(R.string.delete_account, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button
                deleteUser();
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteUser() {

        if (firebaseUser != null) {
            progressBar.setVisibility(View.VISIBLE);
            firebaseUser.delete().addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressBar.setVisibility(View.INVISIBLE);
                    if (task.isSuccessful()) {
                        getActivity().finish();
                    }
                }
            }).addOnFailureListener(getActivity(), new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
