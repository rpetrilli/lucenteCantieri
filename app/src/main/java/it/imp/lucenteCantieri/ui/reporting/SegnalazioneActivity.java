package it.imp.lucenteCantieri.ui.reporting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.os.AsyncTask;
import android.os.Bundle;
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
import it.imp.lucenteCantieri.R;
import it.imp.lucenteCantieri.model.ClienteGerarchiaEntity;
import it.imp.lucenteCantieri.model.SegnalazioneEntity;
import it.imp.lucenteCantieri.servizi.AppService;
import it.imp.lucenteCantieri.servizi.AttivitaElenco;
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
        setContentView(R.layout.activity_report);

        ButterKnife.bind(this);

        initView();

    }

    private void initView() {
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

        for (ClienteGerarchiaEntity cliente: this.mClientiGerarchia) {
            this.clienti.add(cliente.descLivello);
        }

        //clienti spinner
        spinner.setItems(this.clienti);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                //search cliente
                for (ClienteGerarchiaEntity cliente: mClientiGerarchia) {
                    if(cliente.descLivello.equals(item)){
                        idClienteSelected = cliente.idClienteGerachia;
                        Snackbar.make(view, "ID CLIENTE " + idClienteSelected, Snackbar.LENGTH_LONG).show();
                    }
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
}
