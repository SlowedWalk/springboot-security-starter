package tech.hidetora.securityStarter.exception;

public class UsernameAlreadyUsedException extends RuntimeException {
    public UsernameAlreadyUsedException() {
        super("Login name already used!");
    }
}
