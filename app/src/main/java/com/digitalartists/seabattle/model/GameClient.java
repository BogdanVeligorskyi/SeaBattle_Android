package com.digitalartists.seabattle.model;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameClient implements Runnable {

    public static final int ACTION_CHECK_CONNECTION = 1;
    public static final int ACTION_CLIENT_MOVE_1 = 2;
    public static final int ACTION_CLIENT_MOVE_2 = 3;
    public static final int ACTION_SET_SERVER_MOVE = 4;
    public static final int ACTION_SERVER_MOVE = 5;
    public static final int ACTION_SEND_RESPONSE_TO_SERVER = 6;

    public static final String CHECK_CONNECTION = "CHECK_CONNECTION:";
    public static final String CLIENT_MOVE = "CLIENT_MOVE:";
    public static final String SET_SERVER_MOVE = "SET_SERVER_MOVE:";
    public static final String SERVER_MOVE = "SERVER_MOVE:";
    public static final String SEND_RESPONSE_TO_SERVER = "SEND_RESPONSE_TO_SERVER:";

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Context context;
    private String message;

    private volatile String answer;
    private Handler handler;

    private final int action;

    public GameClient(Context context, int action, String message, Handler handler) {
        this.context = context;
        this.action = action;
        this.message = message;
        this.answer = " ";
        this.handler = handler;
    }

    // try to connect to server
    private void startConnection() throws IOException {
        Settings settings = FileProcessing.loadSettings(context);
        String ip = settings.getHostIPAddress();
        int port = 12003;
        if (ip == null) {
            ip = "192.168.0.101";
        }
        Log.d("SERVER_IP", ip);
        //Log.d("PORT", ""+port);
        while (clientSocket == null) {
            clientSocket = new Socket(ip, port);
        }
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader
                (clientSocket.getInputStream()));
    }

    // send message to server and retrieve answer
    private String sendMessage(String msg) throws IOException {
        out.println(msg);
        return in.readLine();
    }

    // disconnect from server
    private void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    @Override
    public void run() {
        if (action == ACTION_CHECK_CONNECTION) {
            try {
                startConnection();
                String res = sendMessage("CHECK_CONNECTION:");
                Log.d("CLIENT:", "CHECK_CONNECTION");
                stopConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (action == ACTION_CLIENT_MOVE_1 || action == ACTION_CLIENT_MOVE_2) {
            try {
                startConnection();
                String res = sendMessage(GameClient.CLIENT_MOVE + message);
                if (res != null) {
                    Log.d("CLIENT", "ACTION_CLIENT_MOVE");
                    String[] resArr = res.split(":");
                    answer = resArr[1];
                    stopConnection();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (action == ACTION_SET_SERVER_MOVE) {
            try {
                startConnection();
                String res = sendMessage(GameClient.SET_SERVER_MOVE);
                Log.d("CLIENT", "SET_SERVER_MOVE");
                stopConnection();
                if (res != null) {
                    answer = res;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (action == ACTION_SERVER_MOVE) {
            try {

                startConnection();
                String res = sendMessage(GameClient.SERVER_MOVE);

                if (res != null) {
                    Log.d("CLIENT", "SERVER_MOVE");
                    Log.d("CLIENT", "res="+res);
                    Message msg = handler.obtainMessage();
                    msg.what = GameClient.ACTION_SERVER_MOVE;
                    msg.arg1 = Integer.parseInt(res);
                    handler.sendMessage(msg);
                }

                stopConnection();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (action == ACTION_SEND_RESPONSE_TO_SERVER) {
            try {

                startConnection();
                Log.d("CLIENT", "SEND_RESPONSE_TO_SERVER");
                String res = sendMessage(GameClient.SEND_RESPONSE_TO_SERVER + message);
                stopConnection();
                answer = "stop";
                Log.d("CLIENT", res);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getAnswer() {
        return answer;
    }
}
