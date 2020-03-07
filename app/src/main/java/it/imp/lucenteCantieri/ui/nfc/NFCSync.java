package it.imp.lucenteCantieri.ui.nfc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.imp.lucenteCantieri.R;

public class NFCSync extends AppCompatActivity {

    List<String> filters;
    TextView filtersTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_n_f_c_sync);

        initView();
    }

    private void initView() {

        //init UI
        filtersTextView = findViewById(R.id.places);

        if(getIntent().getStringArrayListExtra("places") != null) {
            filters = getIntent().getStringArrayListExtra("places");

            StringBuilder places = new StringBuilder();

            for (String place: filters){
                places.append(place).append(" > ");
            }

            //clean last arrow
            filtersTextView.setText(places.substring(0,places.length()-3));
        }
    }
}
