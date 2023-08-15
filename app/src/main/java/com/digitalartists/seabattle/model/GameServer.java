package com.digitalartists.seabattle.model;

import android.content.Context;
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

    public GameServer(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(12003);
            Log.d("SERVER", "Started listening for clients...");
            while (true) {
                Socket socket = serverSocket.accept();
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));

                String str = in.readLine();
                if (str.startsWith(GameClient.CHECK_CONNECTION)) {
                    GameClient.IS_SUCCESS = true;
                }

                Log.d("response", str);

                in.close();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    };
}
