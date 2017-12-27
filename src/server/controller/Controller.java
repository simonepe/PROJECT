package server.controller;

import common.GameMove;
import java.util.List;
import server.model.RockPaperScissorGame;
import server.net.PlayerHandler;

/**
 * The server side controller. All calls to the server side model pass through here.
 */
public class Controller {
    public RockPaperScissorGame newGame;
    List<PlayerHandler> players;

    public void startGame(List<PlayerHandler> players, long firstPlayer, GameMove firstMove, String playerName){
        newGame = new RockPaperScissorGame(players, firstPlayer, firstMove, playerName);
        newGame.startGame();
    }
    
    public void checkIfAllMovesDone(){
        newGame.checkIfAllMovesDone();
    }
}
