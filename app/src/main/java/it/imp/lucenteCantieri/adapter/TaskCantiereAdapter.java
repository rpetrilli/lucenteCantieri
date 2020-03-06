package it.imp.lucenteCantieri.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.imp.lucenteCantieri.R;
import it.imp.lucenteCantieri.servizi.AttivitaElenco;

public class
TaskCantiereAdapter extends RecyclerView.Adapter<TaskCantiereViewHolder> {

    private static final int INDENT = 50;
    private Context mContext;
    private List<AttivitaElenco> mAttivitaList;



    public TaskCantiereAdapter(Context mContext, List<AttivitaElenco> mLevelList) {
        this.mContext = mContext;
        this.mAttivitaList = mLevelList;
    }


    @NonNull
    @Override
    public TaskCantiereViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.taskcantiere_card, parent, false);
        TaskCantiereViewHolder holder = new  TaskCantiereViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TaskCantiereViewHolder holder, final int position) {
        //set text view
        AttivitaElenco item = mAttivitaList.get(position);

        holder.taskTitle.setText(item.descLivello);
        holder.taskDescription.setText(item.descrizione);


    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mAttivitaList.size();
    }

}