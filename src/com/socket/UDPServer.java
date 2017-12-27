package com.socket;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
//udp不需要和客户端连接，服务器监听A端口，客户端发送数据到A端口，服务器就会接收到。

@SuppressWarnings("unused")
public class UDPServer {
    public static void main(String args[]) throws Exception
    {
        //监听9876端口
       DatagramSocket serverSocket = new DatagramSocket(65177);//9876
          byte[] receiveData = new byte[1024];
          byte[] sendData = new byte[1024];
          while(true)
             {
                //构造数据包接收数据
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                //接收数据
                serverSocket.receive(receivePacket);
                //解析数据
                String sentence = new String( receivePacket.getData());
                System.out.println("RECEIVED: " + sentence);
             }
    }
}
