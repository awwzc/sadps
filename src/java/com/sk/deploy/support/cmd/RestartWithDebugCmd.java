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
 * debug模式启动应用服务器
 * @author sam.fan
 */
public class RestartWithDebugCmd implements Command<Void> {
    
    private String projects;
    
    public RestartWithDebugCmd(String projects) {
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
                    
                    printStream.println(projectInfo.getStopCmd());
                    printStream.flush();
                    
                    printStream.println(projectInfo.getDebugCmd());
                    printStream.flush();
                    
                    printStream.println("ps -ef|grep " + projectInfo.getName());
                    printStream.flush();
                    
                    InputStream ips = chanel.getInputStream();
                    
                    BufferedReader br = new BufferedReader(new InputStreamReader(ips,"UTF-8"));
                    String line = null;
                    while((line = br.readLine()) != null){
                        messager.sendMessage(line);
                        if(line.indexOf("dt_socket,address=8000")>-1){
                            printStream.write(3);
                            printStream.flush();
                            break;
                        }
                    }
                    messager.sendMessage("server restarted with debug model successful!<br><br>");
                }
            }
            
        } catch (Exception e) {
            messager.sendErrorMessage("server restarted with debug model error!" + e.getMessage() + "<br><br>");
            e.printStackTrace();
        }
        // closeConnection(); 此处关闭连接可能导致应用服务器进程退出
        return null;
    }
    
}
