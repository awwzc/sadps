package com.sk.deploy.support.cmd;

import java.io.File;

import com.jcraft.jsch.ChannelSftp;
import com.sk.deploy.Command;
import com.sk.deploy.CommandContext;
import com.sk.deploy.support.Config;
import com.sk.deploy.support.FileProgressMonitor;
import com.sk.deploy.support.ProjectInfo;
import com.sk.deploy.util.DeployHelper;
import com.sk.deploy.web.Messager;

/**
 * 功能：文件上传至服务器
 * @author sam.fan
 */
public class UploadFileCmd implements Command<Void> {
    
    private File sourceFile;
    private ProjectInfo projectInfo;
    private String targetPath;
    
    public UploadFileCmd(File sourceFile, ProjectInfo projectInfo,String targetPath) {
        super();
        this.sourceFile = sourceFile;
        this.projectInfo = projectInfo;
        this.targetPath = targetPath;
    }

    @Override
    public Void execute(CommandContext context) {
        Messager messager = context.getMessager();
        String fileName = sourceFile.getName();
        String target = targetPath + Config.FILE_SEPARATOR + fileName;
        long fileSize = sourceFile.length();
        String src = sourceFile.getAbsolutePath();
        try{
            //上传文件
            ChannelSftp chSftp = DeployHelper.getSFTPChannel(projectInfo);
            chSftp.put(src,target, new FileProgressMonitor(messager,fileSize), ChannelSftp.OVERWRITE); // 代码段2
            chSftp.quit();
        } catch (Exception e) {
            e.printStackTrace();
            messager.sendErrorMessage(String.format("upload file[%s] Exception:%s", fileName,e.getMessage()));
        }
        return null;
    }
    
}
