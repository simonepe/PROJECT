package client.controller;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;
import client.net.ServerConnection;
import client.net.OutputHandler;
import common.GameMove;

public class Controller {
    private final ServerConnection serverConnection = new ServerConnection();
    private final String MAKE_MOVE = "You are connected to localhost. Enter a move to start a game!";

    public void connect(String host, int port, OutputHandler outputHandler) {
        CompletableFuture.runAsync(() -> {
            try {
                serverConnection.connect(host, port, outputHandler);
            } catch (IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
        }).thenRun(() -> outputHandler.printMsg(MAKE_MOVE));
    }

    public void disconnect() throws IOException {
        serverConnection.disconnect();
    }

    public void sendGameMove(GameMove move) {
        CompletableFuture.runAsync(() -> {
            try {
                serverConnection.sendGameMove(move);
            } catch (IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
        });
    }
}
