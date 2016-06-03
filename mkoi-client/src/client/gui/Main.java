package client.gui;

import java.awt.EventQueue;

public class Main {
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainActivity window = new MainActivity();
                window.setVisible(true);
                window.setSize(500, 300);
                window.setResizable(false);
            }
        });
	}
	
}
