package com.callUrl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;

public class CallUrl {
	protected final static String CHARACTER = "utf-8";
	protected final static String NORMAL = "http://192.168.11.137:8978/odd_server/odd_server?";
	protected final static String TEST = "http://192.168.10.92:8080/odd_server/odd_server?";
	protected final static String LOCAL = "http://localhost:8086/odd/odd_server?";
	public final static String LOCALPATH = "localPath";
	public final static String FILEUSER = "fileUser";
	protected final static String CALL = "call";
	protected final static String PARAMETER1 = "parameter1";
	protected final static String PARAMETER2 = "parameter2";
	protected final static String PARAMETER3 = "parameter3";
	protected final static String PARAMETER4 = "parameter4";
	protected final static String RFIDTXT = "rfidTxt.properties";
	public final static String JSONKEY = "jsonKey";
	
	protected final static String CLASSLOADER = "classLoader";
	protected final static String RFIDTXTURL = "rfidTxtUrl";
	protected final static String EDIT = "edit";
	
	@SuppressWarnings("deprecation")
	public static void sendMessageToOdd(String data) throws HttpException, IOException {
		HttpClient client = new HttpClient();
		PostMethod postMethod = new PostMethod(getRfidTxt(CALL));
		client.setTimeout(30000);
		String[] p = getRfidTxt(PARAMETER1).split(",");
		postMethod.setParameter(p[0], p[1]);
		p = getRfidTxt(PARAMETER2).split(",");
		postMethod.setParameter(p[0], p[1]);
		p = getRfidTxt(PARAMETER3).split(",");
		postMethod.setParameter(p[0], p[1]);
		p = getRfidTxt(PARAMETER4).split(",");
		postMethod.setParameter(p[0], data);
		//编码设置
		postMethod.getParams().setHttpElementCharset(CHARACTER);
		postMethod.getParams().setContentCharset(CHARACTER);
		client.executeMethod(postMethod);
	}
	public static String getRfidTxt(String keyName){
		String url = "";
		InputStream inputStream = CallUrl.class.getClassLoader().getResourceAsStream(RFIDTXT);
		Properties p = new Properties();
		try {
			p.load(inputStream);
			if(EDIT.equals(p.getProperty(CLASSLOADER))){//jar方式发布,允许用户自定义修改
				File file = new File(p.getProperty(RFIDTXTURL));
			 	FileInputStream in = null;
		        try{
		            in = new FileInputStream(file);
		            Properties pp = new Properties();
		            pp.load(in);
		            url = pp.getProperty(keyName);
		        } catch (FileNotFoundException e){
		            e.printStackTrace();
		        }
			}else{
				url = p.getProperty(keyName);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return url;
	}
	//这里取txt文件内容并转换成json格式
	private static String readUsers(){
		File file = new  File(getRfidTxt(LOCALPATH)+getRfidTxt(FILEUSER));  
        if(!file.exists()){
        	return null;
        }
        String d = CallUtils.readStrTxt(file,CHARACTER);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(getRfidTxt(JSONKEY), StringUtils.isBlank(d)?"":CallUtils.toUnicodeString(d.trim()));
		String data = CallUtils.getCreateJson(map);
		
		return data;
	}
	public static void main(String[] args) {
		try {
			File localFile = new  File(getRfidTxt(LOCALPATH));  
	        if(!localFile.exists()){
	        	localFile.mkdir();
	        }
	        String data = readUsers();
			if(StringUtils.isBlank(data)){
				return;
			}
			sendMessageToOdd(data);
			
			File file = new  File(getRfidTxt(LOCALPATH)+getRfidTxt(FILEUSER));  
	        if(file.exists()){
	        	file.delete();
	        }
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
