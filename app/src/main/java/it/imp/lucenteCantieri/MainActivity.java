package it.imp.lucenteCantieri;

import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import it.imp.lucenteCantieri.servizi.Settings;
import it.imp.lucenteCantieri.ui.syncTasks.SyncStrutturaTask;

public class MainActivity extends AppCompatActivity  implements DatePickerDialog.OnDateSetListener {

    private AppBarConfiguration mAppBarConfiguration;
    private ActionBarDrawerToggle mDrawerToggle;
    NavController navController;
    MaterialSpinner spinner;

    TextView navTitleText;
    TextView navSubtitleText;
    Calendar calendar ;
    DatePickerDialog datePickerDialog ;
    int Year, Month, Day ;
    TextView date;
    TextView dayName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        calendar = Calendar.getInstance();
        date = findViewById(R.id.date);
        dayName = findViewById(R.id.dayName);

        Year = calendar.get(Calendar.YEAR) ;
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);

        initView();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog = DatePickerDialog.newInstance(MainActivity.this, Year, Month, Day);
                datePickerDialog.setThemeDark(false);
                datePickerDialog.showYearPickerFirst(false);
                datePickerDialog.setTitle("Date Picker");


                datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialogInterface) {

                        Toast.makeText(MainActivity.this, "Scelta annullata", Toast.LENGTH_SHORT).show();
                    }
                });

                datePickerDialog.show(getFragmentManager(), "DatePickerDialog");
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder()
                .setDrawerLayout(drawer)
                .build();

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawer,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        ){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };


        mDrawerToggle.syncState();

        spinner = drawer.findViewById(R.id.spinner);
        spinner.setItems("Fabbricato 1","Uffici", "Bagni");
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
            }
        });

        View navigationHeader = navigationView.getHeaderView(0);
        //navTitleText = navigationHeader.findViewById(R.id.navTitleText);
        // navSubtitleText = navigationHeader.findViewById(R.id.navSubtitleText);


        ButterKnife.bind(this);

        drawer.openDrawer(Gravity.LEFT);


    }

    void initView(){

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, new Date().getYear());
        calendar.set(Calendar.DAY_OF_YEAR, new Date().getDay());
        String days[] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);

        this.dayName.setText(days[dayIndex - 1]);

        initDrawerText();
    }

    private void initDrawerText() {
        try {
            Settings settings= Settings.getInstance();
            settings.read(this.getApplicationContext());

            writeSettings(settings);

        } catch (Exception e) {
            Log.i(this.getLocalClassName(), e.getMessage());
        }

    }

    private void writeSettings(Settings settings) {
        navTitleText.setText(settings.denominazione);
        navSubtitleText.setText(settings.descSquadra);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_sync:
                (new SyncStrutturaTask(MainActivity.this)).execute(new String[]{""});
                return true;
            case R.id.action_associa:


                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public void onDateSet(DatePickerDialog view, int Year, int Month, int Day) {

        this.date.setText(Day + "/" + Month + "/" + Year);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Year);
        calendar.set(Calendar.DAY_OF_YEAR, Day);
        String days[] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);

        this.dayName.setText(days[dayIndex - 1]);
    }

}
