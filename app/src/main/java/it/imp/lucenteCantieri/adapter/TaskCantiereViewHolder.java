package it.imp.lucenteCantieri.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.imp.lucenteCantieri.R;

class TaskCantiereViewHolder extends RecyclerView.ViewHolder {

TextView taskTitle;
TextView taskDescription;

    TaskCantiereViewHolder(@NonNull View v) {
        super(v);

        taskTitle = (TextView) v.findViewById(R.id.taskTitle);
        taskDescription = (TextView) v.findViewById(R.id.taskDescription);
    }
}