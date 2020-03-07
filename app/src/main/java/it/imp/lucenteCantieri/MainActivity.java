package it.imp.lucenteCantieri;

import android.annotation.SuppressLint;
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
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.navigation.NavigationView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import it.imp.lucenteCantieri.adapter.MenuLevelAdapter;
import it.imp.lucenteCantieri.adapter.TaskCantiereAdapter;
import it.imp.lucenteCantieri.servizi.AppService;
import it.imp.lucenteCantieri.servizi.AttivitaElenco;
import it.imp.lucenteCantieri.servizi.NodoAlbero;
import it.imp.lucenteCantieri.servizi.Settings;
import it.imp.lucenteCantieri.ui.barcode.BarcodeCaptureActivity;
import it.imp.lucenteCantieri.ui.nfc.NFCSync;
import it.imp.lucenteCantieri.ui.syncTasks.SyncElencoAttivitaTask;
import it.imp.lucenteCantieri.ui.syncTasks.SyncStrutturaTask;

public class MainActivity extends AppCompatActivity  implements DatePickerDialog.OnDateSetListener {

    //UI
    ActionBarDrawerToggle mDrawerToggle;
    TextView navTitleText;
    TextView navSubtitleText;
    TextView date;
    TextView title;
    ImageView nfc;
    RecyclerView levelRecycleView;
    RecyclerView taskRecyclerView;
    DrawerLayout drawer;
    Toolbar toolbar;

    //utils
    static final int RC_BARCODE_CAPTURE = 9001;
    Calendar calendar ;
    DatePickerDialog datePickerDialog ;
    int Year, Month, Day ;
    MenuLevelAdapter levelNameAdapter;
    TaskCantiereAdapter taskCantiereAdapter;
    Date mSelectedDate = new Date();
    ArrayList<String> mFilterSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        ButterKnife.bind(this);

    }

    void initView(){
        //init UI
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        date = findViewById(R.id.date);
        FloatingActionButton fab = findViewById(R.id.fab);
        drawer = findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawer,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        //drawer title
        navTitleText = drawer.findViewById(R.id.navTitleText);
        navSubtitleText = drawer.findViewById(R.id.navSubtitleText);
        initDrawerText();

        //toolbar
        title = toolbar.findViewById(R.id.title);
        nfc = toolbar.findViewById(R.id.nfc);
        title.setText("Tasks");

        //recycler view
        levelRecycleView = findViewById(R.id.levelListView);
        taskRecyclerView = findViewById(R.id.taskListView);

        //init tools
        calendar = Calendar.getInstance();
        Year = calendar.get(Calendar.YEAR) ;
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);
        mFilterSelected = new ArrayList<>();

        //TODO remove
        mFilterSelected.add("Fab 1");
        mFilterSelected.add("Bagno");

        //calendar init
        SimpleDateFormat df = new SimpleDateFormat("dd/MM");
        date.setText(df.format(mSelectedDate));

        //hamburger menu
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle.syncState();

        refreshDrawer();

        //observables
        nfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nfc = new Intent(MainActivity.this, NFCSync.class);
                nfc.putStringArrayListExtra("places", mFilterSelected);
                nfc.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(nfc);
            }
        });

        //observables
        fab.setOnClickListener(new View.OnClickListener() {
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
                drawer.openDrawer(Gravity.LEFT);
            }
        });

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
                    showErrorMessage("Errore di sincronizzazione dei luoghi");
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

    public void readTasks(NodoAlbero item) {
        //close drawer
        this.drawer.closeDrawer(Gravity.LEFT);

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
                if (elenco == null){
                    showErrorMessage("Nessuna attività pianificata per questa data");
                    return;
                }

                //Set taskCantiereAdapter for listview
                if (elenco.size() > 0) {
                    taskCantiereAdapter = new TaskCantiereAdapter(MainActivity.this, elenco);
                    taskRecyclerView.setAdapter(taskCantiereAdapter);
                }

                taskRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1,GridLayoutManager.VERTICAL, false));

                //get places level filters
                AppService appService = AppService.getInstance(MainActivity.this);
                mFilterSelected = appService.descrizioniFiltro(item);
            }
        };

        task.execute();
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
        create option menu
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
                levelNameAdapter.notifyDataSetChanged();

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
    public void showErrorMessage(String errorMessage){
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setMessage(errorMessage);
        dialog.setTitle(R.string.errore);
        dialog.setCancelable(true);
        dialog.show();
    }


}
