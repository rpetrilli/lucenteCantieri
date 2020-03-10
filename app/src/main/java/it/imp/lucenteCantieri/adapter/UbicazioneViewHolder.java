package it.imp.lucenteCantieri.adapter;

import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import it.imp.lucenteCantieri.R;

class UbicazioneViewHolder extends RecyclerView.ViewHolder {

    TextView ubicazioneTitle;
    CardView ubicatoCard;


    UbicazioneViewHolder(@NonNull View v) {
        super(v);
        ubicatoCard = (CardView) v.findViewById(R.id.ubicatoCard);
        ubicazioneTitle = (TextView) v.findViewById(R.id.ubicazioneTitle);
    }
}