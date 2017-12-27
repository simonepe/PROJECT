package client.net;

import common.Command;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import common.MessageContainer;
import common.MsgException;
import common.GameMove;

public class ServerConnection {
    private static final int TIMEOUT_HALF_HOUR = 1800000;
    private static final int TIMEOUT_HALF_MINUTE = 30000;
    private Socket socket;
    private ObjectOutputStream toServer;
    private ObjectInputStream fromServer;
    private boolean connected;
    private String LOST_CONNECTION = "You lost connection with the server.";
    private String UNEXPECTED_MSG_TYPE = "A message with an unexpected message type was recieved: ";

    public void connect(String host, int port, OutputHandler broadcastHandler) throws IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), TIMEOUT_HALF_MINUTE);
        socket.setSoTimeout(TIMEOUT_HALF_HOUR);
        connected = true;
        toServer = new ObjectOutputStream(socket.getOutputStream());
        fromServer = new ObjectInputStream(socket.getInputStream());
        new Thread(new Listener(broadcastHandler)).start();
    }

    public void disconnect() throws IOException {
        sendMsg(Command.DISCONNECT, null);
        socket.close();
        socket = null;
        connected = false;
    }

    public void sendGameMove(GameMove move) throws IOException {
        sendMsg(Command.MOVE, move);
    }

    private void sendMsg(Command type, GameMove move) throws IOException {
        MessageContainer msg = new MessageContainer(type, move);
        toServer.writeObject(msg);
        toServer.flush();
        toServer.reset();
    }

    private class Listener implements Runnable {
        private final OutputHandler outputHandler;

        private Listener(OutputHandler outputHandler) {
            this.outputHandler = outputHandler;
        }

        @Override
        public void run() {
            try {
                for (;;) {
                    outputHandler.printMsg(extractMsgBody((MessageContainer) fromServer.readObject()));
                }
            } catch (Throwable connectionFailure) {
                if (connected) {
                    outputHandler.printMsg(LOST_CONNECTION);
                }
            }
        }

        private String extractMsgBody(MessageContainer msg) {
            if (msg.getType() != Command.BROADCAST) {
                throw new MsgException(UNEXPECTED_MSG_TYPE + msg.getType());
            }
            return msg.getMsg();
        }
    }
}
