package net.sk.deploy.support;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import net.sk.deploy.Command;
import net.sk.deploy.CommandContext;
import net.sk.deploy.CommandExecutor;
import net.sk.deploy.support.cmd.AddConfigCmd;
import net.sk.deploy.support.cmd.BuildCmd;
import net.sk.deploy.support.cmd.ListFileCmd;
import net.sk.deploy.support.cmd.ModifyConfigCmd;
import net.sk.deploy.support.cmd.RestartCmd;
import net.sk.deploy.support.cmd.RestartWithDebugCmd;
import net.sk.deploy.support.cmd.UploadFileCmd;
import net.sk.deploy.support.cmd.ViewDiskUseAgeCmd;
import net.sk.deploy.util.DeployHelper;
import net.sk.deploy.web.Messager;

@Component("deployService")
public class DeployService implements InitializingBean,CommandExecutor {
	
    private CommandContext commandContext;
    
	@Resource
	private Config deployConfig;
	
	private String userName;
	
	public DeployService() {}
	
    @Override
    public void afterPropertiesSet() throws Exception {
        if(commandContext == null){
            commandContext = new CommandContext();
        }
        commandContext.setConfig(deployConfig);
    }

	public DeployService setMessager(Messager messager) {
        commandContext.setMessager(messager);
    	return this;
    }
	
	public void setUserName(String userName) {
        this.userName = userName;
    }
	
    public String getUserName() {
        return userName;
    }

    @Override
    public <T> T execute(Command<T> command) {
        String msgTemplate = "%s %s execute command: %s \n";
        String logMessage = String.format(msgTemplate, DeployHelper.getTimestamp(),getUserName(),command.getClass().getSimpleName());
        DeployHelper.outPutLogFile(logMessage);
        return command.execute(commandContext);
    }

    /**
     * @param warFiles
	 * @throws Exception 
     */
    public void checkWarAndUpload(){
        File dirPath = new File(Config.UPLOAD_PATH);
        File[] warFiles = dirPath.listFiles();
        if(null != warFiles){
    	    for (File file : warFiles) {
    	    	uploadAndRestart(file);
    	    }
	    }
    }

    /**
     * @param file
     */
    private void uploadAndRestart(File file) {
        String fileName = file.getName();
        Map<String, ProjectInfo> projectContext = deployConfig.getProjectsInfo();
        for (Entry<String, ProjectInfo> projectEntry : projectContext.entrySet()) {
        	String key = projectEntry.getKey();
        	if(fileName.startsWith(key)){
        		ProjectInfo projectInfo = projectEntry.getValue();
        		//上传文件
        		this.execute(new UploadFileCmd(file, projectInfo,projectInfo.getWarPath()));
        		//重启服务
        		this.execute(new RestartCmd(projectInfo));
        		//删除文件
        		FileUtils.deleteQuietly(file);
        		commandContext.getMessager().sendMessage(String.format("delete file [%s] <br><br>", fileName));
        	}
        }
    }
    
    public void restart(String projects) throws Exception{
        if(StringUtils.hasText(projects)){
            String[] projectArr = projects.split(":");
            Map<String, ProjectInfo> projectContext = commandContext.getConfig().getProjectsInfo();
            for (String project : projectArr) {
                ProjectInfo projectInfo = projectContext.get(project);
                this.execute(new RestartCmd(projectInfo));
            }
        }
    }
    public void stopAndDebugCmd(String projects) throws Exception{
    	this.execute(new RestartWithDebugCmd(projects));
    }
    
    public void viewDiskUseAge(String projects) throws Exception{
        this.execute(new ViewDiskUseAgeCmd(projects));
    }
    
    public void listWars(){
        this.execute(new ListFileCmd());
    }
    
    public void build(String projects) throws Exception {
        this.execute(new BuildCmd(projects));
    }
    
    public void addConfig(String project,String config) throws Exception {
        Map<String,String> configMap = new HashMap<String,String>();
        Map<String,String> commentMap = new HashMap<String,String>();
        prepareParams(config, configMap, commentMap);
        
        Map<String, ProjectInfo> projectContext = deployConfig.getProjectsInfo();
        this.execute(new AddConfigCmd(configMap,projectContext.get(project),commentMap));
    }

    /**
     * @param config
     * @param configMap
     * @param commentMap
     * @throws IOException
     */
    private void prepareParams(String config, Map<String, String> configMap, Map<String, String> commentMap)
            throws IOException {
        Reader in = new StringReader(config);
        BufferedReader br = new BufferedReader(in);
        List<String> lines = new ArrayList<String>();
        String line = null;
        while ((line = br.readLine()) != null) {
            String trimLine = line.trim();
            lines.add(trimLine);
            if(StringUtils.hasText(trimLine) && !trimLine.startsWith("#")){
                String[] strArr = line.split("=");
                configMap.put(strArr[0].trim(),strArr[1].trim());
            }
        }
        int len = lines.size();
        for (int i = 0; i < len; i++) {
            String str = lines.get(i);
            if(str.startsWith("#")){
                int pos = i + 1;
                String tempProp;
                String key = null;
                while (pos <= len && key == null) {
                    tempProp = lines.get(pos);
                    pos ++;
                    if(StringUtils.hasText(tempProp) && !tempProp.startsWith("#") && tempProp.indexOf("=") > -1){
                        key = tempProp.split("=")[0].trim();
                    }
                }
                commentMap.put(key, str);
            }
        }
    }
    
    public void modifyConfig(String project,String config) throws Exception {
        Map<String,String> configMap = new HashMap<String,String>();
        Map<String,String> commentMap = new HashMap<String,String>();
        prepareParams(config, configMap, commentMap);
        
        Map<String, ProjectInfo> projectContext = deployConfig.getProjectsInfo();
        
        this.execute(new ModifyConfigCmd(configMap,projectContext.get(project),commentMap));
    }
    
}