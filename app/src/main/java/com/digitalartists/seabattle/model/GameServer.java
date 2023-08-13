package com.digitalartists.seabattle.model;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameServer implements Runnable {

    private Socket serverSocket;
    private PrintWriter out;
    private BufferedReader in;

    @Override
    public void run() {

    }
}
