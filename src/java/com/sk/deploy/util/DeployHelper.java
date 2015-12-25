package com.sk.deploy.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.sk.deploy.support.Config;
import com.sk.deploy.support.ProjectInfo;
import com.sk.deploy.support.SFTPChannel;
import com.sk.deploy.support.SFTPConstants;

public class DeployHelper {
    
    private Session session;
    
    private Channel channel;
    /**
     * 建立ssh连接
     * @param projectInfo
     * @return
     * @throws JSchException
     */
    public Channel openConnection(ProjectInfo projectInfo) throws JSchException{
        if(null != channel){
            return channel;
        }
        JSch jsch = new JSch();
        session = jsch.getSession(projectInfo.getUser(),projectInfo.getIp(), 22);
        Properties prop = new Properties();
        prop.put("StrictHostKeyChecking", "no");
        session.setConfig(prop);
        session.setPassword(projectInfo.getPassword());
        session.connect(30000); // making a connection with timeout.
        return session.openChannel("shell");
    }
    
    /**
     * 关闭ssh连接
     */
    public void closeConnection(){
        if(null != channel){
            channel.disconnect();
        }
        if(null != session){
            session.disconnect();
        }
    }
    
    public static boolean matchUser(String name,String password){
        String pwd = SpringContextUtil.getConfig().getUserMap().get(name);
        if (null != pwd && pwd.equals(password)) {
            return true;
        }
        return false;
    }
    
    public static ChannelSftp getSFTPChannel(ProjectInfo projectInfo) throws JSchException{
        Map<String, String> sftpDetails = new HashMap<String, String>();
        sftpDetails.put(SFTPConstants.SFTP_REQ_HOST, projectInfo.getIp());
        sftpDetails.put(SFTPConstants.SFTP_REQ_USERNAME, projectInfo.getUser());
        sftpDetails.put(SFTPConstants.SFTP_REQ_PASSWORD,projectInfo.getPassword());
        sftpDetails.put(SFTPConstants.SFTP_REQ_PORT, "22");
        SFTPChannel channel = new SFTPChannel();
        return channel.getChannel(sftpDetails, 60000);
    }
    
    /**
     * 保存文件到临时目录输出文件
     * @param path
     * @param fileName
     * @param content
     */
    public static void outPutFile(String path,String fileName, String content) {
        File file = new File(path);
        //如果目录不存在，则创建
        if (!file.exists()) {
            return;
        }
        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(new File(path + Config.FILE_SEPARATOR + fileName)),"UTF-8");
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 保存文件到临时目录输出文件
     * @param path
     * @param fileName
     * @param content
     */
    public static void outPutLogFile(String content) {
        File path = new File(Config.LOG_PATH);
        File logFile = new File(Config.LOG_PATH + Config.FILE_SEPARATOR + Config.LOG_FILE);
        //如果目录不存在，则创建
        try {
            if(!path.exists()){
                path.mkdir();
            }
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(logFile,"rw");
            long len = raf.length();
            raf.seek(len);
            raf.write(content.getBytes());
            raf.close();
        } catch (IOException e) {
            System.out.println("write log error:" + e.getMessage());
        }
    }
    
    public static String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]");
        return sdf.format(System.currentTimeMillis());
    }
    
}
