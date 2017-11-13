package com.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
//DataSender.java(client)
//DataAcceptServer.java(server)
//TcpServer.java(server)
public class DataAcceptServer {
	public static String dmy_hms = "yyyy-MM-dd HH:mm:ss";
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		ServerSocket server = new ServerSocket(8898);// 设置server的端口
		Socket socket = null;
		int number = 0;
		boolean flag = true;
		try {
			while(flag){
				socket = server.accept();//监听连接，方法阻塞，能够建立多个长连接
				number++;
				createHandlerThread(socket, number);
			}
		} finally{
			server.close();
		}
	}
	
	private static void createHandlerThread(Socket socket, int number){
		DataAcceptServer das = new DataAcceptServer();
		Handler handler = das.new Handler(socket, number);
		Thread t = new Thread(handler);
		t.start();
	}
	public class Handler implements Runnable{
		private Socket socket = null;
		private int number = 0;
		public Handler(Socket socket, int number){
			this.socket = socket;
			this.number = number;
		}
		@Override
		public void run() {
			System.out.println("get the connetion with client:" + number);
			BufferedReader in = null;
			PrintWriter out = null;
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));//获取socket的接收流
				out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));//获取socket的发送流
			} catch (IOException e) {
				e.printStackTrace();
			}
			String getline = null;
			boolean end = false;
			while(!end){
				try {
					getline = in.readLine();
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
				if(getline==null){
					System.out.println("client:"+number+" "+format(new Date(), dmy_hms));
				}else{
					System.out.println("get message from client:" + number + " ("+getline+")");
				}
				out.write("ok\n");//返回ok
				out.flush();
				if(getline!=null && getline.equals("byebye")){
					end = true;
				}
				try {
					Thread.sleep(1000*2);//2'
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			try {
				in.close();
				out.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("release the connetion with client:" + number);
		}
		
	} 
	public static String format(Date date,String format) {
		DateFormat df = new SimpleDateFormat(format);
		return df.format(date);
	}
}
