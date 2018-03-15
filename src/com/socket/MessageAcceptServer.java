package com.socket;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.conn.HttpHostConnectException;

import com.callUrl.CallUtils;
import com.callUrl.ParamsUtil;
import com.callUrl.RequestUtil;
import com.print.QuieeDirectPrintJobJava;

//DataSender.java is client.
public class MessageAcceptServer {
	public static String dmy_hms = "yyyy-MM-dd HH:mm:ss";
	public static String charsetName = "UTF-8";
	
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
				String id = "";
				String showMesg = getLine;
				//是否验证信息 如果无则信息往这里写入,如果有则 产品验证完成之后,要将验证信息源文件删除,确保下一次不重读
				//no-不做校验,dirver.txt,other1.txt,other2.txt,...-校验文件信息用英文逗号分隔(将来)
				Boolean bego = true;
				String checkTxt = MessageAcceptServer.getRfidTxt(MessageAcceptServer.CHECK_TXT);
				String checkPath = MessageAcceptServer.getRfidTxt(MessageAcceptServer.CHECK_PATH);
				mkdir(checkPath);
				checkPath += checkTxt;
				if(!StringUtils.isEmpty(checkTxt) && !CallUtils.NO.equals(checkTxt)){
					List<String> list = null;
					try {
						list = fileToList(checkPath);
					} catch (IOException e) {
					}
					if(list==null || list.size()<=0){
						createTxt(checkPath, showMesg, charsetName);
						bego = false;
					}else{
						getLine += ","+StringUtils.substringBetween(list.toString(), "[", "]");
					}
				}
				JSONObject jsonObject = null;
				if(bego){
					//服务器校验
					String url = MessageAcceptServer.getRfidTxt(MessageAcceptServer.appServer);
					String warehouseCode = MessageAcceptServer.getRfidTxt(MessageAcceptServer.warehouseCode);
					String source = MessageAcceptServer.getRfidTxt(MessageAcceptServer.sourceId);
					Map<String, Object> paramMap = new HashMap<String, Object>();
					paramMap.put("data", getLine);
					paramMap.put("warehouseCode", warehouseCode);
					paramMap.put("source", source);
					String result = null;
					Boolean beError = false;
					try {
						result = RequestUtil.post(ParamsUtil.convertObjectToStringParams(paramMap),
								url);
					} catch (Exception e) {
						beError = true;
						result = e.getMessage();
					}finally{
						deleteFile(new File(checkPath));
					}
					if(!beError){
						try {
							jsonObject = JSONObject.fromObject(result);
						}catch (Exception e) {
							beError = true;
							result = e.getMessage();
						}
						if(!beError){
							status = jsonObject.getString(MessageAcceptServer.getRfidTxt(MessageAcceptServer.status));
							value = jsonObject.getString(MessageAcceptServer.getRfidTxt(MessageAcceptServer.value));
							showMesg = status+":"+value;
						}
					}
					if(beError){
						showMesg = result;
					}
				}else{
					showMesg = getLine;
				}
				String path = MessageAcceptServer.getRfidTxt(MessageAcceptServer.LOCALPATH);
				mkdir(path);
				path += MessageAcceptServer.getRfidTxt(MessageAcceptServer.FILEUSER);
				createTxt(path, showMesg, charsetName);
				CallUtils.fieldSet(Boolean.TRUE);
				if(!StringUtils.isEmpty(status)){
					if(status.equals(MessageAcceptServer.getRfidTxt(MessageAcceptServer.success))){
						String beServer = MessageAcceptServer.getRfidTxt(MessageAcceptServer.bePrint);
						if(CallUtils.YESE.equals(beServer)){
							id = jsonObject.getString(MessageAcceptServer.getRfidTxt(MessageAcceptServer.reportParams));
							//校验通过打印报表
							String reportParams="id=0;"+MessageAcceptServer.getRfidTxt(MessageAcceptServer.reportParams)+"="+id;//id=0;ids=[172, 173]
							String appRoot = MessageAcceptServer.getRfidTxt(MessageAcceptServer.appRoot);
							String reportName = MessageAcceptServer.getRfidTxt(MessageAcceptServer.reportName);
							String printServiceName = MessageAcceptServer.getRfidTxt(MessageAcceptServer.printServiceName);
							QuieeDirectPrintJobJava a = new QuieeDirectPrintJobJava(appRoot, reportName, reportParams,printServiceName);
							a.print();
//							System.out.println(appRoot);
						}
					}else if(status.equals(MessageAcceptServer.getRfidTxt(MessageAcceptServer.error))){
						
					}
				}
			}else{
//				System.out.println(format(new Date(), dmy_hms));
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
	public static List<String> fileToList(String pathname) throws IOException
	{
		List<String> list = new ArrayList<String>();
		String lineinfo="";
		File f=new File(pathname);
		if(!f.exists()){
			return null;
		}
		BufferedReader br=new BufferedReader(new FileReader(f));
		while((lineinfo = br.readLine()) != null)
		{
			list.add(lineinfo.trim());
		}
	    br.close();
		return list;
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
	public final static String bePrint = "bePrint";
	public final static String appServer = "appServer";
	public final static String appRoot = "appRoot";
	public final static String reportName = "reportName";
	public final static String printServiceName = "printServiceName";
	public final static String reportParams = "reportParams";
	public final static String warehouseCode = "warehouseCode";//指明入库编码
	public final static String sourceId = "sourceId";//发送源  asn:入库,pick:出库
	
	public final static String CHECK_TXT = "checkTxt";
	public final static String CHECK_PATH = "checkPath";
	
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
