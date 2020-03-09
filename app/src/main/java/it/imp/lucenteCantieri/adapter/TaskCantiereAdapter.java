package it.imp.lucenteCantieri.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.List;

import it.imp.lucenteCantieri.R;
import it.imp.lucenteCantieri.servizi.AttivitaElenco;
import it.imp.lucenteCantieri.ui.confirmationDetails.ConfirmationDetailsActivity;
import it.imp.lucenteCantieri.utils.Constants;

public class
TaskCantiereAdapter extends RecyclerView.Adapter<TaskCantiereViewHolder> {

    private static final int INDENT = 50;
    private Context mContext;
    private List<AttivitaElenco> mAttivitaList;
    Boolean confirmation;


    public TaskCantiereAdapter(Context mContext, List<AttivitaElenco> mLevelList, Boolean confirmation) {
        this.mContext = mContext;
        this.mAttivitaList = mLevelList;
        this.confirmation = confirmation;
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

        holder.taskCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(confirmation){
                    //convert object to json
                    Gson gson = new Gson();
                    String json = gson.toJson(mAttivitaList.get(position));

                    Intent confirmationActivity = new Intent(mContext, ConfirmationDetailsActivity.class);
                    confirmationActivity.putExtra(Constants.ID_TASK_CANTIERE, json);
                    confirmationActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(confirmationActivity);
                }
            }
        });


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