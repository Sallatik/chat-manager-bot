package lat.sal.zwolabot;

public class ZwolabotException extends RuntimeException {

    public ZwolabotException() {
    }

    public ZwolabotException(String message) {
        super(message);
    }

    public ZwolabotException(String message, Throwable cause) {
        super(message, cause);
    }

    public ZwolabotException(Throwable cause) {
        super(cause);
    }

    public ZwolabotException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
