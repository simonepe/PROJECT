
package common;

import java.io.Serializable;

public class MessageContainer implements Serializable {
    private final Command type;
    private final GameMove gameMove;
    private String message;

    public MessageContainer(Command type, GameMove move) {
        this.type = type;
        this.gameMove = move;
    }

    public GameMove getGameMove() {
        return gameMove;
    }

    public Command getType() {
        return type;
    }
    
    public void setMsg(String msg){
        this.message = msg;
    }
    
    public String getMsg(){
        return message;
    }

}
