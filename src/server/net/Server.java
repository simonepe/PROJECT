package server.net;

import common.GameMove;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import server.controller.Controller;

public class Server {
    private static final int LINGER_TIME = 5000;
    private static final int TIMEOUT_HALF_HOUR = 1800000;
    public final Controller contr = new Controller();
    private final List<PlayerHandler> players = new ArrayList<>();
    private final int portNo = 3333;
    private int playerNum = 1;

    public static void main(String[] args) {
        System.out.println("Server started...");
        Server server = new Server();
        server.serve();
    }

    void broadcast(long senderId, String msg) {
        synchronized (players) {
            for (PlayerHandler participant : players) {
                if(participant.getPlayerId() != senderId){
                    participant.sendMsg(msg);
                }
            }
        }
    }

    void removeHandler(PlayerHandler handler) {
        synchronized (players) {
            players.remove(handler);
        }
    }

    private void serve() {
        try {
            ServerSocket listeningSocket = new ServerSocket(portNo);
            while (true) {
                Socket clientSocket = listeningSocket.accept();
                startHandler(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Server failure.");
        }
    }

    private void startHandler(Socket clientSocket) throws SocketException {
        clientSocket.setSoLinger(true, LINGER_TIME);
        clientSocket.setSoTimeout(TIMEOUT_HALF_HOUR);
        PlayerHandler handler = new PlayerHandler(this, clientSocket);
        handler.setPlayerName(playerNum);
        handler.createPlayerId();
        playerNum++;
        synchronized (players) {
            players.add(handler);
        }
        Thread handlerThread = new Thread(handler);
        handlerThread.setPriority(Thread.MAX_PRIORITY);
        handlerThread.start();
    }
    
    public void startGame(long firstPlayer, GameMove firstMove, String playerName){
        contr.startGame(players, firstPlayer, firstMove, playerName);
    }
    
    public void checkIfAllMovesDone(){
        contr.checkIfAllMovesDone();
    }
}
