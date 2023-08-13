package com.digitalartists.seabattle.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.digitalartists.seabattle.R;
import com.digitalartists.seabattle.model.FileProcessing;
import com.digitalartists.seabattle.model.Settings;
import java.io.IOException;

// Settings Activity class
public class SettingsActivity extends AppCompatActivity {

    // settings object
    private Settings settings;
    private boolean isPaired;


    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_settings);

        startInit(savedInstanceState);

        intitTextField();


        handlerRadioButtons();
        handlerSaveButton();

    }


    // save window state
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MainActivity.SETTINGS, settings);
    }


    // after return to this activity
    @Override
    public void onResume() {
        super.onResume();
    }


    private void handlerSaveButton() {

        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch isDarkModeOn = findViewById(R.id.switch_id);
        if (settings.getIsDarkMode() == 1) {
            isDarkModeOn.setChecked(true);
        }
        // handler for 'Save' button
        findViewById(R.id.saveSettings_id).setOnClickListener(butPlay -> {
            if (isDarkModeOn.isChecked()) {
                settings.setIsDarkMode(1);
            } else {
                settings.setIsDarkMode(0);
            }

            // save settings to file if data are correct
            try {
                FileProcessing.saveSettings(SettingsActivity.this, settings);
            } catch (IOException e) {
                e.printStackTrace();
            }

            finish();
        });
    }


    private void startInit(Bundle savedInstanceState) {
        Context context = getApplicationContext();
        if (savedInstanceState != null) {
            settings = savedInstanceState.getParcelable(MainActivity.SETTINGS);
        } else {
            try {
                settings = FileProcessing.loadSettings(context);
                isPaired = false;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    private void intitTextField() {
        TextView textViewIsPaired = findViewById(R.id.isPairedState_id);
        if (isPaired) {
            textViewIsPaired.setTextColor(Color.GREEN);
            textViewIsPaired.setText("State: paired");
        } else {
            textViewIsPaired.setTextColor(Color.RED);
            textViewIsPaired.setText("State: not paired");
        }
    }


    private void handlerRadioButtons() {
        // 'Theme' radio group
        RadioGroup roleRadioGroup = findViewById(R.id.role_id);
        RadioButton hostButtonRadioGroup = findViewById(R.id.hostButton_id);
        RadioButton guestButtonRadioGroup = findViewById(R.id.guestButton_id);
        roleRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // on theme change
            switch (checkedId) {
                case R.id.hostButton_id:
                    break;
                case R.id.guestButton_id:
                    break;
            }
        });
    }


}
