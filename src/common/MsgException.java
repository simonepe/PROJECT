package common;

public class MsgException extends RuntimeException {
    public MsgException(String msg) {
        super(msg);
    }
    
    public MsgException(Throwable rootCause) {
        super(rootCause);
    }
}
