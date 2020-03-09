package it.imp.lucenteCantieri.ui.nfc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.imp.lucenteCantieri.MainActivity;
import it.imp.lucenteCantieri.R;
import it.imp.lucenteCantieri.adapter.MenuLevelAdapter;
import it.imp.lucenteCantieri.adapter.UbicazioneAdapter;
import it.imp.lucenteCantieri.ui.ConfirmActivity;

public class NFCSync extends AppCompatActivity {

    List<String> filters;
    RecyclerView ubicazioniRecyclerView;
    UbicazioneAdapter ubicazioniAdapter;
    Button test;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_n_f_c_sync);

        initView();
    }

    private void initView() {

        //init UI
        ubicazioniRecyclerView = findViewById(R.id.ubicazioniRecyclerView);

        test = findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent test = new Intent(NFCSync.this, ConfirmActivity.class);
                test.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(test);
            }
        });


        if(getIntent().getStringArrayListExtra("places") != null) {
            filters = getIntent().getStringArrayListExtra("places");

            //set adapter for places list view
            ubicazioniAdapter = new UbicazioneAdapter(NFCSync.this, filters);
            //Set levelNameAdapter for listview
            if (filters.size() > 0) {
                ubicazioniRecyclerView.setAdapter(ubicazioniAdapter);
            }

            ubicazioniRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1,GridLayoutManager.VERTICAL, false));

        }
    }
}
