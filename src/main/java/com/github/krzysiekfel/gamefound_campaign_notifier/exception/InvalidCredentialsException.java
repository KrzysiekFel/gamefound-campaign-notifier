package com.github.krzysiekfel.gamefound_campaign_notifier.exception;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Invalid email or password");
    }
}
