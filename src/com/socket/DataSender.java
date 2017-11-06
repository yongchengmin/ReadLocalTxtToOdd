package com.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class DataSender {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws UnknownHostException, IOException {
		Socket socket = new Socket("127.0.0.1",8898);
		//向服务器端程序发送数据
		OutputStream outputStream = socket.getOutputStream();
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
		BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
		bufferedWriter.write("hello server, 你能 receiver my data?");
		bufferedWriter.flush();
		bufferedWriter.close();
		socket.close();
		System.out.println("finish send data!!");
		/*Socket socket = new Socket("127.0.0.1", 3320);//UDPServer
		//socket.setSoTimeout(10000);//设置超时时间
		BufferedReader sin = new BufferedReader(new InputStreamReader(System.in));
		PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"));//获取socket的发送流
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));//获取socket的接收流
		String readline = null;
		readline =sin.readLine();
		while(!readline.equals("byebye")) {//以byebye终止连接
			out.println(readline);
			out.flush();
			System.out.println("Client:" +readline);
			System.out.println("Server:" +in.readLine());
			readline = sin.readLine();
		}
		out.close();
		in.close();
		socket.close();*/
	}

}
