package com.digitalartists.seabattle.model;

import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

// class which contains methods for files processing
public class FileProcessing {

    // filenames
    public static final String SETTINGS_FILENAME = "settings.txt";

    // load settings from settings.txt file
    public static Settings loadSettings(Context context) throws IOException {

        Settings settings = null;
        File file = context.getFileStreamPath(SETTINGS_FILENAME);

        // if application is run for the first time - don`t read settings.txt
        if (!file.exists()) {
            settings = new Settings(1, "192.168.0.101", "192.168.0.102", "HOST");
            return settings;
        }

        // otherwise read settings from file
        InputStream is = context.openFileInput(SETTINGS_FILENAME);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String s;
        while ((s = reader.readLine()) != null) {
            //Log.d("s.length", ""+s.length());
            if (s.length() == 0) {
                break;
            }
            String[] valuesSettings = s.split(",");
            settings = new Settings(Integer.parseInt(valuesSettings[0]), valuesSettings[1], valuesSettings[2], valuesSettings[3]);
        }
        return settings;
    }


    // save settings to settings.txt file
    public static void saveSettings(Context context, Settings settings) throws IOException {
        try {
            OutputStreamWriter outputStreamWriter =
                    new OutputStreamWriter
                            (context.openFileOutput(SETTINGS_FILENAME,
                                    Context.MODE_PRIVATE));
            outputStreamWriter.write("" + settings.getIsDarkMode()
                    + "," + settings.getHostIPAddress()
                    + "," + settings.getGuestIPAddress()
                    + "," + settings.getRole());
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e);
        }
    }

}
