package com;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.lang.StringUtils;

import com.callUrl.CallUrl;
import com.callUrl.CallUtils;

public class RfidTxt {
	/**"yyyy-MM-dd HH:mm:ss*/
	public static String dmy_hms = "yyyy-MM-dd HH:mm:ss";
	static JScrollPane jsp;
	static JTextArea jta;
	static String temp1;
	static String temp2;
	static int i = 1;
	protected final static String CHARACTER = "utf-8";
	protected final static String file_no = "File not generated";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable(){
            @Override 
            public void run(){
                final JFrame frame = new JFrame("RFID TXT");
                jta = new JTextArea();
                jta.setCaretPosition(jta.getDocument().getLength());
                jta.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,24));
                jsp = new JScrollPane(jta);
                frame.add(jsp);
                Timer timer = new Timer(500,new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                    	temp1 =  readUsers();
                    	temp2 = "_"+CallUtils.format(new Date(), dmy_hms);
                        jta.append(temp1+temp2+"_"+i+"\n");
                        //调用call
                        if(!file_no.equals(temp1)){
                        	try {
								CallUrl.sendMessageToOdd(jsonV(temp1));
								File file = new  File(CallUrl.getRfidTxt(CallUrl.LOCALPATH)+CallUrl.getRfidTxt(CallUrl.FILEUSER));  
						        if(file.exists()){
						        	file.delete();
						        }
							} catch (HttpException e1) {
								jta.append(e1.getMessage()+"\n");
								e1.printStackTrace();
							} catch (IOException e1) {
								jta.append(e1.getMessage()+"\n");
								e1.printStackTrace();
							}
                        }
                    	if(i>20){
                    		frame.remove(jsp);
                    		
                    		jta = null;jsp = null;
                    		jta = new JTextArea();
                            jta.setCaretPosition(jta.getDocument().getLength());
                            jta.setFont(new Font(Font.DIALOG_INPUT,Font.BOLD,24));
                            jsp = new JScrollPane(jta);
                    		
                    		frame.add(jsp);
                    		frame.validate();
                    		frame.repaint();
                    		i = 0;
                    	}
                    	i++;
                    }
                });
                timer.start();
                
                ImageIcon icon=new ImageIcon("base.png");
                frame.setIconImage(icon.getImage());  
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setPreferredSize(new Dimension(700,600));
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
	private static String jsonV(String d){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(CallUrl.getRfidTxt(CallUrl.JSONKEY), StringUtils.isBlank(d)?"":CallUtils.toUnicodeString(d.trim()));
		String data = CallUtils.getCreateJson(map);
		return data;
	}
	private static String readUsers(){
//		String d = readByte(localPath, fileUser);
		File file = new  File(CallUrl.getRfidTxt(CallUrl.LOCALPATH)+CallUrl.getRfidTxt(CallUrl.FILEUSER));  
        if(!file.exists()){
        	return file_no;
        }
        String d = CallUtils.readStrTxt(file,CHARACTER);
		return d;
	}
}
