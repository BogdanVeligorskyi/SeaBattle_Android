package com.digitalartists.seabattle.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.EditText;
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

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_settings);
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


        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch isDarkModeOn = findViewById(R.id.switch_id);
        if (settings.getIsDarkMode() == 1) {
            isDarkModeOn.setChecked(true);
        }

        // EditText and TextView fields initialization
        EditText editCols = findViewById(R.id.columns_id);
        editCols.setText(String.format("%d", settings.getCols()));

        EditText editRows = findViewById(R.id.rows_id);
        editRows.setText(String.format("%d", settings.getRows()));

        EditText editMines = findViewById(R.id.numberOfMines_id);
        editMines.setText(String.format("%d", settings.getMinesNum()));

        TextView textError = findViewById(R.id.errorText_id);
        textError.setText("");

        // handler for 'Save' button
        findViewById(R.id.saveSettings_id).setOnClickListener(butPlay -> {
            settings.setCols(Integer.parseInt(String.valueOf(editCols.getText())));
            settings.setRows(Integer.parseInt(String.valueOf(editRows.getText())));
            settings.setMinesNum(Integer.parseInt(String.valueOf(editMines.getText())));

            // check if entered values are correct
            int res = isEnteredValuesCorrect(
                    settings.getRows(),
                    settings.getCols(),
                    settings.getMinesNum());

            if (res == 1) {
                textError.setText("Incorrect number of rows!");
            } else if (res == 2) {
                textError.setText("Incorrect number of columns!");
            } else if (res == 3) {
                textError.setText("Incorrect number of mines!");
            } else {
                textError.setText("");
                if (isDarkModeOn.isChecked()) {
                    settings.setIsDarkMode(1);
                } else {
                    settings.setIsDarkMode(0);
                }

                // save settings to file if data are correct
                try {
                    FileProcessing.saveSettings(context, settings);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                finish();
            }

        });

    }


    // check entered values whether they are correct or not
    private int isEnteredValuesCorrect(int rows, int cols, int mines) {

        // check number of rows
        if (rows <= 0 || rows > 25) {
            return 1;
        }

        // check number of columns
        if (cols <= 0 || cols > 25) {
            return 2;
        }

        // check number of mines
        if (mines <= 0 || mines > rows*cols / 3) {
            return 3;
        }

        return 0;

    }


    // save window state
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MainActivity.SETTINGS, settings);
    }


    // after return to this activity
    @Override
    public void onResume(){
        super.onResume();
    }
}
