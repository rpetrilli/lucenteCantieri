package it.imp.lucenteCantieri.adapter;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.imp.lucenteCantieri.R;

class PhotoViewHolder extends RecyclerView.ViewHolder {

    TextView imageName;
    ImageView image;
    FrameLayout delete;

    PhotoViewHolder(@NonNull View v) {
        super(v);

        image = v.findViewById(R.id.img);
        imageName = v.findViewById(R.id.imageTitle);
        delete = v.findViewById(R.id.btn_delete);

    }
}