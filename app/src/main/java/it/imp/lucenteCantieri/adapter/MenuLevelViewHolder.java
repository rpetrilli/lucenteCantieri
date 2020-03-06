package it.imp.lucenteCantieri.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.imp.lucenteCantieri.R;

public class MenuLevelViewHolder extends RecyclerView.ViewHolder {

public TextView levelName;

    public MenuLevelViewHolder(@NonNull View v) {
            super(v);

        levelName = (TextView) v.findViewById(R.id.levelName);
    }
}