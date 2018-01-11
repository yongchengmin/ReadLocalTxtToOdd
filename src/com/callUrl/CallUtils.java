package com.callUrl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;

import com.RfidSocketServer;

import net.sf.json.JSONObject;

public class CallUtils {
	public static String enter = "\r\n";
	public static String YESE = "yes";
	public static String NO = "no";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	@SuppressWarnings("rawtypes")
	public static String getCreateJson(Map map){
		JSONObject jsonObject = new JSONObject();
		jsonObject.putAll(map);

		return jsonObject.toString();
		//{"key1":"value01","key2":"value02","key3":"value03"}
	}
	public static String toUnicodeString(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c >= 0 && c <= 255) {
				sb.append(c);
			} else {
				sb.append("\\u"+Integer.toHexString(c));
			}
		}
		return sb.toString();
	}
	public static String decodeUnicode(String theString){
		char aChar;
		int len= theString.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for(int x=0;x<len;){
			aChar = theString.charAt(x++);
			if(aChar=='\\'){
				aChar=theString.charAt(x++);
				if(aChar=='u'){
					int value=0;
			
					for(int i=0;i<4;i++){
						aChar = theString.charAt(x++);
						switch(aChar){
							case'0':
							case'1':
							case'2':
							case'3':
							case'4':
							case'5':
							case'6':
							case'7':
							case'8':
							case'9':
								value=(value<<4)+aChar-'0';
								break;
							case'a':
							case'b':
							case'c':
							case'd':
							case'e':
							case'f':
								value=(value<<4)+10+aChar-'a';
								break;
							case'A':
							case'B':
							case'C':
							case'D':
							case'E':
							case'F':
								value=(value<<4)+10+aChar-'A';
								break;
							default:
								throw new IllegalArgumentException("Malformed\\uxxxxencoding.");
						}
			
					}
					outBuffer.append((char)value);
				}else{
					if(aChar=='t')
						aChar='\t';
					else if(aChar=='r')
						aChar='\r';
					else if(aChar=='n')
						aChar='\n';
					else if(aChar=='f')
						aChar='\f';
					outBuffer.append(aChar);
				}
			} else {
				outBuffer.append(aChar);
			}
		}
		return outBuffer.toString();
	}
	public static String readStrTxt(File file,String encoding){
    	//jt.readTxt(new File("C:/Users/Administrator/Desktop/aaaaa.txt"));
    	BufferedReader br = null;
    	String sql = "";
    	StringBuffer sb = new StringBuffer();
    	try {
    		InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
    		br = new BufferedReader(read);
    		String temp = null;
    		while ((temp = br.readLine()) != null) {
    			if("".equals(temp)){
    				continue;
    			}
    			StringTokenizer st = new StringTokenizer(temp);
    			StringBuffer sbTemp = new StringBuffer();
    			while(st.hasMoreElements()){
    				String num = st.nextToken("\t").trim();
    				sbTemp.append(num);
    			}
    			if(sbTemp.length()>0){
    				sb.append(sbTemp+enter);
    			}
    		}
    		sql = substringBeforeLast(sb.toString(), enter);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(null != br){
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sql;
    }
	 public static void createTxt(String file,StringBuffer row,String character){
    	OutputStreamWriter osw = null;
    	try {
    		osw = new OutputStreamWriter(new FileOutputStream(file,true),character);
    		osw.write(row.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
	       	 if(osw!=null)//if(osw!=null)
	             try {
	             	osw.close();//osw.close();
	             } catch (IOException e) {
	                 e.printStackTrace();
	             }
	    }
    }
	public static String format(Date date,String format) {
		DateFormat df = new SimpleDateFormat(format);
		return df.format(date);
	}
	public static String substringBeforeLast(String str, String separator){
		if ((isEmpty(str)) || (isEmpty(separator))) {
	      return str;
	    }
	    int pos = str.lastIndexOf(separator);
	    if (pos == -1) {
	      return str;
	    }
	    return str.substring(0, pos);
	}
	public static boolean isEmpty(String str){
		return (str == null) || (str.length() == 0);
	}
	public static String localIp(){
		//服务的地址
		InetAddress address;
		String localIp = "";
		try {
			address = InetAddress.getLocalHost();
			localIp = address.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return localIp;
	}
	
	public static void fieldSet(Boolean isSend){
		try {
			Object o=RfidSocketServer.class.newInstance();//获取对象
			Field f=RfidSocketServer.class.getField("send");//根据key获取参数
			f.set(o, isSend);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}
	
	//参数 num:字符串长度  str:需要显示的字符
	public static String getStr(int num, String str){
		StringBuffer sb = new StringBuffer("");
		for(int i=0;i<num;i++){
		   sb.append(str);
		}
		return sb.toString();
	}
}
