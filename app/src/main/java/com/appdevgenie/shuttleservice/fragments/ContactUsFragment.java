package com.appdevgenie.shuttleservice.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.appdevgenie.shuttleservice.R;

public class ContactUsFragment extends Fragment implements View.OnClickListener {

    private View view;
    private Context context;
    private ImageButton ibEmail;
    private ImageButton ibShare;
    private ImageButton ibPhone;
    private TextView tvContactInfoNum;
    private TextView tvContactInfoEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        setupVariables();
        return view;
    }

    private void setupVariables() {

        context = getActivity();

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        AppCompatActivity appCompatActivity = (AppCompatActivity)getActivity();
        if (appCompatActivity != null) {
            appCompatActivity.setSupportActionBar(toolbar);
            appCompatActivity.getSupportActionBar().setTitle(R.string.contact_info);
            appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
        }

        tvContactInfoNum = view.findViewById(R.id.tvContactInfoNum);
        tvContactInfoEmail = view.findViewById(R.id.tvContactInfoEmail);

        ibEmail = view.findViewById(R.id.ibUserContactEmail);
        ibShare = view.findViewById(R.id.ibUserContactShare);
        ibPhone = view.findViewById(R.id.ibUserContactPhone);
        ibEmail.setOnClickListener(this);
        ibShare.setOnClickListener(this);
        ibPhone.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        String email = tvContactInfoEmail.getText().toString();
        String phone = tvContactInfoNum.getText().toString();
        CharSequence shareText = TextUtils.concat(getString(R.string.app_name), "\nPhone: ", phone, "\nEmail: ", email);

        switch (v.getId()){

            case R.id.ibUserContactEmail:
                onEmailClicked(email);
                break;

            case R.id.ibUserContactShare:
                shareContactInfo(shareText.toString());
                break;

            case R.id.ibUserContactPhone:
                onPhoneClicked(phone);
                break;
        }
    }

    private void onEmailClicked(String email) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_EMAIL, email);
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        startActivity(Intent.createChooser(intent, "Email"));
    }

    private void onPhoneClicked(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    private void shareContactInfo(String shareText) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.app_name)));
    }
}
