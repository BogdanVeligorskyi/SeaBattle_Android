package com.digitalartists.seabattle.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import java.util.ArrayList;
import java.util.List;

// Game Activity class
public class GameActivity extends AppCompatActivity {

    private static int[] visited_your_arr = null;   // cols*rows array of each button id
    private int[] visited_opponent_arr = null;
    private Settings settings;
    private static boolean isYourMove;
    private TextView textViewMove;

    private ProgressBar progressBar;
    private GameServer gs;
    private ImageView imageView;

    private static List<ImageView> imageViewList = new ArrayList<ImageView>();

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
        textViewMove = findViewById(R.id.move_id);
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
                imageButton.setTag(String.valueOf((i-1) * 10 + (j-1)));
                setIconToButton(imageButton, visited_your_arr[(i-1) * 10 + (j-1)]);
                imageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);

                tableRow.addView(imageButton);
                imageViewList.add(imageButton);
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

                // handler for CLICK on this image (button)
                imageButton.setOnClickListener(v -> {

                    // if attempted to click cell while it`s not user`s move
                    if (!isYourMove) {
                        Toast.makeText(getApplicationContext(), "It`s not your move!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ImageView iView = (ImageView) v;
                    int id = iView.getId();
                    id -= 100;

                    // send value to server
                    if (!settings.getRole().startsWith("HOST")) {
                        GameClient gc = new GameClient(getApplicationContext(), 2, String.valueOf(id), null);
                        new Thread(gc).start();
                        Log.d("Answer is ", ""+gc.getAnswer());
                        String answer = " ";
                        while (answer.startsWith(" ")) {
                            answer = gc.getAnswer();
                        }
                        imageView = iView;
                        setResultForClient(iView, Integer.parseInt(answer));
                        isYourMove = checkIfRuined(Integer.parseInt(answer));
                        setTextViewMove();
                        if (!isYourMove) {
                            gc = new GameClient(getApplicationContext(), 4, "", null);
                            new Thread (gc).start();
                            answer = " ";
                            while (answer.startsWith(" ")) {
                                answer = gc.getAnswer();
                            }
                            gc = new GameClient(getApplicationContext(), 5, "", handler);
                            new Thread (gc).start();
                        }
                    } else {
                        imageView = iView;
                        GameServer.move = GameClient.SERVER_MOVE + id;
                        Log.d("ACTIVITY", GameServer.move);
                    }

                    //Toast.makeText(this, "Clicked button in 2nd board", Toast.LENGTH_LONG).show();
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
            runAsServer();
        } else {
            runAsClient();
        }
    }

    private void runAsServer() {
        Log.d("HERE", "sssss");
        gs = new GameServer(getApplicationContext(), handler);
        new Thread(gs).start();
        progressBar = findViewById(R.id.progressBar);
        if (gs == null) {
            Log.d("HERE", "null");
        }
        Log.d("ANSWER", gs.getAnswer());
        progressBar.setVisibility(View.VISIBLE);
        isYourMove = false;
        Log.d("HERE", "sssss");
        setTextViewMove();

    }

    private void runAsClient() {
        new Thread(new GameClient(getApplicationContext(), 1, "", null)).start();
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        isYourMove = true;
        setTextViewMove();
    }


    private void setTextViewMove() {
        if (isYourMove) {
            textViewMove.setText("Your move");
        } else {
            textViewMove.setText("Opponent move");
        }
    }

    public static void setMoveForServer() {
        isYourMove = true;
    }

    public static int checkCellForServer(int cellNum) {
        return visited_your_arr[cellNum];
    }

    private int checkCellForClient(int cellNum) {
        return visited_your_arr[cellNum];
    }


    private boolean checkIfRuined(int result) {
        return result == 1 || result == 2 || result == 3;
    }


    private void setResultForClient(ImageView imageView, int result) {
        if (imageView == null) {
            Log.d("setIconToButton", "Button was not found!");
            return;
        }

        if (result == 1) {
            imageView.setImageResource(R.drawable.digit_1_ruin);
        } else if (result == 2) {
            imageView.setImageResource(R.drawable.digit_2_ruin);
        } else if (result == 3) {
            imageView.setImageResource(R.drawable.digit_3_ruin);
        } else if (result == 5 || result == 0) {
            imageView.setImageResource(R.drawable.non_clicked_cell_ruin);
        } else if (result == 10) {
            imageView.setImageResource(R.drawable.mine_clicked);
        }
    }

    public static void setResultForServer(int cellNum, int result) {
        ImageView imageView = imageViewList.get(cellNum);
        if (imageView == null) {
            Log.d("setIconToButton", "Button was not found!");
            return;
        }

        if (result == 1) {
            imageView.setImageResource(R.drawable.digit_1_ruin);
        } else if (result == 2) {
            imageView.setImageResource(R.drawable.digit_2_ruin);
        } else if (result == 3) {
            imageView.setImageResource(R.drawable.digit_3_ruin);
        } else if (result == 5 || result == 0) {
            imageView.setImageResource(R.drawable.non_clicked_cell_ruin);
        } else if (result == 10) {
            imageView.setImageResource(R.drawable.mine_clicked);
        }
    }

    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == GameClient.ACTION_CHECK_CONNECTION){
                progressBar.setVisibility(View.INVISIBLE);
            } else if (msg.what == GameClient.ACTION_CLIENT_MOVE_1) {
                setResultForServer(msg.arg1, msg.arg2);
            } else if (msg.what == GameClient.ACTION_SET_SERVER_MOVE) {
                setMoveForServer();
                setTextViewMove();
            } else if (msg.what == GameClient.ACTION_SERVER_MOVE) {
                int num = checkCellForClient(msg.arg1);
                Toast.makeText(getApplicationContext(), "jjjjjjjj", Toast.LENGTH_SHORT).show();
                setResultForServer(msg.arg1, num);
                GameClient gameClient = new GameClient(getApplicationContext(), 6, ""+ msg.arg1 + ":" + num, null);
                new Thread(gameClient).start();
                String answer = " ";
                while (answer.startsWith(" ")) {
                    Log.d("CLIENT", "inwhile");
                    answer = gameClient.getAnswer();
                }
                if (checkIfRuined(num)) {
                    gameClient = new GameClient(getApplicationContext(), 5, "", handler);
                    new Thread(gameClient).start();
                }
            } else if (msg.what == GameClient.ACTION_SEND_RESPONSE_TO_SERVER) {
                setResultForClient(imageView, msg.arg2);
            }

            super.handleMessage(msg);
        }
    };

}

