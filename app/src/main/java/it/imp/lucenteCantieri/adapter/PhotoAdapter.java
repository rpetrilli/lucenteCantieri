package it.imp.lucenteCantieri.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import it.imp.lucenteCantieri.R;
import it.imp.lucenteCantieri.model.TaskCantiereImg;
import it.imp.lucenteCantieri.servizi.AppService;
import it.imp.lucenteCantieri.ui.confirmationDetails.ConfirmationDetailsActivity;

public class
PhotoAdapter extends RecyclerView.Adapter<PhotoViewHolder> {

    private Context mContext;
    private List<TaskCantiereImg> mPhotoList;
    private Executor executor = Executors.newSingleThreadExecutor();


    public PhotoAdapter(Context mContext, List<TaskCantiereImg> mPhotoList) {
        this.mContext = mContext;
        this.mPhotoList = mPhotoList;
    }


    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_card, parent, false);
        PhotoViewHolder holder = new  PhotoViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, final int position) {
        //set text view
        TaskCantiereImg photo = mPhotoList.get(position);

        holder.imageName.setText(photo.nomeImmagine.substring(photo.nomeImmagine.length()-10, photo.nomeImmagine.length()));
        Picasso.get().load(new File(photo.nomeImmagine)).fit().centerCrop().into(holder.image);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setTitle("Attenzione")
                        .setMessage("Sicuro di voler cancellare la foto?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                executor.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        //delete photo from database
                                        AppService.getInstance(mContext).deletePhoto(photo);

                                        //delete from recycle view
                                        mPhotoList.remove(position);

                                        notifyItemChanged(position);
                                    }
                                });
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mPhotoList.size();
    }
}