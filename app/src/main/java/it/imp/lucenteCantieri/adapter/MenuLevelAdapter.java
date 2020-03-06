package it.imp.lucenteCantieri.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.imp.lucenteCantieri.R;
import it.imp.lucenteCantieri.servizi.NodoAlbero;

public class MenuLevelAdapter extends RecyclerView.Adapter<MenuLevelViewHolder> {

    private Context mContext;
    private List<NodoAlbero> mLevelList;


    public MenuLevelAdapter(Context mContext, List<NodoAlbero> mLevelList) {
        this.mContext = mContext;
        this.mLevelList = mLevelList;
    }


    @NonNull
    @Override
    public MenuLevelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menulevel_card, parent, false);
        MenuLevelViewHolder holder = new  MenuLevelViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MenuLevelViewHolder holder, final int position) {
        //set text view
        holder.levelName.setText(mLevelList.get(position).getDescrizione());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mLevelList.size();
    }

}