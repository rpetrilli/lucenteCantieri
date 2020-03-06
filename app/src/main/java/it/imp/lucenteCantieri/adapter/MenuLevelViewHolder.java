package it.imp.lucenteCantieri.adapter;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.imp.lucenteCantieri.R;

class MenuLevelViewHolder extends RecyclerView.ViewHolder {

TextView levelName;
LinearLayout level;

    MenuLevelViewHolder(@NonNull View v) {
        super(v);

        level = (LinearLayout) v.findViewById(R.id.level);
        levelName = (TextView) v.findViewById(R.id.levelName);
    }
}