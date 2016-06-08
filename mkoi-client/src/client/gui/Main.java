package client.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;

public class Main {
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainActivity window = new MainActivity();
                window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                window.setVisible(true);
                window.setSize(443, 450);
                window.setResizable(false);
            }
        });
	}
	
}
