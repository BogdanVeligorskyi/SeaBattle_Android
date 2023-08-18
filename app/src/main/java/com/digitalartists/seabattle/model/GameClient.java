package com.digitalartists.seabattle.model;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameClient implements Runnable {

    public static final int ACTION_CHECK_CONNECTION = 1;
    public static final int ACTION_CLIENT_MOVE = 2;
    public static final int ACTION_SERVER_MOVE = 3;
    public static final int ACTION_SET_OPPONENT_MOVE = 4;

    public static final String CHECK_CONNECTION = "CHECK_CONNECTION:";
    public static final String CLIENT_MOVE = "CLIENT_MOVE:";
    public static final String SERVER_MOVE = "SERVER_MOVE:";
    public static final String SET_OPPONENT_MOVE = "SET_OPPONENT_MOVE:";

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Context context;
    private String message;

    private volatile String answer;

    private final int action;
    //public static boolean IS_SUCCESS = false;

    public GameClient(Context context, int action, String message) {
        this.context = context;
        this.action = action;
        this.message = message;
        this.answer = " ";
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
                Log.d("CLIENT:", ""+res);
                stopConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (action == ACTION_CLIENT_MOVE) {
            try {
                startConnection();
                String res = sendMessage(GameClient.CLIENT_MOVE + message);
                if (res != null) {
                    String[] resArr = res.split(":");
                    answer = resArr[1];
                    Log.d("ANSWER FROM SERVER", "" + answer);
                    stopConnection();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (action == ACTION_SET_OPPONENT_MOVE) {
            /*try {
                startConnection();
                String res = sendMessage("CHECK_CONNECTION");
                Log.d("CLIENT:", ""+res);
                stopConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
    }

    public String getAnswer() {
        return answer;
    }
}
