package com.sk.deploy;

public interface CommandExecutor {
    
    <T> T execute(Command<T> command);
    
}
