package com.sk.deploy;

import com.sk.deploy.support.Config;
import com.sk.deploy.web.Messager;

public class CommandContext {
    
    protected Messager messager;
    
    protected Config config;
    
    public Messager getMessager() {
        return messager;
    }

    public void setMessager(Messager messager) {
        this.messager = messager;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }
    
}
