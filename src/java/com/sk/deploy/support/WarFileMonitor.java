package com.sk.deploy.support;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.stereotype.Component;

@Component("warFileMonitor")
public class WarFileMonitor {
	
	private FileAlterationMonitor monitor;
	
	private boolean isRunning = false;
	
	public WarFileMonitor() {
		monitor = new FileAlterationMonitor(1000);
    }

	public WarFileMonitor(long interval){
	   monitor = new FileAlterationMonitor(interval);
    }

	public void monitor(String path,FileAlterationListener listener){
		FileAlterationObserver observer = new FileAlterationObserver(path);
		Iterable<FileAlterationObserver> observers = monitor.getObservers();
		if (!observers.iterator().hasNext()) {
			observer.addListener(listener);
			monitor.addObserver(observer);
        }
		
	}
	
	public void start() throws Exception{
		monitor.start();
		isRunning = true;
	}
	public void stop() throws Exception{
		monitor.stop();
		isRunning = false;
	}

	public synchronized boolean isRunning() {
    	return isRunning;
    }
}
