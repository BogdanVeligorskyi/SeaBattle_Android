package com.digitalartists.seabattle.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
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
    private EditText hostIPEditText;
    private EditText guestIPEditText;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch isDarkModeOn;


    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_settings);

        startInit(savedInstanceState);

        initSettings();

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

        isDarkModeOn = findViewById(R.id.switch_id);

        // handler for 'Save' button
        findViewById(R.id.saveSettings_id).setOnClickListener(butPlay -> {
            if (isDarkModeOn.isChecked()) {
                settings.setIsDarkMode(1);
            } else {
                settings.setIsDarkMode(0);
            }

            settings.setHostIPAddress(String.valueOf(hostIPEditText.getText()));
            settings.setGuestIPAddress(String.valueOf(guestIPEditText.getText()));

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
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    @SuppressLint("NonConstantResourceId")
    private void handlerRadioButtons() {
        // 'Theme' radio group
        RadioGroup roleRadioGroup = findViewById(R.id.role_id);
        roleRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // on theme change
            switch (checkedId) {
                case R.id.hostButton_id:
                    settings.setRole("HOST");
                    break;
                case R.id.guestButton_id:
                    settings.setRole("GUEST");
                    break;
            }
        });
    }


    private void initSettings() {

        RadioButton hostButtonRadioGroup = findViewById(R.id.hostButton_id);
        RadioButton guestButtonRadioGroup = findViewById(R.id.guestButton_id);
        isDarkModeOn = findViewById(R.id.switch_id);
        if (settings.getIsDarkMode() == 1) {
            isDarkModeOn.setChecked(true);
        }

        Log.d("ROLE", ""+settings.getRole());

        if (settings.getRole().contains("HOST")) {
            hostButtonRadioGroup.setChecked(true);
        } else {
            guestButtonRadioGroup.setChecked(true);
        }

        hostIPEditText = findViewById(R.id.hostIP_id);
        guestIPEditText = findViewById(R.id.guestIP_id);

        hostIPEditText.setText(settings.getHostIPAddress());
        guestIPEditText.setText(settings.getGuestIPAddress());

    }

}
