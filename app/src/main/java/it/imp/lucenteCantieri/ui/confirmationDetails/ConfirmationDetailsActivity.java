package it.imp.lucenteCantieri.ui.confirmationDetails;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import it.imp.lucenteCantieri.R;
import it.imp.lucenteCantieri.adapter.PhotoAdapter;
import it.imp.lucenteCantieri.model.TaskCantiereImg;
import it.imp.lucenteCantieri.servizi.AppService;
import it.imp.lucenteCantieri.servizi.AttivitaElenco;
import it.imp.lucenteCantieri.utils.Constants;
import it.imp.lucenteCantieri.workers.UploadWorker;

public class ConfirmationDetailsActivity extends AppCompatActivity {

    //UI
    TextView date;
    TextView taskTitle;
    TextView places;
    TextView taskDescription;
    TextView timer;
    ImageView camera;
    Button mConfermaButton;
    MultiAutoCompleteTextView mTxtNote;
    RecyclerView photoRecyclerView;

    //utils;
    public List<String> mFiltersSelected;
    Calendar calendar ;
    int Year, Month, Day ;
    Date mSelectedDate = new Date();
    AttivitaElenco mAttivitaElenco;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int REQUEST_CAPTURE_IMAGE = 100;
    String imageFilePath;
    PhotoAdapter photoAdapter;
    private Executor executor = Executors.newSingleThreadExecutor();
    Timer T;
    Date mInizio = new Date();
    long seconds = 0;
    long minutes = 0;
    long hours = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation_details);

        initView();
    }

    private void initView() {
        timer = findViewById(R.id.timer);
        date = findViewById(R.id.date);
        places = findViewById(R.id.places);
        taskTitle = findViewById(R.id.taskTitle);
        taskDescription = findViewById(R.id.taskDescription);
        camera = findViewById(R.id.camera);
        mTxtNote = findViewById(R.id.txtNote);
        photoRecyclerView = findViewById(R.id.photoList);


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if(pictureIntent.resolveActivity(getPackageManager()) != null){
                        //Create a file to store the image
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException e){
                            // Error occurred while creating the File
                        }
                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(ConfirmationDetailsActivity.this,
                                    "it.imp.lucenteCantieri.provider", photoFile);
                            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                    photoURI);
                            startActivityForResult(pictureIntent,
                                    REQUEST_CAPTURE_IMAGE);
                        }
                    }
                }
            }
        });


        //init tools
        calendar = Calendar.getInstance();
        Year = calendar.get(Calendar.YEAR) ;
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);

        //calendar init
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        date.setText(df.format(mSelectedDate));

        if (getIntent().getStringExtra(Constants.ID_TASK_CANTIERE) != null){
            String json = getIntent().getStringExtra(Constants.ID_TASK_CANTIERE);
            Gson gson = new Gson();
            mAttivitaElenco = gson.fromJson(json, AttivitaElenco.class);
        }

        if(getIntent().getStringArrayListExtra(Constants.PLACES) != null){
            mFiltersSelected = getIntent().getStringArrayListExtra(Constants.PLACES);

            initPlaces();
        }

        mConfermaButton = findViewById(R.id.confirm_button);
        mConfermaButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        regitraConfermaNelServer();
                    }
                }
        );

        initTask();

        initTimer();

    }

    private void initTimer() {
        T=new Timer();
        T.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        timer.setText(setTimerString());
                    }
                });
            }
        }, 1000, 1000);
    }

    private String setTimerString() {

        //vaue check
        if(seconds == 60){
            seconds = 0;
            minutes+=1;

            if(minutes == 60){
                minutes = 0;
                hours+=1;
            }

        }else{
            seconds += 1;
        }

        //print check
        if(hours == 0){
            if(minutes == 0){
                return Long.toString(seconds);
            }else {
                return minutes + ":" + seconds;
            }
        }else{
            return hours + ":" + minutes + ":" + seconds;
        }
    }


    private void initPlaces() {
        if(mFiltersSelected.size() > 0){
            StringBuilder places = new StringBuilder();

            for (String place: mFiltersSelected){
                places.append(place).append(" > ");
            }

            //clean last arrow
            this.places.setText(places.substring(0,places.length()-3));
        }
    }

    private void loadImgList() {
        AsyncTask<Void, Void, List<TaskCantiereImg>> task = new AsyncTask<Void, Void, List<TaskCantiereImg>>(){

            @Override
            protected List<TaskCantiereImg> doInBackground(Void... inp) {
                //get places tree from App Services
                try {
                    AppService appService = AppService.getInstance(ConfirmationDetailsActivity.this);
                    return appService.leggiImmagini(mAttivitaElenco.idTaskCantiere);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            @Override
            protected void onPostExecute(List<TaskCantiereImg> elenco) {
                //Set taskCantiereAdapter for listview
                photoAdapter = new PhotoAdapter(ConfirmationDetailsActivity.this, elenco);
                photoRecyclerView.setAdapter(photoAdapter);

                photoRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1,GridLayoutManager.VERTICAL, false));
                photoRecyclerView.setNestedScrollingEnabled(false);
                photoRecyclerView.setHasFixedSize(true);
                photoRecyclerView.setItemViewCacheSize(20);
                photoRecyclerView.setDrawingCacheEnabled(true);
                photoRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
            }
        };

        task.execute();

    }

    public void regitraConfermaNelServer(){
        AppService appService = AppService.getInstance(ConfirmationDetailsActivity.this);
        appService.closeAttivita(mAttivitaElenco.idTaskCantiere);
        new AlertDialog.Builder(this)
                .setTitle("Attenzione")
                .setMessage("Confermare l'attività? ")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        //stop timer
                        T.cancel();

                        Constraints constraints = new Constraints.Builder()
                                .setRequiredNetworkType(NetworkType.CONNECTED)
                                .build();

                        Data uploadData = new Data.Builder()
                                .putLong(Constants.ID_TASK_CANTIERE, mAttivitaElenco.idTaskCantiere)
                                .putString(Constants.NOTE, mTxtNote.getText().toString())
                                .putLong(Constants.INIZIO, dateToLong(mInizio))
                                .putLong(Constants.FINE, dateToLong(new Date()))
                                .build();

                        OneTimeWorkRequest uploadWork =
                                new OneTimeWorkRequest.Builder(UploadWorker.class)
                                        .setConstraints(constraints)
                                        .setInputData(uploadData)
                                        .build();


                        WorkManager.getInstance(ConfirmationDetailsActivity.this).enqueue(uploadWork);

                        finish();
                    }})
                .setNegativeButton(android.R.string.no, null).show();

    }

    private long dateToLong(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        return cal.get(Calendar.SECOND) + cal.get(Calendar.MINUTE) * 60 +  cal.get(Calendar.HOUR_OF_DAY) * 3600;
    }


    private void initTask() {
        taskTitle.setText(this.mAttivitaElenco.descLivello);
        taskDescription.setText(this.mAttivitaElenco.descrizione);

        loadImgList();
    }


    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(pictureIntent.resolveActivity(getPackageManager()) != null){
                    //Create a file to store the image
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException e){
                        // Error occurred while creating the File
                    }
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(this,"it.imp.lucenteCantieri.provider", photoFile);
                        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                photoURI);
                        startActivityForResult(pictureIntent,
                                REQUEST_CAPTURE_IMAGE);
                    }
                }
            }
            else
            {
                Toast.makeText(this, "Permessi negati", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAPTURE_IMAGE) {
            Toast.makeText(this, imageFilePath, Toast.LENGTH_LONG).show();

            AsyncTask<Void, Void, TaskCantiereImg> task = new AsyncTask<Void, Void, TaskCantiereImg>(){

                @Override
                protected TaskCantiereImg doInBackground(Void... inp) {
                    //get places tree from App Services
                    try {
                        AppService appService = AppService.getInstance(ConfirmationDetailsActivity.this);
                        return appService.salvaImmagine(mAttivitaElenco.idTaskCantiere, imageFilePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
                @Override
                protected void onPostExecute(TaskCantiereImg elenco) {
                    loadImgList();
                }
            };

            task.execute();

        }else if(resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, "Acquisizione immagine interrotta", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        new AlertDialog.Builder(this)
                .setTitle("Attenzione")
                .setMessage("Uscendo perderai i dati che hai inserito")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                AppService appService = AppService.getInstance(ConfirmationDetailsActivity.this);
                                appService.deleteAllPhotos(mAttivitaElenco.idTaskCantiere);

                            }
                        });
                        finish();
                    }})
                .setNegativeButton(android.R.string.no, null).show();

    }
}
