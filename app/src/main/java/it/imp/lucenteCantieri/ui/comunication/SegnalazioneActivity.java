package it.imp.lucenteCantieri.ui.comunication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.imp.lucenteCantieri.MainActivity;
import it.imp.lucenteCantieri.R;
import it.imp.lucenteCantieri.adapter.MenuLevelAdapter;
import it.imp.lucenteCantieri.model.SegnalazioneEntity;
import it.imp.lucenteCantieri.servizi.AppService;
import it.imp.lucenteCantieri.servizi.NodoAlbero;
import it.imp.lucenteCantieri.servizi.Settings;
import it.imp.lucenteCantieri.utils.Constants;
import it.imp.lucenteCantieri.workers.SegnalazioneWorker;
import it.imp.lucenteCantieri.workers.UploadWorker;

public class SegnalazioneActivity extends AppCompatActivity {

    @BindView(R.id.btn_invia)
    Button mBtnInvia;
    @BindView(R.id.txtSegnalazione)
    MultiAutoCompleteTextView mTxtSegnalazione;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comunication);

        ButterKnife.bind(this);

        leggiUbicazioniCollegate();

    }

    private void leggiUbicazioniCollegate() {
        //TODO: riempire gerarchia
        //AppService.getInstance(this).leggiGerachiaPerNodoAlbero(null);
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
