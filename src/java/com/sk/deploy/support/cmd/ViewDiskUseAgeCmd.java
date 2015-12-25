package com.sk.deploy.support.cmd;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.jcraft.jsch.Channel;
import com.sk.deploy.Command;
import com.sk.deploy.CommandContext;
import com.sk.deploy.support.ProjectInfo;
import com.sk.deploy.util.DeployHelper;
import com.sk.deploy.web.Messager;

/**
 * 查看服务器硬盘使用情况
 * @author sam.fan
 */
public class ViewDiskUseAgeCmd implements Command<Void> {
    
    private String projects;
    
    public ViewDiskUseAgeCmd(String projects) {
        this.projects = projects;
    }

    @Override
    public Void execute(CommandContext context) {
        Messager messager = context.getMessager();
        DeployHelper deployHelper = new DeployHelper();
        try {
            if(StringUtils.isNotEmpty(projects)){
                String[] projectArr = projects.split(":");
                Map<String, ProjectInfo> projectContext = context.getConfig().getProjectsInfo();
                for (String project : projectArr) {
                    ProjectInfo projectInfo = projectContext.get(project);
                    Channel chanel = deployHelper.openConnection(projectInfo);
                    OutputStream ops = chanel.getOutputStream();
                    PrintStream printStream = new PrintStream(ops, true);
                    chanel.setOutputStream(null);
                    chanel.connect(3000);
                    
                    printStream.println("df -h");
                    printStream.flush();
                    
                    InputStream ips = chanel.getInputStream();
                    
                    BufferedReader br = new BufferedReader(new InputStreamReader(ips,"UTF-8"));
                    String line = null;
                    int i = 0;
                    while((line = br.readLine()) != null){
                        i++;
                        messager.sendMessage(line);
                        if(i == 9){
                            printStream.write(3);
                            printStream.flush();
                            break;
                        }
                    }
                    //closeConnection(); 此处关闭连接可能导致应用服务器进程退出
                    messager.sendMessage("---------------------------" + project + " storge state-------------------------------<br><br>");
                }
            }
        } catch (Exception e) {
            messager.sendErrorMessage("view storge status error " + e.getMessage());
            e.printStackTrace();
        }
        // closeConnection(); 此处关闭连接可能导致应用服务器进程退出
        return null;
    }
    
}
