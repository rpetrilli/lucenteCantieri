package it.imp.lucenteCantieri.ui.welcome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import it.imp.lucenteCantieri.MainActivity;
import it.imp.lucenteCantieri.R;
import it.imp.lucenteCantieri.servizi.Settings;
import it.imp.lucenteCantieri.ui.barcode.BarcodeCaptureActivity;

public class WelcomeActivity extends AppCompatActivity {

    private static final int RC_BARCODE_CAPTURE = 9001;
    private ViewPager viewPager;
    private Button button;
    private SliderPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // making activity full screen
        getWindow().getDecorView()
                .setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        // hide action bar you can use NoAction theme as well
        Objects.requireNonNull(getSupportActionBar()).hide();
        // bind views
        viewPager = findViewById(R.id.pagerIntroSlider);
        TabLayout tabLayout = findViewById(R.id.tabs);
        button = findViewById(R.id.button);
        // init slider pager adapter
        adapter = new SliderPagerAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        // set adapter
        viewPager.setAdapter(adapter);
        // set dot indicators
        tabLayout.setupWithViewPager(viewPager);
        // make status bar transparent
        changeStatusBarColor();
        button.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (viewPager.getCurrentItem() < adapter.getCount()) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);

                    if(viewPager.getCurrentItem() == 1){
                        Intent intent = new Intent(WelcomeActivity.this, BarcodeCaptureActivity.class);
                        intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                        intent.putExtra(BarcodeCaptureActivity.UseFlash, true);
                        intent.putExtra(BarcodeCaptureActivity.AutoSelect, true);

                        startActivityForResult(intent, RC_BARCODE_CAPTURE);
                    }
                }
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override public void onPageSelected(int position) {
                if (position == adapter.getCount() - 1) {
                    button.setText(R.string.get_started);
                } else {
                    button.setText(R.string.next);
                }
            }
            @Override public void onPageScrollStateChanged(int state) {
            }
        });
    }
    private void changeStatusBarColor() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
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
