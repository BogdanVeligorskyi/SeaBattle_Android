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
    public static final int ACTION_YOUR_MOVE = 2;
    public static final int ACTION_OPPONENT_MOVE = 3;
    public static final int ACTION_SET_OPPONENT_MOVE = 4;

    public static final String CHECK_CONNECTION = "CHECK_CONNECTION";

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Context context;
    private final int action;
    public static boolean IS_SUCCESS = false;

    public GameClient(Context context, int action) {
        this.context = context;
        this.action = action;
        IS_SUCCESS = false;
    }

    // try to connect to server
    private void startConnection() throws IOException {
        Settings settings = FileProcessing.loadSettings(context);
        String ip = settings.getHostIPAddress();
        int port = 50028;
        if (ip != null) {
            ip = "192.168.0.101";
        }
        //Log.d("SERVER_IP", ip);
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
                String res = sendMessage("CHECK_CONNECTION");
                Log.d("CLIENT:", ""+res);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (action == ACTION_YOUR_MOVE) {
            try {
                startConnection();
                //String res = sendMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
