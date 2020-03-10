package it.imp.lucenteCantieri.ui.confirmationDetails;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.app.Activity;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it.imp.lucenteCantieri.R;
import it.imp.lucenteCantieri.model.TaskCantiereImg;
import it.imp.lucenteCantieri.servizi.AppService;
import it.imp.lucenteCantieri.servizi.AttivitaElenco;
import it.imp.lucenteCantieri.utils.Constants;
import it.imp.lucenteCantieri.workers.UploadWorker;

public class ConfirmationDetailsActivity extends AppCompatActivity {

    //UI
    TextView date;
    TextView taskTitle;
    TextView taskDescription;
    ImageView camera;
    Button mConfermaButton;
    MultiAutoCompleteTextView mTxtNote;

    //utils;
    List<String> mFiltersSelected;
    Calendar calendar ;
    int Year, Month, Day ;
    Date mSelectedDate = new Date();
    AttivitaElenco mAttivitaElenco;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int REQUEST_CAPTURE_IMAGE = 100;
    String imageFilePath;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation_details);

        initView();
    }

    private void initView() {
        date = findViewById(R.id.date);
        taskTitle = findViewById(R.id.taskTitle);
        taskDescription = findViewById(R.id.taskDescription);
        camera = findViewById(R.id.camera);
        mTxtNote = findViewById(R.id.txtNote);


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
        if (getIntent().getStringExtra(Constants.PLACES) != null){
            //TODO: .....
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
                //TODO: popolare la lista a schermo
            }
        };

        task.execute();

    }

    public void regitraConfermaNelServer(){
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        Data uploadData = new Data.Builder()
                .putLong(Constants.ID_TASK_CANTIERE, mAttivitaElenco.idTaskCantiere)
                .putString(Constants.NOTE, mTxtNote.getText().toString())
                .build();

        OneTimeWorkRequest uploadWork =
                new OneTimeWorkRequest.Builder(UploadWorker.class)
                        .setConstraints(constraints)
                        .setInputData(uploadData)
                        .build();


        WorkManager.getInstance(this).enqueue(uploadWork);

        finish();

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
                        Uri photoURI = FileProvider.getUriForFile(this,                                                                                                    "com.example.android.provider", photoFile);
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
                }
            };

            task.execute();






        }else if(resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, "Acquisizione immagine interrotta", Toast.LENGTH_LONG).show();
        }
    }
}