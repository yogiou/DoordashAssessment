package jie.wen.doordash.assessment.springboot.data.error;

public class NoPhoneNumberRequestException extends Error {
    public NoPhoneNumberRequestException() {
        super();
    }
    public NoPhoneNumberRequestException(String message, Throwable cause) {
        super(message, cause);
    }
    public NoPhoneNumberRequestException(String message) {
        super(message);
    }
    public NoPhoneNumberRequestException(Throwable cause) {
        super(cause);
    }
}
