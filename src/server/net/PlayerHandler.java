package server.net;

import common.Command;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.net.Socket;
import common.MsgException;
import common.GameMove;
import common.MessageContainer;
import java.util.Random;

public class PlayerHandler implements Runnable {
    private static final String LEAVE_MESSAGE = " left the game.";
    private static final String JOIN_MESSAGE = " has joined the game!";
    private static final String UNEXPECTED_MSG_TYPE = "A message with an unexpected message type was recieved: ";
    private final Server server;
    private final Socket clientSocket;
    private ObjectInputStream fromClient;
    private ObjectOutputStream toClient;
    private boolean connected;
    private final Random idGenerator = new Random();
    private long playerId;
    public GameMove currentMove;
    private int wins;
    private int roundNumber = 0;
    private int currentScore;
    public String playerName;

   public PlayerHandler(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
        connected = true;
    }

    @Override
    public void run() {
        try {
            fromClient = new ObjectInputStream(clientSocket.getInputStream());
            toClient = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
        server.broadcast(playerId, playerName + JOIN_MESSAGE);
        while (connected) {
            try {
                MessageContainer cmd = (MessageContainer) fromClient.readObject();
                switch (cmd.getType()) {
                    case MOVE:
                        currentMove = cmd.getGameMove();
                        if(server.contr.newGame == null){
                            server.startGame(playerId, currentMove, playerName);
                        }else{
                            server.checkIfAllMovesDone();
                        }
                        break;
                    case DISCONNECT:
                        disconnectClient();
                        server.broadcast(playerId, playerName + LEAVE_MESSAGE);
                        break;
                    default:
                        throw new MsgException(UNEXPECTED_MSG_TYPE + cmd.getType());
                }
            } catch (IOException | ClassNotFoundException e) {
                disconnectClient();
                throw new MsgException(e);
            }
        }
    }
    
    public long createPlayerId() {
        playerId = idGenerator.nextLong();
        return playerId;
    }
    
    public long getPlayerId(){
        return playerId;
    }

    public void sendMsg(String msgSend) throws UncheckedIOException {
        try {
            MessageContainer msg = new MessageContainer(Command.BROADCAST, null);
            msg.setMsg(msgSend);
            toClient.writeObject(msg);
            toClient.flush();
            toClient.reset();
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }
    
    private void disconnectClient() {
        try {
            clientSocket.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        connected = false;
        server.removeHandler(this);
    }
    
    public GameMove getCurrentMove(){
        return currentMove;
    }
    
    public void addWin(){
        wins++;
    }
    
    public void updateValuesAfterRound(){
        currentScore += wins;
        wins = 0;
        roundNumber++;
        currentMove = null;
    }
    
    public int getCurrentScore(){
        return currentScore;
    }
    
    public int getRoundNumber(){
        return roundNumber;
    }
    
    public void setPlayerName(int num){
        playerName = "Player " + String.valueOf(num);
    }
    
    public String getPlayerName(){
        return playerName;
    }
    
}
