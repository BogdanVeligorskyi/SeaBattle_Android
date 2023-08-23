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

    public static String move;
    private volatile String answer;

    public GameServer(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
        this.answer = "";
        move = " ";
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

                Log.d("SERVER", "afterreadline " + str);

                // in order to receive query from server-side GameActivity
                while (str.startsWith(GameClient.SERVER_MOVE) && move.startsWith(" ")) {
                    //Log.d("SERVER", "while"+ str);
                }

                // check connection
                if (str.startsWith(GameClient.CHECK_CONNECTION)) {
                    Message msg = handler.obtainMessage();
                    msg.what = GameClient.ACTION_CHECK_CONNECTION;
                    handler.sendMessage(msg);
                    Log.d("SERVER", "client successfully connected!");
                    out.println("CONNECTED");
                    in.close();
                    socket.close();
                }

                // client move
                if (str.startsWith(GameClient.CLIENT_MOVE)) {
                    Log.d("SERVER", "received cell from client");
                    String[] strNum = str.split(":");
                    answer = String.valueOf(GameActivity.checkCellForServer(Integer.parseInt(strNum[1])));
                    Message msg = handler.obtainMessage();
                    msg.what = GameClient.ACTION_CLIENT_MOVE_1;
                    msg.arg1 = Integer.parseInt(strNum[1]);
                    msg.arg2 = Integer.parseInt(answer);
                    handler.sendMessage(msg);
                    out.println("SERVER_ANSWERED:" + answer);
                    in.close();
                    socket.close();
                }

                // set server move
                if (str.startsWith(GameClient.SET_SERVER_MOVE)) {
                    Log.d("SERVER", "SET_SERVER_MOVE");
                    answer = "Move set to SERVER";
                    Message msg = handler.obtainMessage();
                    msg.what = GameClient.ACTION_SET_SERVER_MOVE;
                    handler.sendMessage(msg);
                    GameActivity.setMoveForServer();
                    out.println("SERVER_ANSWERED:" + answer);
                    in.close();
                    socket.close();
                }

                // server move
                if (move.startsWith(GameClient.SERVER_MOVE)) {
                    Log.d("SERVER", "SERVER_MOVE");
                    String[] strNum = move.split(":");
                    out.println(strNum[1]);
                    move = " ";
                    str = "null";
                    Log.d("SERVER", ""+strNum[1]);
                    in.close();
                    socket.close();
                }

                // send response to server
                if (str.startsWith(GameClient.SEND_RESPONSE_TO_SERVER)) {
                    Log.d("SERVER", "SEND_RESPONSE_TO_SERVER");
                    String[] strNum = str.split(":");
                    Message msg = handler.obtainMessage();
                    msg.what = GameClient.ACTION_SEND_RESPONSE_TO_SERVER;
                    msg.arg1 = Integer.parseInt(strNum[1]);
                    msg.arg2 = Integer.parseInt(strNum[2]);
                    handler.sendMessage(msg);
                    Log.d("SERVER", ""+ strNum[2]);
                    out.println("SENT");
                    in.close();
                    socket.close();

                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getAnswer() {
        return answer;
    };

}
