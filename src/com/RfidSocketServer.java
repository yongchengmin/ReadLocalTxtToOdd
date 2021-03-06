package com;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import com.callUrl.CallUtils;
import com.socket.MessageAcceptServer;

public class RfidSocketServer {
	/**"yyyy-MM-dd HH:mm:ss*/
	public static String dmy_hms = "yy-MM-dd HH:mm:ss";
	static JScrollPane jsp;
	static JTextArea jta;
	static int i = 1;
	static String temp1;
	static String temp2;
	protected final static String file_no = "File not generated";
	protected final static String CHARACTER = "utf-8";
	public static boolean send = false;
	
	public static void main(String[] args) throws IOException {
		final String port = MessageAcceptServer.getRfidTxt(MessageAcceptServer.port);
		final String source = MessageAcceptServer.getRfidTxt(MessageAcceptServer.sourceId);
		
		ServerSocket server = new ServerSocket(Integer.valueOf(port));
		Socket socket = null;
		boolean flag = true;
		
        EventQueue.invokeLater(new Runnable(){
            @Override 
            public void run(){
            	final int width = 1100;
            	int height = 620;
                final JFrame frame = new JFrame("RFID SEND "+port+" "+source);
                jta = new JTextArea();
                jta.setCaretPosition(jta.getDocument().getLength());
                jta.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,24));
//                jta.setEnabled(false);
                jta.setEditable(false);
                jsp = new JScrollPane(jta);
                frame.add(jsp);
                Timer timer = new Timer(500,new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                    	if(send){
//                            if(!file_no.equals(temp1)){
//                            	File file = new  File(MessageAcceptServer.getRfidTxt(MessageAcceptServer.LOCALPATH)
//                            			+MessageAcceptServer.getRfidTxt(MessageAcceptServer.FILEUSER));  
//    							if(file.exists()){
//    								file.delete();
//    							}
//                            }
                        	if(i>8){
                        		frame.remove(jsp);
                        		
                        		jta = null;jsp = null;
                        		jta = new JTextArea();
                                jta.setCaretPosition(jta.getDocument().getLength());
                                jta.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,24));
//                                jta.setEnabled(false);
                                jta.setEditable(false);
                                jsp = new JScrollPane(jta);
                        		
                        		frame.add(jsp);
                        		frame.validate();
                        		frame.repaint();
                        		i = 1;
                        	}
                    		temp1 =  readUsers();
                        	temp2 = "("+CallUtils.format(new Date(), dmy_hms)+")";
                            jta.append(temp1+temp2+"."+i+"\n");
                            jta.append(CallUtils.getStr(width, "-")+"\n");
                        	i++;
                        	send = false;
                    	}
                    }
                });
                timer.start();
                
//                ImageIcon icon=new ImageIcon("base.png");
                URL url = RfidSocketServer.class.getClassLoader().getResource("base.png");
                ImageIcon icon=new ImageIcon(url);
                frame.setIconImage(icon.getImage());  
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setPreferredSize(new Dimension(width,height));
                frame.pack();
                frame.setVisible(true);
            }
        });
        try {
			while(flag){
				socket = server.accept();
				MessageAcceptServer.createHandlerThread(socket);
//				send = true;
			}
		} finally{
			server.close();
		}
    }
	private static String readUsers(){
		File file = new  File(MessageAcceptServer.getRfidTxt(MessageAcceptServer.LOCALPATH)
				+MessageAcceptServer.getRfidTxt(MessageAcceptServer.FILEUSER));  
        if(!file.exists()){
        	return file_no;
        }
        String d = CallUtils.readStrTxt(file,CHARACTER);
		return d;
	}
}
