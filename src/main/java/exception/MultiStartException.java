package exception;

public class MultiStartException extends Exception {
    public MultiStartException() {
        super("Cant Start Dispatcher Multiple time");
    }
}
