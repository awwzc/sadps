package net.sk.deploy.support;

import java.io.File;
import java.io.IOException;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.stereotype.Component;

import net.sk.deploy.web.Messager;

@Component("warFileListener")
public class WarFileListener implements FileAlterationListener{
	
	private Messager messager;
	
	public Messager getMessager() {
    	return messager;
    }

	public void setMessager(Messager messager) {
    	this.messager = messager;
    }

	private static String destDir = Config.UPLOAD_PATH;
	
	public WarFileListener() {}

	@Override
    public void onDirectoryChange(File file) {
		messager.sendMessage(String.format("change file [%s]", file.getAbsolutePath()));
    }

	@Override
    public void onDirectoryCreate(File file) {
		messager.sendMessage(String.format("delete file [%s]", file.getAbsolutePath()));
    }

	@Override
    public void onDirectoryDelete(File file) {
		messager.sendMessage(String.format("delete directory [%s]", file.getAbsolutePath()));
    }

	@Override
    public void onFileChange(File file) {
		messager.sendMessage(String.format("change file [%s]", file.getName()));
		copyFile(file);
    }

	@Override
    public void onFileCreate(File file) {
		messager.sendMessage(String.format("create file [%s]", file.getName()));
		copyFile(file);
    }

	@Override
    public void onFileDelete(File file) {
		messager.sendMessage(String.format("delete file [%s]", file.getName()));
    }
	
	private synchronized void copyFile(File srcFile){
		File dirFile = new File(destDir);
		try {
			if(srcFile.exists()){
				messager.sendMessage("copy file [" + srcFile.getName() + "] to directory [" + destDir + "]" );
				FileUtils.copyFileToDirectory(srcFile, dirFile,true);
			}
        } catch (IOException e) {
	        e.printStackTrace();
        }
	}

	@Override
    public void onStart(FileAlterationObserver observer) {
	    
    }

	@Override
    public void onStop(FileAlterationObserver observer) {
	    
    }

	
}
