package server.model;

import common.GameMove;
import java.util.List;
import server.net.PlayerHandler;

public class RockPaperScissorGame {
    private final List<PlayerHandler> players;
    private final long firstPlayerId;
    private final GameMove firstMove;
    private final String playerName;
    private final String GAME_HAS_STARTED_MSG = "A new game has been started by ";
    private final String MAKE_MOVE_MSG = " . Make your move!";
    private final String CURR_ROUND = "Current round: ";
    private final String CURR_SCORE = " Current score: ";
    private final String NEW_GAME_MSG = "Make a move to start a new round or type quit to end game";
    
    public RockPaperScissorGame(List<PlayerHandler> players, long firstPlayerId, GameMove firstMove, String playerName){
        this.players = players;
        this.firstPlayerId = firstPlayerId;
        this.firstMove = firstMove;
        this.playerName = playerName;
    }
    
    public void startGame(){
        synchronized (players) {
            for (PlayerHandler player : players) {
                if(player.getPlayerId() != firstPlayerId){
                    player.sendMsg(GAME_HAS_STARTED_MSG + playerName + MAKE_MOVE_MSG);
                }
            }
        }
    }
    
    public void calculateScore(){
        for (PlayerHandler player : players) {
            GameMove move = player.getCurrentMove();
            for (PlayerHandler opponent : players){
                GameMove opponentMove = opponent.getCurrentMove();
                boolean winner = checkIfWinner(move, opponentMove);
                if(winner){
                    player.addWin();
                }
            }
        }
        
    }
    
    public boolean checkIfAllMovesDone(){
        for (PlayerHandler player : players) {
            if(player.currentMove == null){
                return false;
            }
        }
        finishRound();
        return true;
    }
    
    public void finishRound(){
        calculateScore();
        for (PlayerHandler player : players) {
                player.updateValuesAfterRound();
                player.sendMsg(CURR_ROUND + String.valueOf(player.getRoundNumber() 
                        + CURR_SCORE + String.valueOf(player.getCurrentScore()))
                        + System.lineSeparator() + NEW_GAME_MSG);
            }
    }
    
    private boolean checkIfWinner(GameMove move, GameMove opponentMove){
        if(move.equals(GameMove.ROCK)){
            if(opponentMove.equals(GameMove.SCISSOR)){
                return true;
            }    
        }
        if(move.equals(GameMove.PAPER)){
            if(opponentMove.equals(GameMove.ROCK)){
                return true;
            }
        }
        if(move.equals(GameMove.SCISSOR)){
            if(opponentMove.equals(GameMove.PAPER)){
                return true;
            }
        }
        return false;
    }
    
}
