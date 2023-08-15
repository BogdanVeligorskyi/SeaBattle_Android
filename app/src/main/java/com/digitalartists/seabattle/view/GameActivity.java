package com.digitalartists.seabattle.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.digitalartists.seabattle.R;
import com.digitalartists.seabattle.model.FileProcessing;
import com.digitalartists.seabattle.model.GameClient;
import com.digitalartists.seabattle.model.GameServer;
import com.digitalartists.seabattle.model.Settings;

import java.io.IOException;

// Game Activity class
public class GameActivity extends AppCompatActivity {

    private int[] visited_your_arr = null;   // cols*rows array of each button id
    private int[] visited_opponent_arr = null;
    private Settings settings;
    private boolean isYourMove;

    private int numOfYourRuinsLeft;
    private int numOfOpponentRuinsLeft;

    @SuppressLint({"DefaultLocale", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_game);

        Context context = getApplicationContext();

        startInit(savedInstanceState, context);

        createButtonsForBoards();

        runGame();

    }

    // set icon to button in order to show additional information needed for user
    private void setIconToButton(ImageView imageView, int type) {

        if (imageView == null) {
            Log.d("setIconToButton", "Button was not found!");
            return;
        }

        if (type == 1) {
            imageView.setImageResource(R.drawable.digit_1);
        } else if (type == 2) {
            imageView.setImageResource(R.drawable.digit_2);
        } else if (type == 3) {
            imageView.setImageResource(R.drawable.digit_3);
        } else if (type == 5 || type == 0) {
            imageView.setImageResource(R.drawable.non_clicked_cell);
        } else if (type == 10) {
            imageView.setImageResource(R.drawable.mine_usual);
        }

    }


    private void startInit(Bundle savedInstanceState, Context context) {
        visited_your_arr = getIntent().getIntArrayExtra(PlayActivity.VISITED_ARR);

        numOfYourRuinsLeft = 16;
        numOfOpponentRuinsLeft = 16;

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


    private void createButtonsForBoards() {
        TableLayout tableLayout;
        ImageView imageButton;
        String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        String[] numbers = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

        visited_opponent_arr = new int[10*10];

        // create ImageView components (buttons) for game board
        for (int i = 0; i < 11; i++) {
            if (i == 0) {
                tableLayout = findViewById(R.id.yourButtonsPanel_id);
                TableRow tableRow = new TableRow(this);
                tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tableRow.setGravity(Gravity.CENTER);
                for (int k = 0; k < 11; k++) {
                    TextView textView = new TextView(this);
                    textView.setLayoutParams((new TableRow.LayoutParams(30,
                            30)));
                    if (k == 0) {
                        textView.setText(" ");
                    } else {
                        textView.setText(numbers[k-1]);
                        textView.setGravity(Gravity.CENTER);
                    }
                    tableRow.addView(textView);
                }
                tableLayout.addView(tableRow, new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                continue;
            }
            tableLayout = findViewById(R.id.yourButtonsPanel_id);
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tableRow.setGravity(Gravity.CENTER);
            for (int j = 0; j < 11; j++) {
                if (j == 0) {
                    TextView textView = new TextView(this);
                    textView.setLayoutParams((new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT)));
                    textView.setText(letters[i-1]);
                    tableRow.addView(textView);
                    continue;
                }
                imageButton = new ImageView(this);
                imageButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                imageButton.setId((i-1) * 10 + (j-1));
                setIconToButton(imageButton, visited_your_arr[(i-1) * 10 + (j-1)]);
                imageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);

                // handler for CLICK on this image (button)
                imageButton.setOnClickListener(v -> {
                    ImageView iView = (ImageView) v;
                    Toast.makeText(this, "Clicked button in 1st board", Toast.LENGTH_LONG).show();
                });

                tableRow.addView(imageButton);
            }
            tableLayout.addView(tableRow, new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
        }

        for (int i = 0; i < 11; i++) {
            if (i == 0) {
                tableLayout = findViewById(R.id.opponentButtonsPanel_id);
                TableRow tableRow = new TableRow(this);
                tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tableRow.setGravity(Gravity.CENTER);
                for (int k = 0; k < 11; k++) {
                    TextView textView = new TextView(this);
                    textView.setLayoutParams((new TableRow.LayoutParams(30,
                            30)));
                    if (k == 0) {
                        textView.setText(" ");
                    } else {
                        textView.setText(numbers[k-1]);
                        textView.setGravity(Gravity.CENTER);
                    }
                    tableRow.addView(textView);
                }
                tableLayout.addView(tableRow, new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                continue;
            }
            tableLayout = findViewById(R.id.opponentButtonsPanel_id);
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tableRow.setGravity(Gravity.CENTER);
            for (int j = 0; j < 11; j++) {
                if (j == 0) {
                    TextView textView = new TextView(this);
                    textView.setLayoutParams((new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT)));
                    textView.setText(letters[i-1]);
                    tableRow.addView(textView);
                    continue;
                }
                imageButton = new ImageView(this);
                imageButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                imageButton.setId((i-1) * 10 + (j-1) + 100);
                visited_opponent_arr[(i-1) * 10 + (j-1)] = 0;
                imageButton.setImageResource(R.drawable.non_clicked_cell);
                imageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);

                imageButton.setEnabled(false);

                // handler for CLICK on this image (button)
                imageButton.setOnClickListener(v -> {
                    ImageView iView = (ImageView) v;
                    Toast.makeText(this, "Clicked button in 2nd board", Toast.LENGTH_LONG).show();
                });

                tableRow.addView(imageButton);
            }
            tableLayout.addView(tableRow, new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
        }
    }

    private void runGame() {
        if (settings.getRole().startsWith("HOST")) {
            new Thread(new GameServer(getApplicationContext())).start();
            runAsServer();
        } else {
            runAsClient();
        }
    }

    private void runAsServer() {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        while (!GameClient.IS_SUCCESS) {
        }
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void runAsClient() {
        new Thread(new GameClient(getApplicationContext(), 1)).start();
    }

}

