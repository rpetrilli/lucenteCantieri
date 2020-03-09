package it.imp.lucenteCantieri.ui.nfc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import it.imp.lucenteCantieri.R;
import it.imp.lucenteCantieri.adapter.UbicazioneAdapter;
import it.imp.lucenteCantieri.ui.confirm.ConfirmationListActivity;
import it.imp.lucenteCantieri.utils.Constants;

import static android.nfc.NdefRecord.createMime;

public class NFCWriterActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback{

    List<String> filters;
    RecyclerView ubicazioniRecyclerView;
    UbicazioneAdapter ubicazioniAdapter;
    Button test;
    String json;

    NfcAdapter mNfcAdapter;
    Tag myTag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_n_f_c_sync);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, getString(R.string.msg_dispositivo_non_idoneo), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        mNfcAdapter.setNdefPushMessageCallback(NFCWriterActivity.this, NFCWriterActivity.this);


        initView();
    }

    private void initView() {

        //init UI
        ubicazioniRecyclerView = findViewById(R.id.ubicazioniRecyclerView);

        test = findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent test = new Intent(NFCWriterActivity.this, ConfirmationListActivity.class);
                test.putExtra(Constants.UBICAZIONE_CANTIERE, json);
                test.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(test);
            }
        });


        if(getIntent().getStringArrayListExtra(Constants.PLACES) != null) {
            filters = getIntent().getStringArrayListExtra(Constants.PLACES);

            //set adapter for places list view
            ubicazioniAdapter = new UbicazioneAdapter(NFCWriterActivity.this, filters);
            //Set levelNameAdapter for listview
            if (filters.size() > 0) {
                ubicazioniRecyclerView.setAdapter(ubicazioniAdapter);
            }

            ubicazioniRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1,GridLayoutManager.VERTICAL, false));

        }

        if (getIntent().getStringExtra(Constants.UBICAZIONE_CANTIERE) != null){
            json = getIntent().getStringExtra(Constants.UBICAZIONE_CANTIERE);
        }
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
        NdefMessage msg = createNdefMessage();
        return msg;
    }

    @NonNull
    private NdefMessage createNdefMessage() {
        return new NdefMessage(
                new NdefRecord[] { createMime(
                        "application/vnd.it.imp.lalucente", json.getBytes())
                });
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);


            NdefMessage msg = createNdefMessage();

            writeTag(myTag, msg);

        }
    }


    public void writeTag(Tag tag, NdefMessage message) {
        if (tag != null) {
            try {
                Ndef ndefTag = Ndef.get(tag);
                if (ndefTag == null)  {
                    // Let's try to format the Tag in NDEF
                    NdefFormatable nForm = NdefFormatable.get(tag);
                    if (nForm != null) {
                        nForm.connect();
                        nForm.format(message);
                        nForm.close();
                    }
                }
                else {
                    ndefTag.connect();
                    ndefTag.writeNdefMessage(message);
                    ndefTag.close();
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }



}
