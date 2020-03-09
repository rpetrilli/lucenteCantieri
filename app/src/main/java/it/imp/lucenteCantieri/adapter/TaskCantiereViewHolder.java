package it.imp.lucenteCantieri.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import it.imp.lucenteCantieri.R;

class TaskCantiereViewHolder extends RecyclerView.ViewHolder {

TextView taskTitle;
TextView taskDescription;
CardView taskCard;

    TaskCantiereViewHolder(@NonNull View v) {
        super(v);

        taskTitle = (TextView) v.findViewById(R.id.taskTitle);
        taskDescription = (TextView) v.findViewById(R.id.taskDescription);
        taskCard = (CardView) v.findViewById(R.id.taskCard);
    }
}