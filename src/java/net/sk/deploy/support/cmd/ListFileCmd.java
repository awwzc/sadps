package net.sk.deploy.support.cmd;

import java.io.File;

import net.sk.deploy.Command;
import net.sk.deploy.CommandContext;
import net.sk.deploy.support.Config;
import net.sk.deploy.web.Messager;

/**
 * 列出文件上传临时目录的所有文件
 * @author sam.fan
 */
public class ListFileCmd implements Command<Void> {
    
    @Override
    public Void execute(CommandContext context) {
        Messager messager = context.getMessager();
        File dirFile = new File(Config.UPLOAD_PATH);
        File[] warFiles = dirFile.listFiles();
        if(null != warFiles){
            StringBuffer fileNames = new StringBuffer();
            for (File file : warFiles) {
                fileNames.append(file.getName() + "\t");
            }
            messager.sendMessage(fileNames.toString());
        }
        return null;
    }
    
}
