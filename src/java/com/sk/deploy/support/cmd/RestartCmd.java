package com.sk.deploy.support.cmd;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

import com.jcraft.jsch.Channel;
import com.sk.deploy.Command;
import com.sk.deploy.CommandContext;
import com.sk.deploy.support.ProjectInfo;
import com.sk.deploy.util.DeployHelper;
import com.sk.deploy.web.Messager;

/**
 * 功能：重启服务器
 * @author sam.fan
 */
public class RestartCmd implements Command<Void> {
    
    private ProjectInfo projectInfo;
    
    public RestartCmd(ProjectInfo projectInfo) {
        this.projectInfo = projectInfo;
    }

    @Override
    public Void execute(CommandContext context) {
        Messager messager = context.getMessager();
        DeployHelper deployHelper = new DeployHelper();
        try {
            Channel chanel;
            chanel = deployHelper.openConnection(projectInfo);
            OutputStream ops = chanel.getOutputStream();
            PrintStream printStream = new PrintStream(ops, true);
            chanel.setOutputStream(null);
            chanel.connect(3000);

            printStream.println(projectInfo.getResetCmd());
            printStream.flush();

            InputStream ips = chanel.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(ips,"UTF-8"));
            String line = null;
            boolean beginDeploy = false;
            while ((line = br.readLine()) != null) {
                if (line.indexOf("Deploying configuration descriptor") > -1) {
                    beginDeploy = true;
                }
                messager.sendMessage(line);
                if (beginDeploy && line.indexOf("Server startup in") > -1) {
                    printStream.write(3);
                    printStream.flush();
                    messager.sendMessage("Execute Ctrl+C ");
                    break;
                }
            }
            messager.sendMessage("server restarted successful!");
        } catch (Exception e) {
            messager.sendErrorMessage("server restarted error" + e.getMessage());
        }
        // closeConnection(); 此处关闭连接可能导致应用服务器进程退出
        return null;
    }
    
}
