package com.digitalartists.seabattle.model;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.digitalartists.seabattle.view.GameActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer implements Runnable {

    private ServerSocket serverSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Context context;
    private Handler handler;

    private volatile String answer;

    public GameServer(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
        this.answer = "";
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(12003);
            Log.d("SERVER", "Started listening for clients...");
            while (true) {
                Socket socket = serverSocket.accept();
                Log.d("SERVER", "client connected");
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                String str = in.readLine();
                if (str.startsWith(GameClient.CHECK_CONNECTION)) {
                    Message msg = handler.obtainMessage();
                    msg.what = GameClient.ACTION_CHECK_CONNECTION;
                    handler.sendMessage(msg);
                    Log.d("SERVER", "client successfully connected!");
                    out.println("CONNECTED");
                }

                if (str.startsWith(GameClient.CLIENT_MOVE)) {
                    Log.d("SERVER", "received cell from client");
                    String[] strNum = str.split(":");
                    answer = String.valueOf(GameActivity.checkCellForServer(Integer.parseInt(strNum[1])));
                    Message msg = handler.obtainMessage();
                    msg.what = GameClient.ACTION_CLIENT_MOVE_1;
                    msg.arg1 = Integer.parseInt(strNum[1]);
                    msg.arg2 = Integer.parseInt(answer);
                    handler.sendMessage(msg);
                    //GameActivity.setResultForServer(Integer.parseInt(strNum[1]), Integer.parseInt(answer));
                    out.println("SERVER_ANSWERED:" + answer);
                }

                if (str.startsWith(GameClient.SET_OPPONENT_MOVE)) {
                    Log.d("SERVER", "set server move");
                    answer = "Move set to SERVER";
                    Message msg = handler.obtainMessage();
                    msg.what = GameClient.ACTION_SET_OPPONENT_MOVE;
                    handler.sendMessage(msg);
                    GameActivity.setMoveForServer();
                    out.println("SERVER_ANSWERED:" + answer);
                }

                //Log.d("response", str);

                in.close();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getAnswer() {
        return answer;
    };
}
