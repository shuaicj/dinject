package shuaicj.dinject;

/**
 * General dinject exception.
 *
 * @author shuaicj 2018/04/12
 */
@SuppressWarnings("serial")
public class DinjectException extends RuntimeException {

    public DinjectException(String message) {
        super(message);
    }

    public DinjectException(String message, Throwable cause) {
        super(message, cause);
    }
}
