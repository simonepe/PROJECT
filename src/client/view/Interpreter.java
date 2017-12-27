package client.view;

import java.util.Scanner;
import client.controller.Controller;
import client.net.OutputHandler;
import common.GameMove;

public class Interpreter implements Runnable {
    private static final String OPERATION_FAILED = "Operation failed";
    private static final String PROMPT = "> ";
    private final Scanner console = new Scanner(System.in);
    private boolean recCommands = false;
    private Controller contr;
    private final ThreadSafeOutput output = new ThreadSafeOutput();
    private final String host = "localhost";
    private final int port = 3333;
    private final String CONNECT_MSG = "Type connect to connect to server.";

    public void start() {
        if (recCommands) {
            return;
        }
        recCommands = true;
        contr = new Controller();
        new Thread(this).start();
    }

    @Override
    public void run() {
        output.println(CONNECT_MSG);
        while (recCommands) {
            try {
                String input = new String(nextLine());
                switch (input.toUpperCase()) {
                    case "QUIT":
                        recCommands = false;
                        contr.disconnect();
                        break;
                    case "CONNECT":
                        contr.connect(host, port, new ConsoleOutput());
                        break;
                    default:
                        contr.sendGameMove(GameMove.valueOf(input.toUpperCase()));
                }
            } catch (Exception e) {
                output.println(OPERATION_FAILED);
            }
        }
    }

    private String nextLine() {
        output.print(PROMPT);
        return console.nextLine();
    }

    private class ConsoleOutput implements OutputHandler {
        @Override
        public void printMsg(String msg) {
            output.println((String) msg);
            output.print(PROMPT);
        }
    }

}
