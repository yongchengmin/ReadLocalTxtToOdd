package com.socket;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import com.callUrl.CallUtils;
import com.callUrl.ParamsUtil;
import com.callUrl.RequestUtil;
import com.print.QuieeDirectPrintJobJava;

//DataSender.java is client.
public class MessageAcceptServer {
	public static String dmy_hms = "yyyy-MM-dd HH:mm:ss";
	public static String charsetName = "UTF-8";
	public static void main(String[] args) throws IOException {
		/*ServerSocket server = new ServerSocket(8898);
		Socket socket = null;
		boolean flag = true;
		try {
			while(flag){
				socket = server.accept();
				createHandlerThread(socket);
			}
		} finally{
			server.close();
		}*/
		String reportParams="id=0;ids=[462260]";//id=0;ids=[172, 173]
		String appRoot = MessageAcceptServer.getRfidTxt(MessageAcceptServer.appRoot);
		String reportName = MessageAcceptServer.getRfidTxt(MessageAcceptServer.reportName);
		String printServiceName = MessageAcceptServer.getRfidTxt(MessageAcceptServer.printServiceName);
		QuieeDirectPrintJobJava a = new QuieeDirectPrintJobJava(appRoot, reportName, reportParams,printServiceName);
		a.print();
	}
	
	public static void createHandlerThread(Socket socket){
		MessageAcceptServer mas = MessageAcceptServer.getInstance();
		Handler handler = mas.new Handler(socket);
		Thread t = new Thread(handler);
		t.start();
	}
	private static MessageAcceptServer single=null;
	public static MessageAcceptServer getInstance(){
		if(single==null){
			single = new MessageAcceptServer();
		}
		return single;
	}
	public class Handler implements Runnable{
		private Socket socket = null;
		public Handler(Socket socket){
			this.socket = socket;
		}
		public void run() {
			BufferedReader in = null;
			PrintWriter out = null;
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream(), charsetName));
				out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), charsetName));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			String getLine = null;
			try {
				getLine = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(getLine!=null){
//				System.out.println(getLine);
				String status = "no";
				String value = "";
				String showMesg = getLine;
				//服务器校验
				String beServer = CallUtils.YESE;//MessageAcceptServer.getRfidTxt(MessageAcceptServer.beServer);
				if(!StringUtils.isEmpty(beServer)){
					if(CallUtils.YESE.equals(beServer)){
						String url = MessageAcceptServer.getRfidTxt(MessageAcceptServer.appServer);
						Map<String, Object> paramMap = new HashMap<String, Object>();
						paramMap.put("data", getLine);
						String result = null;
						try {
							result = RequestUtil.post(ParamsUtil.convertObjectToStringParams(paramMap),
									url);
						} catch (Exception e) {
							e.printStackTrace();
						}
//						System.out.println("result="+result);
						JSONObject jsonObject = JSONObject.fromObject(result);
						status = jsonObject.getString(MessageAcceptServer.getRfidTxt(MessageAcceptServer.status));
						value = jsonObject.getString(MessageAcceptServer.getRfidTxt(MessageAcceptServer.value));
//						System.out.println(status+":"+value);
						showMesg = status+":"+value;
					}
				}
				
				String path = MessageAcceptServer.getRfidTxt(MessageAcceptServer.LOCALPATH);
				mkdir(path);
				path += MessageAcceptServer.getRfidTxt(MessageAcceptServer.FILEUSER);
				createTxt(path, showMesg, charsetName);
				CallUtils.fieldSet(Boolean.TRUE);
				if(!StringUtils.isEmpty(status)){
					if(status.equals(MessageAcceptServer.getRfidTxt(MessageAcceptServer.success))){
						//校验通过打印报表
						String reportParams="id=0;"+MessageAcceptServer.getRfidTxt(MessageAcceptServer.reportParams)+"="+value;//id=0;ids=[172, 173]
						String appRoot = MessageAcceptServer.getRfidTxt(MessageAcceptServer.appRoot);
						String reportName = MessageAcceptServer.getRfidTxt(MessageAcceptServer.reportName);
						String printServiceName = MessageAcceptServer.getRfidTxt(MessageAcceptServer.printServiceName);
						QuieeDirectPrintJobJava a = new QuieeDirectPrintJobJava(appRoot, reportName, reportParams,printServiceName);
						a.print();
//						System.out.println(appRoot);
					}else if(status.equals(MessageAcceptServer.getRfidTxt(MessageAcceptServer.error))){
						
					}else{
						
					}
				}else{
					
				}
			}else{
				System.out.println(format(new Date(), dmy_hms));
			}
			out.write("ok\n");
			out.flush();
			
			try {
				in.close();
				out.close();
				socket.close();
			} catch (IOException e) {  
				e.printStackTrace();
			}
		}
		
	}
	public static void fileWriter(String f,String row,String character) throws IOException{
		File file = new File(f);
		FileWriter fw = new FileWriter(file,true);//设置成true就是追加,false就是覆盖
		fw.write(row);
//		fw.write("\r\n");
//		fw.write("ffd");
		fw.close();
	}
	public static void deleteFile(File file){ 
  	   if(file.exists()){                         
  		    if(file.isFile()){                     //判断是否是文件
  		        file.delete();                     
  		    }else if(file.isDirectory()){          //否则如果它是一个目录
  		        File files[] = file.listFiles();          
  		        for(int i=0;i<files.length;i++){           
  		            deleteFile(files[i]);             
  		        } 
  		    } 
  	   }
  	}
	public static File mkdir(String path){
    	File file =new File(path);    
    	//如果文件夹不存在则创建    
    	if  (!file .exists()  || !file .isDirectory()){       
    	    file .mkdir();    
    	}
    	return file;
    }
	public static void createTxt(String file,String row,String character){
    	OutputStreamWriter osw = null;
    	try {
    		osw = new OutputStreamWriter(new FileOutputStream(file,false),character);//设置成true就是追加,false就是覆盖
    		osw.write(row);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
	       	 if(osw!=null){ 
	       		try {
	             	osw.close();
	             } catch (IOException e) {
	                 e.printStackTrace();
	             }
	       	 }
	    }
    }
	public static String format(Date date,String format){
		DateFormat df = new SimpleDateFormat(format);
		return df.format(date);
	}
	
	protected final static String RFIDTXT = "rfidTxt.properties";
	protected final static String RFIDTXTURL = "rfidSocketTxtUrl";
	public final static String LOCALPATH = "localPath";
	public final static String FILEUSER = "fileUser";
	
	public final static String port = "port";
	public final static String beServer = "beServer";//是否经过服务验证 是/否  是:调用服务且返回yes打印报表
	public final static String appServer = "appServer";
	public final static String appRoot = "appRoot";
	public final static String reportName = "reportName";
	public final static String printServiceName = "printServiceName";
	public final static String reportParams = "reportParams";
	
	public final static String status = "status";//返回状态 是/否
	public final static String value = "value";//返回值标识  返回打印报表需要的参数
	public final static String success = "success";
	public final static String error = "error";
	public static String getRfidTxt(String keyName){
		String url = "";
		InputStream inputStream = MessageAcceptServer.class.getClassLoader().getResourceAsStream(RFIDTXT);
		Properties p = new Properties();
		try {
			p.load(inputStream);
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
		} catch (IOException e) {
			e.printStackTrace();
		}
		return url;
	}
}
