package it.imp.lucenteCantieri.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.imp.lucenteCantieri.R;

class PhotoViewHolder extends RecyclerView.ViewHolder {

    TextView imageName;
    TextView imagePath;
    ImageView image;

    PhotoViewHolder(@NonNull View v) {
        super(v);

        image = v.findViewById(R.id.image);
        imageName = v.findViewById(R.id.imageTitle);
        imagePath = v.findViewById(R.id.imagePath);

    }
}