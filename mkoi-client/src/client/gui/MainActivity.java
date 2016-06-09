package client.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import client.encryption.MessageObject;
import client.encryption.MessageObjectService;
import client.encryption.ParamService;

public class MainActivity extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7604652746553104171L;
	
	private static final Pattern IP_ADDR_PATTERN = Pattern
			.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
	private static final Pattern IS_INTEGER_PATTERN = Pattern.compile("\\d*");
	private static final Pattern RATIO_PATTERN = Pattern.compile("\\d*\\/\\d*");

	private JTextField tfIpAddr;
	private JTextField tfKey;
	private JTextField tfLength;
	private JTextField tfRatio;

	private JButton btnChooseFile;
	private JButton btnSendFile;
	private JLabel lblChoosedFile;
	private JCheckBox chckbxTransf;

	private NetworkClient ntwClient;

	private String textData;

	public MainActivity() {
		setTitle("Bezpieczny transfer plik\u00F3w - aplikacja klienta");

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);

		JLabel lblAdresIpSerwera = new JLabel("Adres IP serwera: ");
		lblAdresIpSerwera.setBounds(43, 39, 134, 15);
		panel.add(lblAdresIpSerwera);

		tfIpAddr = new JTextField();
		tfIpAddr.setBounds(178, 37, 114, 19);
		panel.add(tfIpAddr);
		tfIpAddr.setColumns(10);

		JLabel lblKlucz = new JLabel("Klucz: ");
		lblKlucz.setBounds(43, 78, 70, 15);
		panel.add(lblKlucz);

		tfKey = new JTextField();
		tfKey.setBounds(178, 76, 114, 19);
		panel.add(tfKey);
		tfKey.setColumns(10);

		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(43, 129, 349, 163);
		panel.add(layeredPane);

		JLabel lblParametrywinnowing = new JLabel(
				"Parametry \"winnowing & chaffing\"");
		lblParametrywinnowing.setBounds(0, 0, 242, 26);
		layeredPane.add(lblParametrywinnowing);

		JLabel lblDugo = new JLabel("Dlugosc bloku danych: ");
		lblDugo.setBounds(10, 35, 163, 15);
		layeredPane.add(lblDugo);

		tfLength = new JTextField();
		tfLength.setBounds(177, 33, 114, 19);
		layeredPane.add(tfLength);
		tfLength.setColumns(10);

		tfRatio = new JTextField();
		tfRatio.setBounds(177, 64, 114, 19);
		layeredPane.add(tfRatio);
		tfRatio.setColumns(10);

		JLabel lblStosunek = new JLabel("Stosunek: ");
		lblStosunek.setBounds(12, 66, 105, 15);
		layeredPane.add(lblStosunek);

		chckbxTransf = new JCheckBox("Transformata");
		chckbxTransf.setBounds(177, 97, 129, 23);
		layeredPane.add(chckbxTransf);

		btnChooseFile = new JButton("Wybierz plik..");
		btnChooseFile.setBounds(43, 314, 134, 25);
		panel.add(btnChooseFile);

		btnSendFile = new JButton("Wyslij plik..");
		btnSendFile.setBounds(258, 314, 134, 25);
		panel.add(btnSendFile);

		JLabel lblWybranyPlik = new JLabel("Wybrany plik: ");
		lblWybranyPlik.setBounds(43, 351, 101, 15);
		panel.add(lblWybranyPlik);

		lblChoosedFile = new JLabel("");
		lblChoosedFile.setBounds(156, 351, 236, 15);
		panel.add(lblChoosedFile);

		tfIpAddr.setText("127.0.0.1");

		// adding action handlers
		ActionHandler handler = new ActionHandler(this);
		btnSendFile.addActionListener(handler);
		btnChooseFile.addActionListener(handler);

		// init network client
		ntwClient = new NetworkClient();
	}

	public boolean validateFields() {

		boolean ipAddr = IP_ADDR_PATTERN.matcher(tfIpAddr.getText().trim())
				.matches();

		boolean dataLen = IS_INTEGER_PATTERN.matcher(tfLength.getText().trim())
				.matches();
		boolean ratio = RATIO_PATTERN.matcher(tfRatio.getText().trim())
				.matches();
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
						while (sc.hasNextLine()) {
							str.append(sc.nextLine() + "\n");
						}
						textData = str.toString();
					} catch (FileNotFoundException e1) {
						JOptionPane.showMessageDialog(window,
								"Error: " + e1.getMessage());
					}

					lblChoosedFile.setText(selectedFile.getName());

				}

			} else if (source == btnSendFile) {
				System.out.println("Send file clicked");

				if (!validateFields()) {
					JOptionPane.showMessageDialog(window, "Wrong value");
					return;
				}

				String ipAddr = tfIpAddr.getText().trim();
				Integer dataLen = Integer.parseInt(tfLength.getText().trim());
				boolean isTransformationOn = chckbxTransf.isSelected();
				String[] ratioStr = tfRatio.getText().trim().split("/");

				BigDecimal goodNb = BigDecimal.valueOf(Integer.parseInt(ratioStr[0].trim()));
				BigDecimal badNb = BigDecimal.valueOf(Integer.parseInt(ratioStr[1].trim()));
				if (BigDecimal.ZERO.equals(badNb)) {
					badNb = BigDecimal.ONE;
				}
				BigDecimal res = goodNb.divide(badNb, 0, BigDecimal.ROUND_CEILING);

				byte[] fileBytes = new byte[0];
				try {
					fileBytes = textData.getBytes("UTF-8");
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				int ratio = BigDecimal.valueOf(fileBytes.length).divide(res, 0, BigDecimal.ROUND_CEILING).intValue();
				// winnowing and chaffing

//				ParamService pService = new ParamService(tfKey.getText().trim(), dataLen,
//						ratio);
				ParamService pService = new ParamService("mkoi_16L", dataLen,
						ratio);
				MessageObjectService moService = new MessageObjectService(
						pService);
				MessageObject mo = moService.createMessageObject(fileBytes,
						isTransformationOn);

				// sending file

				try {
					ntwClient.send(ipAddr, mo);
				} catch (UnknownHostException e1) {
					JOptionPane.showMessageDialog(window,
							"Error: " + e1.getMessage());
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(window,
							"Error: " + e1.getMessage());
				}

			}

		}

	}

}
