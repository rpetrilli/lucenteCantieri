package it.imp.lucenteCantieri.ui.confirm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.imp.lucenteCantieri.R;
import it.imp.lucenteCantieri.adapter.TaskCantiereAdapter;
import it.imp.lucenteCantieri.servizi.AppService;
import it.imp.lucenteCantieri.servizi.AttivitaElenco;
import it.imp.lucenteCantieri.servizi.NodoAlbero;
import it.imp.lucenteCantieri.servizi.UbicazioneCantiere;
import it.imp.lucenteCantieri.utils.Constants;


public class ConfirmationListActivity extends AppCompatActivity {


    //UI
    TextView date;
    TextView places;
    RecyclerView taskRecyclerView;

    //utils
    NfcAdapter mNfcAdapter;
    UbicazioneCantiere mUbicazioneCantiere;
    TaskCantiereAdapter taskCantiereAdapter;
    List<String> mFiltersSelected;
    Calendar calendar ;
    int Year, Month, Day ;
    Date mSelectedDate = new Date();

    NodoAlbero nodoAlbero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

    }


    private void initView() {
        //init UI
        taskRecyclerView = findViewById(R.id.taskRecyclerView);
        date = findViewById(R.id.date);
        places = findViewById(R.id.places);


        //init tools
        calendar = Calendar.getInstance();
        Year = calendar.get(Calendar.YEAR) ;
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);

        //calendar init
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        date.setText(df.format(mSelectedDate));

        //get list of filters
        mFiltersSelected = AppService.getInstance(this).descrizioniFiltro(mUbicazioneCantiere);
        leggiUbicazioni();

        //get nodo albero
        nodoAlbero = new NodoAlbero(mUbicazioneCantiere);

        readTasks(nodoAlbero);

        //init Utils
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, getString(R.string.msg_dispositivo_non_idoneo), Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        readTasks(nodoAlbero);
    }

    private void leggiUbicazioni() {
        if(mFiltersSelected.size() > 0){
            StringBuilder places = new StringBuilder();
            for (String place: mFiltersSelected){
                places.append(place).append(" > ");
            }
            //remove last arrow
            this.places.setText(places.substring(0,places.length()-3));
        }
    }

        /*
        load task from DB and Api
     */

    public void readTasks(NodoAlbero item) {
        AsyncTask<Void, Void, List<AttivitaElenco>> task = new AsyncTask<Void, Void, List<AttivitaElenco>>(){

            @Override
            protected List<AttivitaElenco> doInBackground(Void... mainActivities) {

                //get tasks from App Services
                try {
                    AppService appService = AppService.getInstance(ConfirmationListActivity.this);
                    return appService.leggiTaskCantiere(mSelectedDate, item);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            @Override
            protected void onPostExecute(List<AttivitaElenco> elenco) {

                //Set taskCantiereAdapter for listview
                taskCantiereAdapter = new TaskCantiereAdapter(ConfirmationListActivity.this, elenco, true, mFiltersSelected);
                taskRecyclerView.setAdapter(taskCantiereAdapter);

                taskRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1,GridLayoutManager.VERTICAL, false));

                taskCantiereAdapter.notifyDataSetChanged();
            }
        };

        task.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
            getIntent().setAction(null);
        }else if( getIntent().getStringExtra(Constants.UBICAZIONE_CANTIERE) != null){
            String json = getIntent().getStringExtra(Constants.UBICAZIONE_CANTIERE);
            Gson gson = new Gson();
            mUbicazioneCantiere = gson.fromJson(json, UbicazioneCantiere.class );
        }

        initView();
    }

    void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        String json = new String(msg.getRecords()[0].getPayload());

        Gson gson = new Gson();
        mUbicazioneCantiere = gson.fromJson(json, UbicazioneCantiere.class );

    }

}
