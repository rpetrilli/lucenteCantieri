package it.imp.lucenteCantieri;

import android.content.DialogInterface;
import android.os.Bundle;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import it.imp.lucenteCantieri.adapter.MenuLevelAdapter;
import it.imp.lucenteCantieri.servizi.AppService;
import it.imp.lucenteCantieri.servizi.NodoAlbero;
import it.imp.lucenteCantieri.servizi.Settings;
import it.imp.lucenteCantieri.ui.syncTasks.SyncStrutturaTask;

public class MainActivity extends AppCompatActivity  implements DatePickerDialog.OnDateSetListener {

    //UI
    ActionBarDrawerToggle mDrawerToggle;
    TextView navTitleText;
    TextView navSubtitleText;
    TextView date;
    TextView dayName;
    RecyclerView levelRecycleView;

    //utils
    Calendar calendar ;
    DatePickerDialog datePickerDialog ;
    int Year, Month, Day ;
    private MenuLevelAdapter levelNameAdapter;
    private List<NodoAlbero> levelNameList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        initView();


        ButterKnife.bind(this);


    }

    void initView(){
        //init UI
        calendar = Calendar.getInstance();
        Year = calendar.get(Calendar.YEAR) ;
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);

        //init tools
        levelNameList = new ArrayList<NodoAlbero>();

        date = findViewById(R.id.date);
        dayName = findViewById(R.id.dayName);
        FloatingActionButton fab = findViewById(R.id.fab);


        //hamburger menu
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        AppBarConfiguration mAppBarConfiguration = new AppBarConfiguration.Builder()
                .setDrawerLayout(drawer)
                .build();

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawer,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );


        mDrawerToggle.syncState();

        //drawer title
        navTitleText = drawer.findViewById(R.id.navTitleText);
        navSubtitleText = drawer.findViewById(R.id.navSubtitleText);
        initDrawerText();

        //recycle view init
        initRecycleView();

        //calendar init
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, new Date().getYear());
        calendar.set(Calendar.DAY_OF_YEAR, new Date().getDay());
        String days[] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);

        this.dayName.setText(days[dayIndex - 1]);

        //observables
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



        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });

    }

    private void initRecycleView() {
        levelRecycleView = (RecyclerView)findViewById(R.id.levelListView);

        //Get level name list
        AppService appService = new AppService(this);
        levelNameList.addAll(appService.getAlberoDrawer());

        //Init levelNameAdapter
        levelNameAdapter = new MenuLevelAdapter(this, levelNameList);
        //Set levelNameAdapter for listview
        if (levelNameList.size() > 0) {
            levelRecycleView.setAdapter(levelNameAdapter);
        }

        levelRecycleView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

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
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

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
