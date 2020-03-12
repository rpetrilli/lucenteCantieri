package it.imp.lucenteCantieri.ui.reporting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import it.imp.lucenteCantieri.R;
import it.imp.lucenteCantieri.model.ClienteGerarchiaEntity;
import it.imp.lucenteCantieri.model.SegnalazioneEntity;
import it.imp.lucenteCantieri.servizi.AppService;
import it.imp.lucenteCantieri.servizi.NodoAlbero;
import it.imp.lucenteCantieri.servizi.Settings;
import it.imp.lucenteCantieri.utils.Constants;
import it.imp.lucenteCantieri.workers.SegnalazioneWorker;

public class SegnalazioneActivity extends AppCompatActivity {

    //UI
    @BindView(R.id.btn_invia)
    Button mBtnInvia;
    @BindView(R.id.txtSegnalazione)
    MultiAutoCompleteTextView mTxtSegnalazione;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.places)
    TextView places;
    @BindView(R.id.spinner)
    MaterialSpinner spinner;

    //utils
    Date mSelectedDate = new Date();
    List<ClienteGerarchiaEntity> mClientiGerarchia = new ArrayList<>();
    List<String> clienti = new ArrayList<>();
    List<String> filters;
    NodoAlbero nodoAlbero;
    Long idClienteSelected = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segnalazione);

        ButterKnife.bind(this);

        initView();

    }

    private void initView() {

        //toolbar with back arrow
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //focus
        mTxtSegnalazione.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        mTxtSegnalazione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
            }
        });


        //set date
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        date.setText(df.format(mSelectedDate));

        //get ubicazioni
        if (getIntent().getStringArrayListExtra(Constants.PLACES) != null){
            filters = getIntent().getStringArrayListExtra(Constants.PLACES);

            initPlaces();
        }

        if(getIntent().getStringExtra(Constants.NODO) != null){
            String nodo = getIntent().getStringExtra(Constants.NODO);
            Gson gson = new Gson();
            nodoAlbero = gson.fromJson(nodo, NodoAlbero.class);

            leggiUbicazioniCollegate();
        }
    }

    private void initPlaces() {
        if(filters.size() > 0){
            StringBuilder places = new StringBuilder();

            for (String place: filters){
                places.append(place).append(" > ");
            }

            //clean last arrow
            this.places.setText(places.substring(0,places.length()-3));
        }
    }


    private void leggiUbicazioniCollegate() {
        this.mClientiGerarchia = AppService.getInstance(this).leggiGerachiaPerNodoAlbero(nodoAlbero);

        //add initial hint
        this.clienti.add("Ubicazione della segnalazione");


        for (ClienteGerarchiaEntity cliente: this.mClientiGerarchia) {
            this.clienti.add(cliente.descLivello);
        }

        //clienti spinner
        spinner.setItems(this.clienti);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {

                if(!item.equals("Ubicazione della segnalazione")) {
                    //search cliente
                    for (ClienteGerarchiaEntity cliente : mClientiGerarchia) {
                        if (cliente.descLivello.equals(item)) {
                            idClienteSelected = cliente.idClienteGerachia;
                        }
                    }
                }else{
                    idClienteSelected = null;
                }
            }
        });

    }

    @OnClick(R.id.btn_invia)
    public void inviaSegnalazioneAlServer() {

        SegnalazioneEntity segnalazione = new SegnalazioneEntity();
        segnalazione.idCliente = Settings.getInstance().idCliente;
        segnalazione.idClienteSquadra = Settings.getInstance().idClienteSquadra;
        segnalazione.descrizione = mTxtSegnalazione.getText().toString();
        segnalazione.dataCreazione = new Date();
        segnalazione.idClienteGerachia = idClienteSelected;

        AsyncTask<SegnalazioneEntity, Void, SegnalazioneEntity> task = new AsyncTask<SegnalazioneEntity, Void, SegnalazioneEntity>(){

            @Override
            protected SegnalazioneEntity doInBackground(SegnalazioneEntity... segnalazione) {
                //get places tree from App Services
                try {
                    return AppService.getInstance(SegnalazioneActivity.this).salvaSegnalazione(segnalazione[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            @Override
            protected void onPostExecute(SegnalazioneEntity segnalazione) {

                Constraints constraints = new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build();

                Data uploadData = new Data.Builder()
                        .putLong(Constants.ID_SEGNALAZIONE, segnalazione.idSegnalazione)
                        .build();

                OneTimeWorkRequest uploadWork =
                        new OneTimeWorkRequest.Builder(SegnalazioneWorker.class)
                                .setConstraints(constraints)
                                .setInputData(uploadData)
                                .build();


                WorkManager.getInstance(SegnalazioneActivity.this).enqueue(uploadWork);

                finish();


            }
        };

        task.execute(segnalazione);

    }

    /*
    hyde keyboard function
 */
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @OnTextChanged(R.id.txtSegnalazione)
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(s.toString().trim().length()==0){
            mBtnInvia.setEnabled(false);
            mBtnInvia.setBackgroundResource(R.color.colorDisabled);
        } else {
            mBtnInvia.setEnabled(true);
            mBtnInvia.setBackgroundResource(R.color.colorPrimary);

        }
    }


}
