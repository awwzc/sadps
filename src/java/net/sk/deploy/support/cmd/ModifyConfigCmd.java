package net.sk.deploy.support.cmd;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.springframework.util.StringUtils;

import com.jcraft.jsch.ChannelSftp;

import net.sk.deploy.Command;
import net.sk.deploy.CommandContext;
import net.sk.deploy.support.Config;
import net.sk.deploy.support.ProjectInfo;
import net.sk.deploy.util.DeployHelper;
import net.sk.deploy.web.Messager;

/**
 * 功能：修改配置文件
 * @author sam.fan
 */
public class ModifyConfigCmd implements Command<Void> {

    private ProjectInfo projectInfo;
    
    private Map<String, String> configMap;
    
    private Map<String, String> commentMap;
    
    private final static String  CONFIG_PATH = "config";
    
    private final static String MID = "-context"; 
    private final static String SUFFIX = "-prd.properties"; 
    
    public ModifyConfigCmd(Map<String, String> configMap,ProjectInfo projectInfo,Map<String, String> commentMap) {
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
            
            //原配置文件load到properties
            Properties prop = new Properties();
            prop.load(new ByteArrayInputStream(bos.toByteArray()));
            
            
            //把原有配置文件内容读取到content中，并逐行比较
            InputStream bis = new ByteArrayInputStream(bos.toByteArray());
            InputStreamReader sin = new InputStreamReader(bis,"UTF-8");
            BufferedReader br = new BufferedReader(sin);
            String line = null;
            boolean hasModify = false;
            while ((line = br.readLine()) != null) {
                String formatLine = line.trim();
                if(!formatLine.startsWith("#")){
                    String existsKey = formatLine.split("=")[0].trim();
                    String oldValue = prop.getProperty(existsKey);
                    String newValue = configMap.get(existsKey);
                    if (StringUtils.hasText(newValue) && !newValue.equals(oldValue)) {
                        content.append("#MODIFY ").append(formatLine).append(Config.LINE_FEED_STR);
                        String comment = commentMap.get(existsKey);
                        if(comment != null){
                            content.append(comment).append(Config.LINE_FEED_STR);
                        }
                        content.append(existsKey).append("=").append(newValue).append(Config.LINE_FEED_STR);
                        hasModify = true;
                        continue;
                    }
                }
                content.append(formatLine).append(Config.LINE_FEED_STR);
            }
            
            if(hasModify){
                //追加到原有文件尾部，并保存到临时目录
                DeployHelper.outPutFile(Config.UPLOAD_PATH, fileName, content.toString());
                
                //上传到服务器
                File tempFile = new File(Config.UPLOAD_PATH + Config.FILE_SEPARATOR + fileName);
                new UploadFileCmd(tempFile, projectInfo,configFilePath).execute(context);
                
                //删除临时文件
                FileUtils.deleteQuietly(tempFile);
                messager.sendMessage("modify properties successful");
            } else {
                messager.sendWarnMessage("no properties has changed");
            }
        } catch (Exception e) {
            messager.sendErrorMessage("load properties error:" + e.getMessage());
        }
        return null;
    }
    
}
