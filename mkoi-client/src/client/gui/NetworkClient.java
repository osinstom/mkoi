package client.gui;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class NetworkClient {

	public void send(String serverAddr, String message)
			throws UnknownHostException, IOException {
		Socket socket = new Socket(serverAddr, 5555);

		DataOutputStream os = new DataOutputStream(socket.getOutputStream());

		try {
			os.writeUTF(message);
		} catch (IOException e) {
			throw e;
		} finally {
			os.close();
			socket.close();
		}

	}

}
