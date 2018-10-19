package com.example.vclab.moodlightshare;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

class RecyclerViewHolder extends RecyclerView.ViewHolder {
    public TextView name;
    public TextView Description;
    public RecyclerViewHolder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.NameText);
        Description = (TextView) itemView.findViewById(R.id.DescriptionText);
    }
}

