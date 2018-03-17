package com.test;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class JLabelTest {

	/**
	 * @param args
	 */
	public static void main(String[] args){
		final JFrame frame = new JFrame();
		frame.getContentPane().setBackground(Color.GREEN);
		JLabel label = new JLabel("123");
		label.setOpaque(false);
		frame.getContentPane().add(label,BorderLayout.CENTER);
		label = new JLabel("12323");
		label.setOpaque(false);
		frame.getContentPane().add(label,BorderLayout.EAST);
		frame.setVisible(true);
	}

}
