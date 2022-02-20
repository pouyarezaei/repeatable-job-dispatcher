package exception;

public class StartAgainException extends Exception {
    public StartAgainException() {
        super("Cant Start Dispatcher Again Initiate New Dispatcher For Newer Jobs");
    }
}
