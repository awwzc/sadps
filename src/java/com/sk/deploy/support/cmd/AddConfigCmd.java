package com.sk.deploy.support.cmd;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.springframework.util.StringUtils;

import com.jcraft.jsch.ChannelSftp;
import com.sk.deploy.Command;
import com.sk.deploy.CommandContext;
import com.sk.deploy.support.Config;
import com.sk.deploy.support.ProjectInfo;
import com.sk.deploy.util.DeployHelper;
import com.sk.deploy.web.Messager;

/**
 * 功能：新增配置文件
 * @author sam.fan
 */
public class AddConfigCmd implements Command<Void> {

    private ProjectInfo projectInfo;
    
    private Map<String, String> configMap;

    private Map<String, String> commentMap;
    
    private final static String  CONFIG_PATH = "config";
    
    private final static String MID = "-context"; 
    private final static String SUFFIX = "-prd.properties"; 
    
    public AddConfigCmd(Map<String, String> configMap,ProjectInfo projectInfo,Map<String, String> commentMap) {
        this.configMap = configMap;
        this.projectInfo = projectInfo;
        this.commentMap = commentMap;
    }

    @Override
    public Void execute(CommandContext context) {
        Messager messager = context.getMessager();
        
        try {
            StringBuilder content = new StringBuilder();
            
            ChannelSftp chSftp = DeployHelper.getSFTPChannel(projectInfo);
            String projectName = projectInfo.getName(); 
            int pos = projectName.indexOf('-');
            String fileName = projectName.substring(0,pos) + MID + projectName.substring(pos) + SUFFIX;
            String configFilePath = projectInfo.getDataPath() + CONFIG_PATH;
            
            //从ftp获取properties文件,缓存在ByteArrayOutputStream流中
            InputStream in = chSftp.get(configFilePath + Config.FILE_SEPARATOR + fileName);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            BufferedInputStream bfis = new BufferedInputStream(in);
            byte[] b = new byte[1024];
            int len;
            while ((len = bfis.read(b)) != -1) {
                bos.write(b, 0, len);
            }
            b = null;
            bos.flush();
            bfis.close();
            
            //把原有配置文件内容读取到content中
            InputStream bis = new ByteArrayInputStream(bos.toByteArray());
            InputStreamReader sin = new InputStreamReader(bis,"UTF-8");
            BufferedReader br = new BufferedReader(sin);
            String line = null;
            while ((line = br.readLine()) != null) {
                content.append(line.trim()).append(Config.LINE_FEED_STR);
            }
            
            //比对是否存在key，不存在则添加，存在则忽略
            Properties prop = new Properties();
            prop.load(new ByteArrayInputStream(bos.toByteArray()));
            StringBuffer property = new StringBuffer();
            boolean allExist = true;
            for (Entry<String,String> entry : configMap.entrySet()) {
                String key = entry.getKey();
                if (StringUtils.hasText(prop.getProperty(key))) {
                    messager.sendWarnMessage("key [" + key + "] is exists");
                    continue;
                }
                allExist = false;
                property.append("#ADD KEY ").append(key).append(Config.LINE_FEED_STR);
                String comment = commentMap.get(key);
                if(comment != null){
                    property.append(comment).append(Config.LINE_FEED_STR);
                }
                property.append(entry.getKey()).append("=").append(entry.getValue()).append(Config.LINE_FEED_STR);
            }
            
            if(!allExist){
                //追加到原有文件尾部，并保存到临时目录
                content.append(property);
                DeployHelper.outPutFile(Config.UPLOAD_PATH, fileName, content.toString());
                
                //上传到服务器
                File tempFile = new File(Config.UPLOAD_PATH + Config.FILE_SEPARATOR + fileName);
                new UploadFileCmd(tempFile, projectInfo,configFilePath).execute(context);
                
                //删除临时文件
                FileUtils.deleteQuietly(tempFile);
                messager.sendMessage("add property successful");
            } else {
                messager.sendWarnMessage("all key exists");
            }
        } catch (Exception e) {
            messager.sendErrorMessage("load properties error:" + e.getMessage());
        }
        return null;
    }
    
    

}
