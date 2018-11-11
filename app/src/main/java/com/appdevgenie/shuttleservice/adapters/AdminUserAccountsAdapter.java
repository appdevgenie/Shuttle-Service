package com.appdevgenie.shuttleservice.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.appdevgenie.shuttleservice.R;
import com.appdevgenie.shuttleservice.model.User;

import java.util.ArrayList;

public class AdminUserAccountsAdapter extends RecyclerView.Adapter<AdminUserAccountsAdapter.UserViewHolder> {

    private Context context;
    private ArrayList<User> userArrayList;
    private EmailClickListener emailClickListener;
    private PhoneClickListener phoneClickListener;

    public AdminUserAccountsAdapter(Context context, ArrayList<User> userArrayList, EmailClickListener emailClickListener, PhoneClickListener phoneClickListener) {
        this.context = context;
        this.userArrayList = userArrayList;
        this.emailClickListener = emailClickListener;
        this.phoneClickListener = phoneClickListener;
    }

    @NonNull
    @Override
    public AdminUserAccountsAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_all_user_info, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminUserAccountsAdapter.UserViewHolder holder, int position) {

        User user = userArrayList.get(holder.getAdapterPosition());

        holder.tvName.setText(user.getName());
        holder.tvEmail.setText(user.getEmail());
        holder.tvNumber.setText(user.getContactNumber());
    }

    @Override
    public int getItemCount() {
        if(userArrayList == null) {
            return 0;
        }
        return userArrayList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvName;
        private TextView tvEmail;
        private TextView tvNumber;
        private ImageButton bEmail;
        private ImageButton bPhone;

        public UserViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvPassengerInfoName);
            tvEmail = itemView.findViewById(R.id.tvPassengerInfoEmail);
            tvNumber = itemView.findViewById(R.id.tvPassengerInfoContactNum);
            bEmail = itemView.findViewById(R.id.ibUserContactEmail);
            bPhone = itemView.findViewById(R.id.ibUserContactPhone);
            bEmail.setOnClickListener(this);
            bPhone.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();
            String phone = userArrayList.get(position).getContactNumber();
            String email = userArrayList.get(position).getEmail();

            switch (v.getId()){
                case R.id.ibUserContactEmail:
                    emailClickListener.onEmailClicked(email);
                    break;

                case R.id.ibUserContactPhone:
                    phoneClickListener.onPhoneClicked(phone);
                    break;
            }
        }
    }

    public interface EmailClickListener{
        void onEmailClicked(String email);
    }

    public interface PhoneClickListener{
        void onPhoneClicked(String phone);
    }
}
