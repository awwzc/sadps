package net.sk.deploy.support.cmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang.StringUtils;

import net.sk.deploy.Command;
import net.sk.deploy.CommandContext;
import net.sk.deploy.support.Config;
import net.sk.deploy.web.Messager;

/**
 * 功能：antBuild
 * @author sam.fan
 */
public class BuildCmd implements Command<Void> {

    private String projects;
    
    public BuildCmd(String projects) {
        this.projects = projects;
    }

    @Override
    public Void execute(CommandContext context) {
        Messager messager = context.getMessager();
        try {
            if (StringUtils.isNotEmpty(projects)) {
                String[] projectArr = projects.split(":");
                for (String project : projectArr) {
                    Process p = Runtime.getRuntime().exec(Config.BATCH_PATH + "/build-" + project + ".bat");
                    BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        messager.sendMessage(line);
                    }
                    messager.sendMessage("--------------------------------------------------------<br></br>");
                }
            }
        } catch (IOException e) {
            messager.sendErrorMessage("build error:" + e.getMessage());
        }
        return null;
    }

}
