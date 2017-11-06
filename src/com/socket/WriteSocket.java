package com.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class WriteSocket {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws UnknownHostException, IOException {
		/*//创建一个socket对象，8888为监听端口
        ServerSocket serverSocket = new ServerSocket(1025);
        //socket对象调用accept方法，等待连接请求
        Socket socket = serverSocket.accept();
        System.out.println("000000000000000");
        OutputStream os = socket.getOutputStream();
        DataOutputStream dos=new DataOutputStream(os);
        dos.writeUTF("hello world\r\n\r\n");
        dos.flush();
        System.out.println("11111111111111");
        dos.close();
        System.out.println("22222222222222");
        os.close();
        System.out.println("isClosed:"+socket.isClosed());
        System.out.println("isConnected:"+socket.isConnected());
        System.out.println("isInputShutdown:"+socket.isInputShutdown());
        System.out.println("isOutputShutdown:"+socket.isOutputShutdown());//26
        
        //打开输入流
        InputStream is = socket.getInputStream();
        //封装输入流
        DataInputStream dis = new DataInputStream(is);

        String info = dis.readUTF();
        System.out.println("对方说: " + info);
        //关闭输入流
        dis.close();
        //关闭socket对象
        socket.close();
        serverSocket.close();*/
        
        
        
		Socket socket = new Socket("127.0.0.1", 40000);
		System.out.println(socket.isClosed());
		System.out.println(socket.isConnected());
		OutputStream out = socket.getOutputStream();
		OutputStreamWriter opsw = new OutputStreamWriter(out);
		BufferedWriter bw = new BufferedWriter(opsw);
		bw.write("hello world\r\n\r\n");
		bw.flush();
		
		InputStream ips = socket.getInputStream();
		InputStreamReader ipsr = new InputStreamReader(ips);
		BufferedReader br = new BufferedReader(ipsr);
		String s = "";
		while((s = br.readLine()) !=null){
			System.out.println(s);
		}
		socket.close();
	}

}
