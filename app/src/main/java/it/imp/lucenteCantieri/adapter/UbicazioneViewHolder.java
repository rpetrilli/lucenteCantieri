package it.imp.lucenteCantieri.adapter;

import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.imp.lucenteCantieri.R;

class UbicazioneViewHolder extends RecyclerView.ViewHolder {

TextView ubicazioneTitle;
ImageView arrow;


    UbicazioneViewHolder(@NonNull View v) {
        super(v);
        ubicazioneTitle = (TextView) v.findViewById(R.id.ubicazioneTitle);
        arrow = (ImageView) v.findViewById(R.id.arrow);
    }
}