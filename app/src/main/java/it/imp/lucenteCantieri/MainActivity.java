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

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import it.imp.lucenteCantieri.servizi.Settings;
import it.imp.lucenteCantieri.ui.barcode.BarcodeCaptureActivity;
import it.imp.lucenteCantieri.ui.calendar.CalendarActivity;
import it.imp.lucenteCantieri.ui.syncTasks.SyncStrutturaTask;

public class MainActivity extends AppCompatActivity {

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final int RC_CALENDAR = 9002;

    private AppBarConfiguration mAppBarConfiguration;

    TextView navTitleText;
    TextView navSubtitleText;
    NavController navController;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
                startActivityForResult(intent, RC_CALENDAR);
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        View navigationHeader = navigationView.getHeaderView(0);
        navTitleText = navigationHeader.findViewById(R.id.navTitleText);
        navSubtitleText = navigationHeader.findViewById(R.id.navSubtitleText);


        ButterKnife.bind(this);

        inizializzaView();
    }

    private void inizializzaView() {
        try {
            Settings settings= Settings.getInstance();
            settings.read(this.getApplicationContext());

            scriviImpostazioni(settings);

        } catch (Exception e) {
            Log.i(this.getLocalClassName(), e.getMessage());
        }

    }

    private void scriviImpostazioni(Settings settings) {
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
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_sync:
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

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try {
            switch (requestCode) {
                case RC_BARCODE_CAPTURE:
                    if (resultCode == CommonStatusCodes.SUCCESS && intent != null) {

                        Barcode bc = intent.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Settings.getInstance().save(getApplicationContext(), bc.displayValue);
                        }

                        scriviImpostazioni(Settings.getInstance());

                    } else {
                        Toast.makeText(getApplicationContext(), R.string.operazione_annullata, Toast.LENGTH_SHORT);
                    }

                    return;
                case RC_CALENDAR:

                    return;
                default:
                    super.onActivityResult(requestCode, resultCode, intent);
            }
        } catch (Exception ex){
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT);
        }

    }

}
