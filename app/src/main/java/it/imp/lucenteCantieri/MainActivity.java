package it.imp.lucenteCantieri;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBarDrawerToggle;

import com.google.gson.Gson;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import androidx.core.view.MenuCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import butterknife.ButterKnife;
import it.imp.lucenteCantieri.adapter.MenuLevelAdapter;
import it.imp.lucenteCantieri.adapter.TaskCantiereAdapter;
import it.imp.lucenteCantieri.servizi.AppService;
import it.imp.lucenteCantieri.servizi.AttivitaElenco;
import it.imp.lucenteCantieri.servizi.NodoAlbero;
import it.imp.lucenteCantieri.servizi.Settings;
import it.imp.lucenteCantieri.servizi.UbicazioneCantiere;
import it.imp.lucenteCantieri.ui.barcode.BarcodeCaptureActivity;
import it.imp.lucenteCantieri.ui.reporting.SegnalazioneActivity;
import it.imp.lucenteCantieri.ui.nfc.NFCWriterActivity;
import it.imp.lucenteCantieri.ui.syncTasks.SyncElencoAttivitaTask;
import it.imp.lucenteCantieri.ui.syncTasks.SyncStrutturaTask;
import it.imp.lucenteCantieri.utils.Constants;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    //UI
    ActionBarDrawerToggle mDrawerToggle;
    TextView navTitleText;
    TextView navSubtitleText;
    TextView mTextViewDate;
    TextView mUbicazioniText;
    TextView mTextViewTitle;
    ImageView mNfcImgView;
    ImageView mWriteTagNFC;
    RecyclerView levelRecycleView;
    RecyclerView taskRecyclerView;
    DrawerLayout mDrawer;
    FloatingActionButton mFabChangeDate;
    Toolbar toolbar;

    LinearLayout errorLayout;

    //utils
    static final int RC_BARCODE_CAPTURE = 9001;
    Calendar calendar ;
    DatePickerDialog datePickerDialog;
    int Year, Month, Day ;
    MenuLevelAdapter levelNameAdapter;
    TaskCantiereAdapter taskCantiereAdapter;
    Date mSelectedDate = new Date();
    ArrayList<String> mFilterSelected;

    NodoAlbero nodoAlbero;
    NodoAlbero mSelectedNodoAlbero;
    private Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        ButterKnife.bind(this);
    }

    void initView(){
        //init UI
        errorLayout = findViewById(R.id.errorLayout);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTextViewDate = findViewById(R.id.date);
        mUbicazioniText = findViewById(R.id.places);
        mFabChangeDate = findViewById(R.id.fab);
        mDrawer = findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawer,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        //drawer title
        navTitleText = mDrawer.findViewById(R.id.navTitleText);
        navSubtitleText = mDrawer.findViewById(R.id.navSubtitleText);
        initDrawerText();

        //toolbar
        mTextViewTitle = toolbar.findViewById(R.id.title);
        mNfcImgView = toolbar.findViewById(R.id.nfc);
        mWriteTagNFC = toolbar.findViewById(R.id.writeTagNFC);
        mTextViewTitle.setText("Tasks");

        //recycler view
        levelRecycleView = findViewById(R.id.levelListView);
        taskRecyclerView = findViewById(R.id.taskListView);

        //init tools
        calendar = Calendar.getInstance();
        Year = calendar.get(Calendar.YEAR) ;
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);
        mFilterSelected = new ArrayList<>();

        //calendar init
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        mTextViewDate.setText(df.format(mSelectedDate));

        //hamburger menu
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle.syncState();

        //read drawer
        refreshDrawer();

        //observables
        mNfcImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nodoAlbero != null) {
                    UbicazioneCantiere ubicazione = AppService.getInstance(MainActivity.this).ubicazionecantiere(nodoAlbero);
                    Gson gson = new Gson();
                    String json = gson.toJson(ubicazione);

                    Intent nfc = new Intent(MainActivity.this, NFCWriterActivity.class);
                    nfc.putStringArrayListExtra(Constants.PLACES, mFilterSelected);
                    nfc.putExtra(Constants.UBICAZIONE_CANTIERE, json);
                    nfc.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(nfc);
                }else{
                    showErrorMessage(getString(R.string.info), getString(R.string.NO_UBICAZIONE));
                }
            }
        });

        mWriteTagNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nodoAlbero != null) {
                    Gson gson = new Gson();
                    String nodo = gson.toJson(nodoAlbero);

                    Intent comunication = new Intent(MainActivity.this, SegnalazioneActivity.class);
                    comunication.putStringArrayListExtra(Constants.PLACES, mFilterSelected);
                    comunication.putExtra(Constants.NODO, nodo);
                    comunication.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(comunication);

                }else{

                    showErrorMessage(getString(R.string.info), getString(R.string.NO_UBICAZIONE));
                }

            }
        });

        //observables
        mFabChangeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog = DatePickerDialog.newInstance(MainActivity.this, Year, Month, Day);
                datePickerDialog.setThemeDark(false);
                datePickerDialog.showYearPickerFirst(false);
                datePickerDialog.setTitle("Seleziona data tasks");


                datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialogInterface) {

                        Toast.makeText(MainActivity.this, "Scelta annullata", Toast.LENGTH_SHORT).show();
                    }
                });

                datePickerDialog.show(getFragmentManager(), "DatePickerDialog");
            }
        });



        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawer.openDrawer(Gravity.LEFT);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSelectedNodoAlbero != null){
            this.readTaskDaVisualizzare(mSelectedNodoAlbero);
        }
    }

    /*
            refresh left drawer with places tree
         */
    private void refreshDrawer() {

        AsyncTask<Void, Void, List<NodoAlbero>> task = new AsyncTask<Void, Void, List<NodoAlbero>>(){

            @Override
            protected List<NodoAlbero> doInBackground(Void... mainActivities) {
                //get places tree from App Services
                try {
                    AppService appService = AppService.getInstance(MainActivity.this);
                    return appService.getAlberoDrawer();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<NodoAlbero> elenco) {
                if (elenco == null){
                    showErrorMessage(getString(R.string.info), getString(R.string.NO_UBICAZIONE));
                    return;
                }

                //set adapter for places list view
                levelNameAdapter = new MenuLevelAdapter(MainActivity.this, elenco);
                //Set levelNameAdapter for listview
                if (elenco.size() > 0) {
                    levelRecycleView.setAdapter(levelNameAdapter);
                }

                levelRecycleView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1,GridLayoutManager.VERTICAL, false));

            }
        };

        task.execute();
    }

    /*
        load task from DB and Api
     */

    public void readTaskDaVisualizzare(NodoAlbero item) {
        //close drawer
        this.mDrawer.closeDrawer(Gravity.LEFT);
        this.mSelectedNodoAlbero = item;

        AsyncTask<Void, Void, List<AttivitaElenco>> task = new AsyncTask<Void, Void, List<AttivitaElenco>>(){

            @Override
            protected List<AttivitaElenco> doInBackground(Void... mainActivities) {

                //get tasks from App Services
                try {
                    AppService appService = AppService.getInstance(MainActivity.this);
                    return appService.leggiTaskCantiere(mSelectedDate, item);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            @Override
            protected void onPostExecute(List<AttivitaElenco> elenco) {
                if (elenco == null || elenco.isEmpty()){
                    showErrorText();
                    return;
                }else{
                    //init error layout
                    errorLayout.setVisibility(View.INVISIBLE);

                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
                    errorLayout.setLayoutParams(lp);
                }

                //Set taskCantiereAdapter for listview
                taskCantiereAdapter = new TaskCantiereAdapter(MainActivity.this, elenco, false, mFilterSelected);
                taskRecyclerView.setAdapter(taskCantiereAdapter);

                taskRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1,GridLayoutManager.VERTICAL, false));

                //hide recycler view
                taskRecyclerView.setVisibility(View.VISIBLE);

                getFilters(item);
            }
        };

        task.execute();
    }

    private void showErrorText() {
        //show error text
        this.errorLayout.setVisibility(View.VISIBLE);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        lp.weight = 2;

        this.errorLayout.setLayoutParams(lp);

        //hide recycler view
        this.taskRecyclerView.setVisibility(View.INVISIBLE);
    }

    public void getFilters(NodoAlbero item){
        //get places level filters
        AppService appService = AppService.getInstance(MainActivity.this);
        mFilterSelected = appService.descrizioniFiltro(item);
        initPlaces();
    }

    private void initPlaces() {
        if(mFilterSelected.size() > 0){
            StringBuilder places = new StringBuilder();

            for (String place: mFilterSelected){
                places.append(place).append(" > ");
            }

            //clean last arrow
            this.mUbicazioniText.setText(places.substring(0,places.length()-3));
        }
    }

    /*
        init drawer header text
     */
    private void initDrawerText() {
        try {
            Settings settings= Settings.getInstance();
            settings.read(this.getApplicationContext());

            navTitleText.setText(settings.denominazione);
            navSubtitleText.setText(settings.descSquadra);

        } catch (Exception e) {
            Log.i(this.getLocalClassName(), e.getMessage());
        }

    }

    /*
        update current nodo

     */

    public void updateNodoAlbero(NodoAlbero item){
        this.nodoAlbero = item;
    }


    /*
        create option menu
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuCompat.setGroupDividerEnabled(menu, true);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /*
        on option item selected method
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();
        switch (id){
            case R.id.action_sync:
                (new SyncElencoAttivitaTask(MainActivity.this)).execute(new String[]{""});
                return true;

            case R.id.action_associa:
                Intent intent = new Intent(MainActivity.this, BarcodeCaptureActivity.class);
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                intent.putExtra(BarcodeCaptureActivity.UseFlash, true);
                intent.putExtra(BarcodeCaptureActivity.AutoSelect, true);

                startActivityForResult(intent, RC_BARCODE_CAPTURE);

                return true;

            case R.id.action_get_struttura:
                (new SyncStrutturaTask(MainActivity.this)).execute(new String[]{""});
                refreshDrawer();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /*
        set calendar date method
     */

    @Override
    public void onDateSet(DatePickerDialog view, int Year, int Month, int Day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, datePickerDialog.getSelectedDay().getYear());
        cal.set(Calendar.MONTH, datePickerDialog.getSelectedDay().getMonth());
        cal.set(Calendar.DAY_OF_MONTH, datePickerDialog.getSelectedDay().getDay());
        mSelectedDate = cal.getTime();

        //refresh UI
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        mTextViewDate.setText(df.format(mSelectedDate));

        //download task
        readTaskDaVisualizzare(nodoAlbero);
    }


    /*
        google camera callback
     */

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try {
            if (requestCode == RC_BARCODE_CAPTURE) {
                if (resultCode == CommonStatusCodes.SUCCESS && intent != null) {

                    Barcode bc = intent.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Settings.getInstance().save(getApplicationContext(), bc.displayValue);
                        initDrawerText();

                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                AppService.getInstance(MainActivity.this).init(MainActivity.this);
                                refreshDrawer();
                            }
                        });

                    }

                } else {
                    Toast.makeText(getApplicationContext(), R.string.operazione_annullata, Toast.LENGTH_SHORT).show();
                }

            } else {
                super.onActivityResult(requestCode, resultCode, intent);
            }
        } catch (Exception ex){
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    /*
        public method to show error message
     */
    public void showErrorMessage(String titolo, String errorMessage){
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setMessage(errorMessage);
        dialog.setTitle(titolo);
        dialog.setCancelable(true);
        dialog.show();
    }


}
