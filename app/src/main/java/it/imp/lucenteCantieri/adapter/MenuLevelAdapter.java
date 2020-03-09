package it.imp.lucenteCantieri.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.imp.lucenteCantieri.MainActivity;
import it.imp.lucenteCantieri.R;
import it.imp.lucenteCantieri.servizi.AppService;
import it.imp.lucenteCantieri.servizi.NodoAlbero;

public class
MenuLevelAdapter extends RecyclerView.Adapter<MenuLevelViewHolder> {

    private static final int INDENT = 50;
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
        NodoAlbero item = mLevelList.get(position);

        holder.levelName.setText(item.getDescrizione());
        holder.levelName.setPadding(INDENT * (item.livello- 1), 16, 0, 16);
        holder.place.setPadding(INDENT * (item.livello- 1), 8, 0, 8);
        holder.divider.setPadding(INDENT * (item.livello- 1), 16, 0, 16);
        holder.levelName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mContext instanceof  MainActivity){
                    ((MainActivity) mContext).updateNodoAlbero(item);
                    ((MainActivity) mContext).getFilters(item);
                    ((MainActivity) mContext).readTasks(item);
                }

            }
        });

        holder.arrow.setImageDrawable(mContext.getResources().getDrawable(item.figliVisibili?R.drawable.ic_arrow_down:R.drawable.ic_arrow_up));
        holder.place.setImageDrawable(mContext.getResources().getDrawable(item.livello == 1?R.drawable.ic_place_black_24dp:R.drawable.ic_brightness_1_black_24dp));
        holder.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppService.getInstance(mContext).toggleNodo(item);
                notifyDataSetChanged();
            }
        });

        //child visibility
        if(!item.show){
            holder.level.setVisibility(View.INVISIBLE);
            holder.level.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
            holder.divider.setVisibility(View.INVISIBLE);
            holder.divider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
        }else{
            holder.level.setVisibility(View.VISIBLE);
            holder.level.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            holder.divider.setVisibility(View.VISIBLE);
            holder.divider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }


        holder.arrow.setVisibility(item.hasChildren?View.VISIBLE:View.INVISIBLE);

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