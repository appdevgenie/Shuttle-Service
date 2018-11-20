package com.appdevgenie.shuttleservice.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;

public class AccountUserInfoFragment extends Fragment implements View.OnClickListener {

    public static final int SELECT_IMAGE_REQUEST = 101;

    private Context context;
    private ProgressBar progressBar;
    private View view;
    private ImageView ivThumb;
    private EditText etUserName;
    private TextView tvUserEmail;
    private EditText etContactNumber;
    private Button bUpdate;
    private Button bSelectImage;
    /*private ImageView ivUpdate;
    private ImageView ivDelete;*/
    //private TextView tvDelete;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private StorageReference imageStorageReference;
    private FirebaseUser firebaseUser;
    private DocumentReference usersDocumentReference;
    private User user;
    private Uri imageUri;
    private ProgressBar pbImageLoading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_account_user_info, container, false);

        setupVariables();

        return view;

    }

    private void setupVariables() {

        context = getActivity();

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        if (appCompatActivity != null) {
            appCompatActivity.setSupportActionBar(toolbar);
            appCompatActivity.getSupportActionBar().setTitle(R.string.user_info);
            appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
        }

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        imageStorageReference = storageReference.child(firebaseAuth.getUid()).child("user.jpg");
        usersDocumentReference =
                firebaseFirestore.collection("users").document(firebaseAuth.getUid());

        progressBar = view.findViewById(R.id.progressBarDelete);
        pbImageLoading = view.findViewById(R.id.pbImageLoading);
        ivThumb = view.findViewById(R.id.ivUserAccount);
        etUserName = view.findViewById(R.id.etUserName);
        tvUserEmail = view.findViewById(R.id.tvUserAccountEmail);
        etContactNumber = view.findViewById(R.id.etUserContactNumber);
        /*ivDelete = view.findViewById(R.id.ivUserAccountDelete);
        ivDelete.setOnClickListener(this);*/
        bUpdate = view.findViewById(R.id.bUserAccountUpdate);
        bUpdate.setOnClickListener(this);
        bSelectImage = view.findViewById(R.id.bSelectImage);
        bSelectImage.setOnClickListener(this);

        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            getUserInfo();
            //tvUserName.setText(firebaseUser.getDisplayName());
            //tvUserEmail.setText(firebaseUser.getEmail());

            /*Picasso.with(context)
                    .load(firebaseUser.getPhotoUrl())
                    .placeholder(R.drawable.circle)
                    .error(R.drawable.circle)
                    .into(ivThumb);*/

            /*Picasso.with(context)
                    .load(firebaseUser.getPhotoUrl())
                    .placeholder(R.drawable.circle)
                    .into(ivThumb, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap imageBitmap = ((BitmapDrawable) ivThumb.getDrawable()).getBitmap();
                            RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                            imageDrawable.setCircular(true);
                            imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                            ivThumb.setImageDrawable(imageDrawable);
                        }

                        @Override
                        public void onError() {
                            ivThumb.setImageResource(R.drawable.circle);
                        }
                    });*/
        }
    }

    private void getUserInfo() {
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

    private void selectUserImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
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
                .into(ivThumb, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap imageBitmap = ((BitmapDrawable) ivThumb.getDrawable()).getBitmap();
                        RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                        imageDrawable.setCircular(true);
                        imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                        ivThumb.setImageDrawable(imageDrawable);
                    }

                    @Override
                    public void onError() {
                        ivThumb.setImageResource(R.drawable.circle);
                    }
                });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

           /* case R.id.ivUserAccountDelete:
                showDeleteConfirmationDialog();
                break;*/

            case R.id.bUserAccountUpdate:
                updateUserInfo();
                break;

            case R.id.bSelectImage:
                selectUserImage();
                break;
        }

    }

    private void updateUserInfo() {
        progressBar.setVisibility(View.VISIBLE);
        pbImageLoading.setVisibility(View.VISIBLE);

        user = new User(
                etUserName.getText().toString(),
                tvUserEmail.getText().toString(),
                etContactNumber.getText().toString());

        usersDocumentReference.set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadComplete("firebaseUser update success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadComplete(e.getMessage());
                    }
                });

        imageStorageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pbImageLoading.setVisibility(View.GONE);
                        loadComplete("Image uploaded");
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

    private void loadComplete(String toastString) {
        Toast.makeText(context, toastString, Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("are you sure?");
        builder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
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
                    } else {
                        Toast.makeText(context, "error deleting firebaseUser", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


}
