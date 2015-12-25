package com.sk.deploy;

public interface Command<T> {
    
    T execute(CommandContext context);
    
}
