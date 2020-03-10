package it.imp.lucenteCantieri.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.imp.lucenteCantieri.R;
import it.imp.lucenteCantieri.servizi.NodoAlbero;

public class
UbicazioneAdapter extends RecyclerView.Adapter<UbicazioneViewHolder> {

    private Context mContext;
    private List<String> mUbicazioniList;



    public UbicazioneAdapter(Context mContext, List<String> mUbicazioniList) {
        this.mContext = mContext;
        this.mUbicazioniList = mUbicazioniList;
    }


    @NonNull
    @Override
    public UbicazioneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ubicazione_card, parent, false);
        UbicazioneViewHolder holder = new  UbicazioneViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull UbicazioneViewHolder holder, final int position) {
        //set text view
        String ubicazione = mUbicazioniList.get(position);

        holder.ubicazioneTitle.setText(ubicazione);

        if(mUbicazioniList.size() != 1 && position != mUbicazioniList.size()-1){
            holder.ubicatoCard.setPadding(8 * position,0,0,0);
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mUbicazioniList.size();
    }

}