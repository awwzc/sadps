package net.sk.deploy.support;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import com.jcraft.jsch.SftpProgressMonitor;

import net.sk.deploy.web.Messager;

/**
 * 功能：文件上传进度监视器
 * @author sam.fan
 */
public class FileProgressMonitor extends TimerTask implements SftpProgressMonitor {
    
    private long progressInterval = 1 * 500; // 默认间隔时间为5秒
    
    private boolean isEnd = false; // 记录传输是否结束
    
    private long transfered; // 记录已传输的数据总大小
    
    private long fileSize; // 记录文件总大小
    
    private Timer timer; // 定时器对象
    
    private boolean isScheduled = false; // 记录是否已启动timer记时器
    
    private Messager messager;
    
    public FileProgressMonitor(long fileSize) {
    	this.fileSize = fileSize;
    }
    public FileProgressMonitor(Messager messager,long fileSize) {
    	this.messager = messager;
        this.fileSize = fileSize;
    }

	@Override
    public void run() {
        if (!isEnd()) { // 判断传输是否已结束
            long transfered = getTransfered();
            if (transfered != fileSize) { // 判断当前已传输数据大小是否等于文件总大小
                messager.sendMessage("current uploaded: " + transfered/1024 + " kb," + getTransferRate(transfered));
            } else {
                setEnd(true); // 如果当前已传输数据大小等于文件总大小，说明已完成，设置end
            }
        } else {
            messager.sendMessage("upload competed...");
            stop(); // 如果传输结束，停止timer记时器
            return;
        }
    }
    
    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
            isScheduled = false;
        }
    }
    
    public void start() {
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(this, 1000, progressInterval);
        isScheduled = true;
        messager.sendMessage("start upload...");
    }
    
    /**
     * 打印progress信息
     * @param transfered
     */
    private String getTransferRate(long transfered) {
    	String progress = " compelte:";
        if (fileSize != 0) {
            double d = ((double)transfered * 100)/(double)fileSize;
            DecimalFormat df = new DecimalFormat( "#.##"); 
            progress += df.format(d) + "%";
        } else {
        	 progress += transfered;
        }
        return progress;
    }

    /**
     * 实现了SftpProgressMonitor接口的count方法
     */
    public boolean count(long count) {
        if (isEnd()) return false;
        if (!isScheduled) {
            start();
        }
        add(count);
        return true;
    }

    /**
     * 实现了SftpProgressMonitor接口的end方法
     */
    public void end() {
        setEnd(true);
    }
    
    private synchronized void add(long count) {
        transfered = transfered + count;
    }
    
    private synchronized long getTransfered() {
        return transfered;
    }
    
    public synchronized void setTransfered(long transfered) {
        this.transfered = transfered;
    }
    
    private synchronized void setEnd(boolean isEnd) {
        this.isEnd = isEnd;
    }
    
    private synchronized boolean isEnd() {
        return isEnd;
    }

    public void init(int op, String src, String dest, long max) {}
    
}