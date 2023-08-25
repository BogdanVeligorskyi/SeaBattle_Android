package com.digitalartists.seabattle.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
    private Thread threadGs;
    private ImageView imageView;
    private TextView textViewYourBoard;
    private TextView textViewOpponentBoard;

    private static final List<ImageView> imageViewList = new ArrayList<>();

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

    // start initialization
    private void startInit(Bundle savedInstanceState, Context context) {
        textViewMove = findViewById(R.id.move_id);
        textViewOpponentBoard = findViewById(R.id.opponentBoard_id);
        textViewYourBoard = findViewById(R.id.yourBoard_id);

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


    @SuppressLint("DefaultLocale")
    private void setTextViewYourBoard() {
        textViewYourBoard.setText(String.format("Your board (Ruins left: %d)", numOfYourRuinsLeft));
    }


    @SuppressLint("DefaultLocale")
    private void setTextViewOpponentBoard() {
        textViewOpponentBoard.setText(String.format("Opponent board (Ruins left: %d)", numOfOpponentRuinsLeft));
    }


    // initialize graphical boards
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
                    textView.setLayoutParams((new TableRow.LayoutParams(60,
                            60)));
                    textView.setPadding(5, 5, 5, 5);
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
                    textView.setPadding(5, 5, 5, 5);
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
                    textView.setLayoutParams((new TableRow.LayoutParams(60,
                            60)));
                    textView.setPadding(5, 5, 5, 5);
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

                    // if you clicked already visited cell
                    if (visited_opponent_arr[id] != 0) {
                        Toast.makeText(getApplicationContext(), "You`ve already clicked this cell!", Toast.LENGTH_SHORT).show();
                        return;
                    }

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
                        setResultForOpponent(iView, Integer.parseInt(answer));
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

                });

                tableRow.addView(imageButton);
            }
            tableLayout.addView(tableRow, new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
        }
    }


    // start server or client
    private void runGame() {
        if (settings.getRole().startsWith("HOST")) {
            runAsServer();
        } else {
            runAsClient();
        }
    }


    // start server
    private void runAsServer() {
        gs = new GameServer(handler);
        threadGs = new Thread(gs);
        threadGs.start();
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        isYourMove = false;
        setTextViewYourBoard();
        setTextViewOpponentBoard();
        setTextViewMove();

    }


    // start client
    private void runAsClient() {
        new Thread(new GameClient(getApplicationContext(), 1, "", handler)).start();
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        isYourMove = true;
        setTextViewYourBoard();
        setTextViewOpponentBoard();
        setTextViewMove();
    }


    // set (or change) text referenced to your or opponent move
    @SuppressLint("SetTextI18n")
    private void setTextViewMove() {
        if (isYourMove) {
            textViewMove.setText("Your move");
            textViewMove.setTextColor(getResources().getColor(R.color.green));
        } else {
            textViewMove.setText("Opponent move");
            textViewMove.setTextColor(getResources().getColor(R.color.red));
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


    // set result for opponent board after user move
    private void setResultForOpponent(ImageView imageView, int result) {
        if (imageView == null) {
            Log.d("setIconToButton", "Button was not found!");
            return;
        }

        if (result == 1) {
            imageView.setImageResource(R.drawable.digit_1_ruin);
            numOfOpponentRuinsLeft--;
        } else if (result == 2) {
            imageView.setImageResource(R.drawable.digit_2_ruin);
            numOfOpponentRuinsLeft--;
        } else if (result == 3) {
            imageView.setImageResource(R.drawable.digit_3_ruin);
            numOfOpponentRuinsLeft--;
        } else if (result == 5 || result == 0) {
            imageView.setImageResource(R.drawable.non_clicked_cell_ruin);
        } else if (result == 10) {
            imageView.setImageResource(R.drawable.mine_clicked);
        }
        int id = imageView.getId();
        visited_opponent_arr[id - 100] = 1;

        setTextViewOpponentBoard();

        // show dialog for winner
        if (numOfOpponentRuinsLeft == 0) {
            if (gs != null) {
                threadGs.interrupt();
            }
            AlertDialog.Builder dialog =
                    new AlertDialog.Builder(this);
            dialog.setCancelable(false);
            dialog.setIcon(R.mipmap.ic_launcher_round);
            dialog.setTitle("Game over");
            dialog.setMessage("Congratulations, you`ve won the game!");
            dialog.setPositiveButton("Ok", ((dialogInterface, m) -> finish()));
            dialog.create();
            dialog.show();
        }

    }

    // set result for user board after opponent move
    public void setResultForYour(int cellNum, int result) {
        ImageView imageView = imageViewList.get(cellNum);
        if (imageView == null) {
            Log.d("setIconToButton", "Button was not found!");
            return;
        }

        if (result == 1) {
            imageView.setImageResource(R.drawable.digit_1_ruin);
            numOfYourRuinsLeft--;
        } else if (result == 2) {
            imageView.setImageResource(R.drawable.digit_2_ruin);
            numOfYourRuinsLeft--;
        } else if (result == 3) {
            imageView.setImageResource(R.drawable.digit_3_ruin);
            numOfYourRuinsLeft--;
        } else if (result == 5 || result == 0) {
            imageView.setImageResource(R.drawable.non_clicked_cell_ruin);
        } else if (result == 10) {
            imageView.setImageResource(R.drawable.mine_clicked);
        }

        setTextViewYourBoard();

        // show dialog for loser
        if (numOfYourRuinsLeft == 0) {
            if (gs != null) {
                threadGs.interrupt();
            }
            AlertDialog.Builder dialog =
                    new AlertDialog.Builder(this);
            dialog.setCancelable(false);
            dialog.setIcon(R.mipmap.ic_launcher_round);
            dialog.setTitle("Game over");
            dialog.setMessage("Unfortunately, you`ve lost the game!");
            dialog.setPositiveButton("Ok", ((dialogInterface, m) -> finish()));
            dialog.create();
            dialog.show();
        }

    }


    // handler for CLIENT-ACTIVITY and SERVER-ACTIVITY interaction
    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == GameClient.ACTION_CHECK_CONNECTION && settings.getRole().startsWith("HOST")) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Connection with GUEST has been established", Toast.LENGTH_LONG).show();
            } else if (msg.what == GameClient.ACTION_CHECK_CONNECTION && settings.getRole().startsWith("GUEST")) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Connection with HOST has been established!", Toast.LENGTH_LONG).show();
            } else if (msg.what == GameClient.ACTION_CLIENT_MOVE_1) {
                setResultForYour(msg.arg1, msg.arg2);
            } else if (msg.what == GameClient.ACTION_SET_SERVER_MOVE) {
                setMoveForServer();
                setTextViewMove();
            } else if (msg.what == GameClient.ACTION_SERVER_MOVE) {
                int num = checkCellForClient(msg.arg1);
                setResultForYour(msg.arg1, num);
                GameClient gameClient = new GameClient(getApplicationContext(), 6, ""+ msg.arg1 + ":" + num, null);
                new Thread(gameClient).start();
                String answer = " ";
                while (answer.startsWith(" ")) {
                    answer = gameClient.getAnswer();
                }
                if (checkIfRuined(num)) {
                    gameClient = new GameClient(getApplicationContext(), 5, "", handler);
                    new Thread(gameClient).start();
                } else {
                    isYourMove = true;
                    setTextViewMove();
                    gameClient = new GameClient(getApplicationContext(), 7, "", null);
                    new Thread(gameClient).start();
                }

            } else if (msg.what == GameClient.ACTION_SEND_RESPONSE_TO_SERVER) {
                setResultForOpponent(imageView, msg.arg2);
            } else if (msg.what == GameClient.ACTION_SET_CLIENT_MOVE) {
                isYourMove = false;
                setTextViewMove();
            }

            super.handleMessage(msg);
        }
    };

}

