package net.sk.deploy;

public interface CommandExecutor {
    
    <T> T execute(Command<T> command);
    
}
