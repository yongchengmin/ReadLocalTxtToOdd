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
import java.util.Properties;

//DataSender.java is client.
public class MessageAcceptServer {
	public static String dmy_hms = "yyyy-MM-dd HH:mm:ss";
	public static String charsetName = "UTF-8";
//	public static void main(String[] args) throws IOException {
//		ServerSocket server = new ServerSocket(8898);
//		Socket socket = null;
//		boolean flag = true;
//		try {
//			while(flag){
//				socket = server.accept();
//				createHandlerThread(socket);
//			}
//		} finally{
//			server.close();
//		}
//	}
	
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
				System.out.println(getLine);
				
				String path = MessageAcceptServer.getRfidTxt(MessageAcceptServer.LOCALPATH);
				mkdir(path);
				path += MessageAcceptServer.getRfidTxt(MessageAcceptServer.FILEUSER);
				createTxt(path, getLine, charsetName);
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
	             	osw.close();//osw.close();
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