package tech.hidetora.securityStarter.exception;

public class AuthorityNotFoundException extends RuntimeException {
    public AuthorityNotFoundException() {
        super("Authority not found!");
    }

    public AuthorityNotFoundException(String message) {
        super(message);
    }
}
