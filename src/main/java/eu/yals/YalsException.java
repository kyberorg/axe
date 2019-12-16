package eu.yals;

public class YalsException extends RuntimeException {
    public YalsException() {
    }

    public YalsException(Throwable th) {
        super(th);
    }
}
