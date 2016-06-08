package client.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;

import javax.swing.JLabel;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import org.apache.commons.codec.Charsets;

import client.encryption.MessageObject;
import client.encryption.MessageObjectService;
import client.encryption.ParamService;

public class MainActivity extends JFrame {
	
	private static final Pattern IP_ADDR_PATTERN = Pattern.compile(
	        "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
	private static final Pattern IS_INTEGER_PATTERN = Pattern.compile("\\d*");
	private static final Pattern RATIO_PATTERN = Pattern.compile("\\d*\\/\\d*");
	
	private JTextField tfIpAddr;
	private JTextField tfDataLength;
	private JTextField tfRatio;
	private JCheckBox transfOn;
	private JLabel lblKeyValue;
	private JButton btnChooseFile;
	private JButton btnSendFile;
	private JLabel lblChoosedFile;
	
	private NetworkClient ntwClient;
	
	private String textData;

	public MainActivity() {
		setTitle("Bezpieczny transfer plik\u00F3w - aplikacja klienta");

		
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 242, 0, 0, 13, 241, 0 };
		gbl_panel.rowHeights = new int[] { 30, 0, 0 };
		gbl_panel.columnWeights = new double[] { 1.0, 0.0, 0.0, 0.0, 1.0,
				Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		JPanel panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.insets = new Insets(0, 0, 0, 5);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 1;
		panel.add(panel_2, gbc_panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] { 107, 0, 0 };
		gbl_panel_2.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel_2.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel_2.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, Double.MIN_VALUE };
		panel_2.setLayout(gbl_panel_2);

		JLabel lblAdresIpSerwera = new JLabel("Adres IP serwera: ");
		GridBagConstraints gbc_lblAdresIpSerwera = new GridBagConstraints();
		gbc_lblAdresIpSerwera.insets = new Insets(0, 0, 5, 5);
		gbc_lblAdresIpSerwera.anchor = GridBagConstraints.EAST;
		gbc_lblAdresIpSerwera.gridx = 0;
		gbc_lblAdresIpSerwera.gridy = 1;
		panel_2.add(lblAdresIpSerwera, gbc_lblAdresIpSerwera);

		tfIpAddr = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 1;
		panel_2.add(tfIpAddr, gbc_textField);
		tfIpAddr.setColumns(10);

		btnChooseFile = new JButton("Wybierz plik..");
		GridBagConstraints gbc_btnWybierzPlik = new GridBagConstraints();
		gbc_btnWybierzPlik.insets = new Insets(0, 0, 5, 0);
		gbc_btnWybierzPlik.gridx = 1;
		gbc_btnWybierzPlik.gridy = 3;
		panel_2.add(btnChooseFile, gbc_btnWybierzPlik);

		JLabel lblWybranyPlik = new JLabel("Wybrany plik: ");
		GridBagConstraints gbc_lblWybranyPlik = new GridBagConstraints();
		gbc_lblWybranyPlik.insets = new Insets(0, 0, 5, 5);
		gbc_lblWybranyPlik.gridx = 0;
		gbc_lblWybranyPlik.gridy = 4;
		panel_2.add(lblWybranyPlik, gbc_lblWybranyPlik);

		lblChoosedFile = new JLabel("");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.insets = new Insets(0, 0, 5, 0);
		gbc_label.gridx = 1;
		gbc_label.gridy = 4;
		panel_2.add(lblChoosedFile, gbc_label);

		btnSendFile = new JButton("Wy\u015Blij plik");
		GridBagConstraints gbc_btnWylijPlik = new GridBagConstraints();
		gbc_btnWylijPlik.gridx = 1;
		gbc_btnWylijPlik.gridy = 6;
		panel_2.add(btnSendFile, gbc_btnWylijPlik);

		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 4;
		gbc_panel_1.gridy = 1;
		panel.add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] { 0, 0, 0 };
		gbl_panel_1.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel_1.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_panel_1.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, Double.MIN_VALUE };
		panel_1.setLayout(gbl_panel_1);

		JLabel lblParametry = new JLabel("PARAMETRY");
		GridBagConstraints gbc_lblParametry = new GridBagConstraints();
		gbc_lblParametry.insets = new Insets(0, 0, 5, 5);
		gbc_lblParametry.gridx = 0;
		gbc_lblParametry.gridy = 0;
		panel_1.add(lblParametry, gbc_lblParametry);

		JLabel label_2 = new JLabel("");
		GridBagConstraints gbc_label_2 = new GridBagConstraints();
		gbc_label_2.insets = new Insets(0, 0, 5, 5);
		gbc_label_2.gridx = 0;
		gbc_label_2.gridy = 1;
		panel_1.add(label_2, gbc_label_2);

		JLabel lblDugocBlokuDanych = new JLabel(
				"D\u0142ugo\u015Bc bloku danych: ");
		GridBagConstraints gbc_lblDugocBlokuDanych = new GridBagConstraints();
		gbc_lblDugocBlokuDanych.insets = new Insets(0, 0, 5, 5);
		gbc_lblDugocBlokuDanych.gridx = 0;
		gbc_lblDugocBlokuDanych.gridy = 2;
		panel_1.add(lblDugocBlokuDanych, gbc_lblDugocBlokuDanych);

		tfDataLength = new JTextField();
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.insets = new Insets(0, 0, 5, 0);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 1;
		gbc_textField_1.gridy = 2;
		panel_1.add(tfDataLength, gbc_textField_1);
		tfDataLength.setColumns(10);

		JLabel lblStosunek = new JLabel("Stosunek (dobre/z\u0142e):");
		GridBagConstraints gbc_lblStosunek = new GridBagConstraints();
		gbc_lblStosunek.insets = new Insets(0, 0, 5, 5);
		gbc_lblStosunek.gridx = 0;
		gbc_lblStosunek.gridy = 3;
		panel_1.add(lblStosunek, gbc_lblStosunek);

		tfRatio = new JTextField();
		GridBagConstraints gbc_textField_2 = new GridBagConstraints();
		gbc_textField_2.insets = new Insets(0, 0, 5, 0);
		gbc_textField_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_2.gridx = 1;
		gbc_textField_2.gridy = 3;
		panel_1.add(tfRatio, gbc_textField_2);
		tfRatio.setColumns(10);

		JLabel lblTransformata = new JLabel("Transformata: ");
		GridBagConstraints gbc_lblTransformata = new GridBagConstraints();
		gbc_lblTransformata.insets = new Insets(0, 0, 5, 5);
		gbc_lblTransformata.gridx = 0;
		gbc_lblTransformata.gridy = 4;
		panel_1.add(lblTransformata, gbc_lblTransformata);

		transfOn = new JCheckBox("");
		GridBagConstraints gbc_checkBox = new GridBagConstraints();
		gbc_checkBox.insets = new Insets(0, 0, 5, 0);
		gbc_checkBox.gridx = 1;
		gbc_checkBox.gridy = 4;
		panel_1.add(transfOn, gbc_checkBox);

		JLabel lblKlucz = new JLabel("Klucz:");
		GridBagConstraints gbc_lblKlucz = new GridBagConstraints();
		gbc_lblKlucz.insets = new Insets(0, 0, 0, 5);
		gbc_lblKlucz.gridx = 0;
		gbc_lblKlucz.gridy = 6;
		panel_1.add(lblKlucz, gbc_lblKlucz);

		lblKeyValue = new JLabel("");
		GridBagConstraints gbc_label_1 = new GridBagConstraints();
		gbc_label_1.gridx = 1;
		gbc_label_1.gridy = 6;
		panel_1.add(lblKeyValue, gbc_label_1);
		
		tfIpAddr.setText("127.0.0.1");
		
		// adding action handlers
		ActionHandler handler = new ActionHandler(this);
		btnSendFile.addActionListener(handler);
		btnChooseFile.addActionListener(handler);
		transfOn.addActionListener(handler);
		
		// init network client
		ntwClient = new NetworkClient();
	}
	
	public boolean validateFields() {
		
		boolean ipAddr = IP_ADDR_PATTERN.matcher(tfIpAddr.getText().trim()).matches();
		
		boolean dataLen = IS_INTEGER_PATTERN.matcher(tfDataLength.getText().trim()).matches();
		boolean ratio = RATIO_PATTERN.matcher(tfRatio.getText().trim()).matches();
		boolean isFileChoosed = !lblChoosedFile.getText().trim().isEmpty();
		
		return ipAddr && dataLen && ratio && isFileChoosed;
	}

	private class ActionHandler implements ActionListener {

		private JFrame window;

		public ActionHandler(MainActivity mainActivity) {
			window = mainActivity;
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			Object source = e.getSource();

			if (source == btnChooseFile) {

				JFileChooser fileChooser = new JFileChooser();
				int result = fileChooser.showOpenDialog(window);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					try {
						Scanner sc = new Scanner(selectedFile);
						StringBuilder str = new StringBuilder();
						while(sc.hasNextLine()) {
							str.append(sc.nextLine() + "\n");
						}
						textData = str.toString();
					} catch (FileNotFoundException e1) {
						JOptionPane.showMessageDialog(window, "Error: " + e1.getMessage());
					}

					lblChoosedFile.setText(selectedFile.getName());
					
				}

			} else if (source == btnSendFile) {
				System.out.println("Send file clicked");
				
				if(!validateFields()) {
					JOptionPane.showMessageDialog(window, "Wrong value");
					return;
				}
					
				
				String ipAddr = tfIpAddr.getText().trim();
				Integer dataLen = Integer.parseInt(tfDataLength.getText().trim());
				boolean isTransformationOn = transfOn.isSelected();
				String[] ratio = tfRatio.getText().trim().split("/");
				
				int goodNb = Integer.parseInt(ratio[0].trim());
				int badNb = Integer.parseInt(ratio[1].trim());

				byte[] fileBytes = new byte[0];
				try {
					fileBytes = textData.getBytes("UTF-8");
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}

				// winnowing and chaffing
				
				ParamService pService = new ParamService("secretKey", dataLen, badNb);
				MessageObjectService moService = new MessageObjectService(pService);
				MessageObject mo = moService.createMessageObject(fileBytes, isTransformationOn);
				
				// sending file
				
				try {
					ntwClient.send(ipAddr, mo);
				} catch (UnknownHostException e1) {
					JOptionPane.showMessageDialog(window, "Error: " + e1.getMessage());
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(window, "Error: " + e1.getMessage());
				}
				
				
			} 

		}

	}

}
