package com.sk.deploy.support;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.sk.deploy.util.BeanMapUtil;


@Component("deployConfig")
public class Config implements InitializingBean {	
	
	private final static String CONFIG_FILE = "config.properties";

	private static final String BUILD_PROJECT_PREFIX = "com.txxy.build."; 

	private static final String DEPLOY_USERS_KEY = "txxy.deploy.users"; 
	
	private static final String MONITOR_PATH_KEY = "txxy.deploy.monitor.path"; 
	
	private static final String UPLOAD_PATH_KEY = "txxy.deploy.upload.path";
	
	private static final String BATCH_PATH_KEY = "txxy.deploy.batch.path";
	
	private static final String LOG_PATH_KEY = "txxy.deploy.log.path";
	
	public final static String LOG_FILE = "deploy.log";
	
    public final static String FILE_SEPARATOR = "/";
    
    public final static String LINE_FEED_STR = "\n";
	
	public static String UPLOAD_PATH = "d:/upload_war";
	
	public static String MONITOR_PATH = "D:/dev/2015";
	
	public static String LOG_PATH = "D:/build";

	public static String BATCH_PATH = "D:/batch";
	
	
	enum logLevel{
	    INFO(null,0),WRAN("<span style=\"color:yellow;\">%s</span>",1),ERROR("<span style=\"color:red;\">%s</span>",2);
	    
	    private logLevel(String style, int code) {
            this.style = style;
            this.code = code;
        }
        private String style;
	    private int code;
        public String getStyle() {
            return style;
        }
        public void setStyle(String style) {
            this.style = style;
        }
        public int getCode() {
            return code;
        }
        public void setCode(int code) {
            this.code = code;
        }
	    
	} 
	
	public Map<String,String> userMap = new HashMap<String, String>();
	
	private Map<String,ProjectInfo> projectsInfo = new LinkedHashMap<String,ProjectInfo>();
	
	public Config() {
    }
	@Override
	public void afterPropertiesSet() throws Exception {
	    try {
	        this.init();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	public void init() throws IOException {
		Properties prop = new Properties();
		try {
			InputStreamReader sin = new InputStreamReader(this.getClass().getResourceAsStream(Config.FILE_SEPARATOR + CONFIG_FILE),"UTF-8");
			prop.load(sin);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Set<Object> keySet = prop.keySet();
		Map<String,Map<Object,Object>> projectMaps = new LinkedHashMap<String,Map<Object,Object>>();
		
		for (Object object : keySet) {
			String key = object.toString();
			if(key.startsWith(BUILD_PROJECT_PREFIX)){
				String[] nodeDesc = key.substring(BUILD_PROJECT_PREFIX.length()).split("\\.");
				String nodeName = nodeDesc[0];
				String property = nodeDesc[1];
				String value = prop.getProperty(key);
				Map<Object, Object> projectMap = projectMaps.get(nodeName);
				if(null == projectMap){
					projectMap = new LinkedHashMap<Object,Object>();
				}
				projectMap.put(property, value);
				projectMaps.put(nodeName, projectMap);
			}
		}
		for (Entry<String, Map<Object, Object>> entry : projectMaps.entrySet()) {
			Map<Object, Object> projectMap = entry.getValue();
			ProjectInfo projectInfo = BeanMapUtil.map2Bean(projectMap,ProjectInfo.class);
			projectsInfo.put(projectInfo.getName(),projectInfo);
		}
		
		String users = prop.getProperty(DEPLOY_USERS_KEY);
		String[] usersInfo = users.split(",");
		for (String user : usersInfo) {
		    String[] userInfoArr = user.split(":");
		    userMap.put(userInfoArr[0],userInfoArr[1]);
		}
		
		String monitorPath = prop.getProperty(MONITOR_PATH_KEY);
		if(StringUtils.hasText(monitorPath)){
		    MONITOR_PATH = monitorPath;
		}
		String uploadPath = prop.getProperty(UPLOAD_PATH_KEY);
		if(StringUtils.hasText(uploadPath)){
		    UPLOAD_PATH = uploadPath;
		}
		String batchPath = prop.getProperty(BATCH_PATH_KEY);
		if(StringUtils.hasText(batchPath)){
		    BATCH_PATH = batchPath;
		}
		String logPath = prop.getProperty(LOG_PATH_KEY);
		if(StringUtils.hasText(logPath)){
		    LOG_PATH = logPath;
		}
	}

	public Map<String, ProjectInfo> getProjectsInfo() {
    	return projectsInfo;
    }

	public void setProjectsInfo(Map<String, ProjectInfo> projectsInfo) {
    	this.projectsInfo = projectsInfo;
    }
	
	public Map<String, String> getUserMap() {
        return userMap;
    }
	
}
