package net.sk.deploy;

public interface Command<T> {
    
    T execute(CommandContext context);
    
}
