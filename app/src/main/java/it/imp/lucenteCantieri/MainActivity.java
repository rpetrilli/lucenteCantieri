package it.imp.lucenteCantieri;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import butterknife.ButterKnife;
import it.imp.lucenteCantieri.servizi.Settings;
import it.imp.lucenteCantieri.ui.barcode.BarcodeCaptureActivity;
import it.imp.lucenteCantieri.ui.syncTasks.SyncStrutturaTask;

public class MainActivity extends AppCompatActivity  implements DatePickerDialog.OnDateSetListener {



    private AppBarConfiguration mAppBarConfiguration;

    TextView navTitleText;
    TextView navSubtitleText;
    Calendar calendar ;
    DatePickerDialog datePickerDialog ;
    int Year, Month, Day ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        calendar = Calendar.getInstance();

        Year = calendar.get(Calendar.YEAR) ;
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.


        View navigationHeader = navigationView.getHeaderView(0);
        navTitleText = navigationHeader.findViewById(R.id.navTitleText);
        navSubtitleText = navigationHeader.findViewById(R.id.navSubtitleText);


        ButterKnife.bind(this);
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
                return true;
            case R.id.action_associa:


                return true;
            case R.id.action_get_struttura:
                (new SyncStrutturaTask(MainActivity.this)).execute(new String[]{""});

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public void onDateSet(DatePickerDialog view, int Year, int Month, int Day) {

        String date = "Data : " + Day + "-" + Month + "-" + Year;

        Toast.makeText(MainActivity.this, date, Toast.LENGTH_LONG).show();
    }

}
