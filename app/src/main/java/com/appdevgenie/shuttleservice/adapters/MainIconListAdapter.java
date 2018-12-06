package com.appdevgenie.shuttleservice.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appdevgenie.shuttleservice.R;
import com.appdevgenie.shuttleservice.model.MainGridIcon;

import java.util.ArrayList;

public class MainIconListAdapter extends RecyclerView.Adapter<MainIconListAdapter.MainViewHolder> {

    private Context context;
    private ArrayList<MainGridIcon> mainGridIcons;
    private ListItemClickListener listItemClickListener;
    private int selectedPos = RecyclerView.NO_POSITION;

    public MainIconListAdapter(Context context, ListItemClickListener listItemClickListener) {
        this.context = context;
        this.listItemClickListener = listItemClickListener;
    }

    @NonNull
    @Override
    public MainIconListAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_icon, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainIconListAdapter.MainViewHolder holder, int position) {
        MainGridIcon mainGridIcon = mainGridIcons.get(holder.getAdapterPosition());
        holder.itemView.setSelected(selectedPos == position);

        holder.tvIcon.setText(mainGridIcon.getIconString());
        holder.ivIcon.setImageResource(mainGridIcon.getIconImage());

    }

    public void setItems(@NonNull ArrayList<MainGridIcon> items) {
        this.mainGridIcons = items;
    }

    @Override
    public int getItemCount() {
        if(mainGridIcons == null) {
            return 0;
        }
        return mainGridIcons.size();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivIcon;
        private TextView tvIcon;

        MainViewHolder(View itemView) {
            super(itemView);

            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvIcon = itemView.findViewById(R.id.tvIcon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //MainGridIcon mainGridIcon = mainGridIcons.get(getAdapterPosition());
            int position = getAdapterPosition();
            listItemClickListener.onItemClicked(position);

            notifyItemChanged(selectedPos);
            selectedPos = getLayoutPosition();
            notifyItemChanged(selectedPos);
        }
    }

    public interface ListItemClickListener{
        void onItemClicked(int position);
    }
}
