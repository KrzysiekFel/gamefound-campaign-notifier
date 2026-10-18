package com.github.krzysiekfel.gamefound_campaign_notifier.exception;

public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(Long id) {
    super("User " + id + " not found");
  }
}
