package com.digitalartists.seabattle.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.digitalartists.seabattle.R;
import com.digitalartists.seabattle.model.FileProcessing;
import com.digitalartists.seabattle.model.Settings;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

// Play Activity class
public class PlayActivity extends AppCompatActivity {

    private Settings settings;          // settings object
    private TimerTask timerTask;        // timer runner
    private int secondsAfterStart;      // time (in seconds) after start
    private int numOfMines;             // number of mines
    private int minesCounter;           // mines left

    private int[] mines_arr = null;     // array of mines
    private int[] visited_arr = null;   // cols*rows array of each button id

    private TextView tvTime;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_play);

        if (savedInstanceState != null) {
            settings = savedInstanceState.getParcelable(MainActivity.SETTINGS);
        } else {
            try {
                settings = FileProcessing.loadSettings(getApplicationContext());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        TextView tvMinesLeft = findViewById(R.id.minesLeft_id);
        tvTime = findViewById(R.id.time_id);

        tvMinesLeft.setText(String.format("Mines Left: %d", settings.getMinesNum()));

        numOfMines = settings.getMinesNum();
        minesCounter = numOfMines;

        mines_arr = generateMines(settings.getCols() * settings.getRows());
        visited_arr = new int[settings.getCols() * settings.getRows()];

        TableLayout tableLayout;
        ImageView imageButton;

        // create time counter
        secondsAfterStart = 0;
        Timer timer = new Timer();
        timerTask = new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(() -> {
                    secondsAfterStart++;
                    String strTime = "Time: ";
                    int minutes = secondsAfterStart / 60;
                    int seconds = secondsAfterStart % 60;

                    if (minutes < 10) {
                        strTime += "0";
                    }
                    strTime += minutes;

                    strTime += ":";

                    if (seconds < 10) {
                        strTime += "0";
                    }
                    strTime += seconds;

                    tvTime.setText(strTime);

                });

            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);

        // create ImageView components (buttons) for game board
        for (int i = 0; i < settings.getRows(); i++) {
            tableLayout = findViewById(R.id.buttonsPanel_id);
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tableRow.setGravity(Gravity.CENTER);
            for (int j = 0; j < settings.getCols(); j++) {
                imageButton = new ImageView(this);
                imageButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                imageButton.setId(i * settings.getCols() + j);
                visited_arr[i * settings.getCols() + j] = 0;
                //imageButton.setImageResource(R.drawable.non_clicked_cell);
                imageButton.setScaleType(ImageView.ScaleType.FIT_CENTER);

                // handler for CLICK on this image (button)
                imageButton.setOnClickListener(v -> {
                    ImageView iView = (ImageView) v;

                    if (visited_arr[iView.getId()] == 2 || visited_arr[iView.getId()] == 1) {
                        return;
                    }

                    // check if clicked button contains mine and
                    // stop the game if it`s true
                    if (checkIfMine(iView.getId())) {
                        timerTask.cancel();
                        setIconToButton(iView, -3);
                        AlertDialog.Builder dialog =
                                new AlertDialog.Builder(this);
                        dialog.setCancelable(false);
                        dialog.setIcon(R.mipmap.ic_launcher_round);
                        dialog.setTitle("Game over");
                        dialog.setMessage("Unfortunately, you`ve lost the game!");
                        dialog.setPositiveButton("Ok", ((dialogInterface, m) -> finish()));
                        dialog.create();
                        dialog.show();
                        secondsAfterStart = 0;

                    } else {
                        checkNeighbourCells(iView.getId());
                    }

                    if (checkIfEnd()) {
                        timerTask.cancel();
                        AlertDialog.Builder dialog =
                                new AlertDialog.Builder(this);
                        dialog.setCancelable(false);
                        dialog.setIcon(R.mipmap.ic_launcher_round);
                        dialog.setTitle("Game over");
                        dialog.setMessage("Congratulations, you`ve won the game!");
                        dialog.setPositiveButton("Ok", ((dialogInterface, m) -> finish()));
                        dialog.create();
                        dialog.show();

                        secondsAfterStart = 0;
                        //addResultToFile(getApplicationContext());
                    }
                });

                // handler for LONG CLICK on this image (button)
                imageButton.setOnLongClickListener(v -> {
                    ImageView iView = (ImageView) v;
                    iView.setClickable(false);

                    // if cell hasn`t been clicked or visited
                    if (visited_arr[iView.getId()] != 2 && visited_arr[iView.getId()] != 1 && minesCounter > 0) {
                        setIconToButton(iView, -1);
                        minesCounter--;
                        visited_arr[iView.getId()] = 2;

                        // if cell has been already long-clicked - return one mine
                    } else if (visited_arr[iView.getId()] == 2) {
                        setIconToButton(iView, -2);
                        minesCounter++;
                        visited_arr[iView.getId()] = 0;
                    }

                    // change label text according to number of mines left
                    tvMinesLeft.setText(String.format("Mines Left: %d", minesCounter));

                    if (checkIfEnd()) {
                        timerTask.cancel();

                        AlertDialog.Builder dialog =
                                new AlertDialog.Builder(this);
                        dialog.setCancelable(false);
                        dialog.setIcon(R.mipmap.ic_launcher_round);
                        dialog.setTitle("Game over");
                        dialog.setMessage("Congratulations, you`ve won the game!");
                        dialog.setPositiveButton("Ok", ((dialogInterface, m) -> finish()));
                        dialog.create();
                        dialog.show();
                        //addResultToFile(getApplicationContext());

                        secondsAfterStart = 0;

                    }

                    return true;
                });

                tableRow.addView(imageButton);
            }
            tableLayout.addView(tableRow, new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
        }

    }

    // generate mines
    private int[] generateMines(int square) {
        mines_arr = new int[numOfMines];
        int min = 1;
        int max = square - 1;
        for (int i = 0; i < numOfMines; i++) {
            boolean isUnique = false;
            while (!isUnique) {
                int randNum = (int) (Math.random() * (max - min + 1) + min);
                for (int k = 0; k <= i; k++) {
                    if (mines_arr[k] == randNum) {
                        isUnique = false;
                        break;
                    }
                    if (k == i) {
                        mines_arr[i] = randNum;
                        isUnique = true;
                    }
                }
            }
        }
        for (int j = 0; j < this.numOfMines; j++) {
            Log.d("CREATED_ARR", "" + mines_arr[j]);
        }
        return mines_arr;
    }


    // check if clicked button contains a mine
    boolean checkIfMine(int buttonNumInt) {
        for (int i = 0; i < numOfMines; i++) {
            if (buttonNumInt == mines_arr[i]) {
                return true;
            }
        }
        return false;
    }


    // draw route until cell with mine as neighbour is found
    void checkNeighbourCells(int butNum) {

        Queue <Integer> queue = new LinkedList<>();
        queue.add(butNum);
        visited_arr[butNum] = 1;

        int rows = settings.getRows();
        int cols = settings.getCols();
        int []arr_of_neighbours = new int[8];

        // draw route to expand all needed cells
        while (queue.size() > 0) {

            butNum = queue.element();
            arr_of_neighbours[0] = butNum - cols;
            arr_of_neighbours[1] = butNum - cols + 1;
            arr_of_neighbours[2] = butNum + 1;
            arr_of_neighbours[3] = butNum + cols + 1;
            arr_of_neighbours[4] = butNum + cols;
            arr_of_neighbours[5] = butNum + cols - 1;
            arr_of_neighbours[6] = butNum - 1;
            arr_of_neighbours[7] = butNum - cols - 1;
            queue.remove();

            for (int i = 0; i < 8; i++) {

                int res = checkNeighbourCell(butNum, arr_of_neighbours[i]);
                if (res == 0 &&
                        visited_arr[arr_of_neighbours[i]] == 0) {
                    queue.add(arr_of_neighbours[i]);
                    visited_arr[arr_of_neighbours[i]] = 1;
                } else if (res == 1 && visited_arr[arr_of_neighbours[i]] == 0) {
                    visited_arr[arr_of_neighbours[i]] = 1;
                }
            }

        }

        int minesInNeighbourCells = 0;

        // count mines in neighbour cells
        for (int j = 0; j < rows*cols; j++) {
            if (visited_arr[j] == 1) {
                if (checkForMinesCount(j - cols, j - cols) == 1) {
                    minesInNeighbourCells++;
                }
                if (((j % cols) != (cols-1)) && checkForMinesCount(j - cols + 1, j - cols + 1) == 1) {
                    minesInNeighbourCells++;
                }
                if (((j % cols) != (cols - 1)) && checkForMinesCount(j + 1, j + 1) == 1) {
                    minesInNeighbourCells++;
                }
                if (((j % cols) != (cols - 1)) && checkForMinesCount(j + cols + 1, j + cols + 1) == 1) {
                    minesInNeighbourCells++;
                }
                if (checkForMinesCount(j + cols, j + cols) == 1) {
                    minesInNeighbourCells++;
                }
                if (((j % cols) != 0) && checkForMinesCount(j + cols - 1, j + cols - 1) == 1) {
                    minesInNeighbourCells++;
                }
                if (((j % cols) != 0) && checkForMinesCount(j - 1, j - 1) == 1) {
                    minesInNeighbourCells++;
                }
                if (((j % cols) != 0) && checkForMinesCount(j - cols - 1, j - cols - 1) == 1) {
                    minesInNeighbourCells++;
                }
                ImageView imageView = findViewById(j);
                setIconToButton(imageView, minesInNeighbourCells);
            }
            minesInNeighbourCells = 0;
        }
    }

    // set icon to button in order to show additional information needed for user
    void setIconToButton(ImageView imageView, int minesNum) {

        if (imageView == null) {
            Log.d("setIconToButton", "Button was not found!");
            return;
        }

        /*if (minesNum == 0) {
            imageView.setImageResource(R.drawable.empty_cell);
        } else if (minesNum == 1) {
            imageView.setImageResource(R.drawable.digit_1);
        } else if (minesNum == 2) {
            imageView.setImageResource(R.drawable.digit_2);
        } else if (minesNum == 3) {
            imageView.setImageResource(R.drawable.digit_3);
        } else if (minesNum == 4) {
            imageView.setImageResource(R.drawable.digit_4);
        } else if (minesNum == 5) {
            imageView.setImageResource(R.drawable.digit_5);
        } else if (minesNum == 6) {
            imageView.setImageResource(R.drawable.digit_6);
        } else if (minesNum == 7) {
            imageView.setImageResource(R.drawable.digit_7);
        } else if (minesNum == 8) {
            imageView.setImageResource(R.drawable.digit_8);
        } else if (minesNum == -1) {
            imageView.setImageResource(R.drawable.flag);
        } else if (minesNum == -2) {
            imageView.setImageResource(R.drawable.non_clicked_cell);
        } else if(minesNum == -3) {
            imageView.setImageResource(R.drawable.mine_clicked);
        }*/

    }


    // check neighbour cell
    int checkNeighbourCell(int firstParam, int butNum) {

        if (!checkIfValidCoord(firstParam, butNum)) {
            return 10;
        }

        for (int i = 0; i < numOfMines; i++) {
            if (mines_arr[i] == butNum) {
                return 2;
            }
        }

        int cols = settings.getCols();

        int []arr = new int[8];
        arr[0] = butNum - cols;
        arr[1] = butNum - cols + 1;
        arr[2] = butNum + 1;
        arr[3] = butNum + cols + 1;
        arr[4] = butNum + cols;
        arr[5] = butNum + cols - 1;
        arr[6] = butNum - 1;
        arr[7] = butNum - cols - 1;

        for (int i = 0; i < numOfMines; i++) {
            for (int j = 0; j < 8; j++) {
                if (checkIfValidCoord(butNum, arr[j]) && mines_arr[i] == arr[j]) {
                    return 1;
                }
            }
        }
        return 0;
    }


    // count number of mines in neighbour cells
    int checkForMinesCount(int firstParam, int num) {
        if (!checkIfValidCoord(firstParam, num)) {
            return 10;
        }
        for (int i = 0; i < numOfMines; i++) {
            if (mines_arr[i] == num) {
                return 1;
            }
        }
        return 0;
    }


    // check if coordinates of button are valid
    boolean checkIfValidCoord(int firstParam, int num) {

        // transform num into x and y coordinates
        int x = num % settings.getCols();
        int y = num / settings.getCols();
        int x_prev = firstParam % settings.getCols();

        // check if cell is out of range
        return x >= 0 && y >= 0 && x < settings.getCols() && y < settings.getRows() &&
                (x != 0 || x_prev != (settings.getCols() - 1)) && (x != (settings.getCols() - 1) || x_prev != 0);
    }


    // check if all cells were visited
    boolean checkIfEnd() {
        for (int i = 0; i < settings.getRows()*settings.getCols(); i++) {
            if (visited_arr[i] == 0) {
                return false;
            }
        }
        return true;
    }

}
