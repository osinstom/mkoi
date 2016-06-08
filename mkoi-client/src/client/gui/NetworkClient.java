package client.gui;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import client.encryption.MessageObject;

public class NetworkClient {

	public void send(String serverAddr, MessageObject obj)
			throws UnknownHostException, IOException {
		Socket socket = new Socket(serverAddr, 5555);

		ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

		try {
			outputStream.writeObject(obj);
		} catch (IOException e) {
			throw e;
		} finally {
			outputStream.close();
			socket.close();
		}

	}

}
