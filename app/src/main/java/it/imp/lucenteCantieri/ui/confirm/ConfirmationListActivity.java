package it.imp.lucenteCantieri.ui.confirm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
    TextView mTxtUbicazioni;
    RecyclerView taskRecyclerView;

    //utils
    NfcAdapter mNfcAdapter;
    public static UbicazioneCantiere mUbicazioneCantiere;
    TaskCantiereAdapter taskCantiereAdapter;
    List<String> mFiltersSelected;
    Calendar calendar ;
    int Year, Month, Day ;
    Date mSelectedDate = new Date();

    NodoAlbero nodoAlbero;

    public static final String MIME_TEXT_PLAIN = "application/vnd.it.imp.lalucente";
    public static final String TAG = "NFCLucenteCantieri";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        initView();

        //init Utils
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, getString(R.string.msg_dispositivo_non_idoneo), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(this, "Abilita gli NFC", Toast.LENGTH_SHORT).show();
        }


        if( getIntent().getStringExtra(Constants.UBICAZIONE_CANTIERE) != null){
            String json = getIntent().getStringExtra(Constants.UBICAZIONE_CANTIERE);
            Gson gson = new Gson();
            mUbicazioneCantiere = gson.fromJson(json, UbicazioneCantiere.class );
        }


        handleIntent(getIntent());

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


        //init UI
        taskRecyclerView = findViewById(R.id.taskRecyclerView);
        date = findViewById(R.id.date);
        mTxtUbicazioni = findViewById(R.id.places);


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

    }

    private void leggiUbicazioni() {
        if(mFiltersSelected.size() > 0){
            StringBuilder places = new StringBuilder();
            for (String place: mFiltersSelected){
                places.append(place).append(" > ");
            }
            //remove last arrow
            this.mTxtUbicazioni.setText(places.substring(0,places.length()-3));
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

        setupForegroundDispatch(ConfirmationListActivity.this, mNfcAdapter);

    }

    @Override
    protected void onPause() {
        stopForegroundDispatch(ConfirmationListActivity.this, mNfcAdapter);

        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        handleIntent(intent);
    }

    /*void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        String json = new String(msg.getRecords()[0].getPayload());

        Gson gson = new Gson();
        mUbicazioneCantiere = gson.fromJson(json, UbicazioneCantiere.class );

    }*/

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);

            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    new NdefReaderTask().execute(tag);
                    break;
                }
            }
        }
    }

    /**
     * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void setupForegroundDispatch(final ConfirmationListActivity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    /**
     * @param activity The corresponding {@link Activity} requesting to stop the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    private static class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag.
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Unsupported Encoding", e);
                    }
                }
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
            /*
             * See NFC forum specification for "Text Record Type Definition" at 3.2.1
             *
             * http://www.nfc-forum.org/specs/
             *
             * bit_7 defines encoding
             * bit_6 reserved for future use, must be 0
             * bit_5..0 length of IANA language code
             */

            byte[] payload = record.getPayload();

            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"

            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Gson gson = new Gson();
                mUbicazioneCantiere = gson.fromJson(result, UbicazioneCantiere.class);

            }
        }
    }

}
