package mkoi.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkManager {

	private ServerSocket srvSocket;

	public NetworkManager() throws IOException {
		srvSocket = new ServerSocket(5555);
	}

	public void listen() throws IOException {
		try {
			while (true) {
				Socket socket = srvSocket.accept();

				if (socket != null) {
					try {
						BufferedReader input = new BufferedReader(
								new InputStreamReader(socket.getInputStream()));
						StringBuilder message = new StringBuilder();
						while (true) {
							String line = input.readLine();
							if (line == null)
								break;

							message.append(line + "\n");
						}

						System.out.println("######## PROXY #######");
						System.out.println(message.toString());

						String decrypted = WinAndChaffDecryptor.decrypt(message
								.toString());

						writeToFile(decrypted);

					} finally {
						socket.close();
					}
				}
			}
		} finally {
			srvSocket.close();
		}

	}

	private void writeToFile(String decrypted) {
		File file = new File("RECEIVED/received.txt");
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(decrypted);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// Close the writer regardless of what happens...
				writer.close();
			} catch (Exception e) {
			}
		}
	}

}
