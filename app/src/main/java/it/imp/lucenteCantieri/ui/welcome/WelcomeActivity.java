package it.imp.lucenteCantieri.ui.welcome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.button.MaterialButton;

import it.imp.lucenteCantieri.MainActivity;
import it.imp.lucenteCantieri.R;
import it.imp.lucenteCantieri.servizi.Settings;
import it.imp.lucenteCantieri.ui.barcode.BarcodeCaptureActivity;

public class WelcomeActivity extends AppCompatActivity {

    Button startButton;
    private static final int RC_BARCODE_CAPTURE = 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        initView();

    }

    void initView(){
        startButton = findViewById(R.id.startButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this, BarcodeCaptureActivity.class);
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                intent.putExtra(BarcodeCaptureActivity.UseFlash, true);
                intent.putExtra(BarcodeCaptureActivity.AutoSelect, true);

                startActivityForResult(intent, RC_BARCODE_CAPTURE);

            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try {
            if (requestCode == RC_BARCODE_CAPTURE) {
                if (resultCode == CommonStatusCodes.SUCCESS && intent != null) {

                    Barcode bc = intent.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Settings.getInstance().save(getApplicationContext(), bc.displayValue);
                        Intent start = new Intent(WelcomeActivity.this, MainActivity.class);
                        start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        start.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(start);
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
}
