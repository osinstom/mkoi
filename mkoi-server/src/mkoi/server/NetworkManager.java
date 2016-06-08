package mkoi.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import client.encryption.MessageObject;
import client.encryption.MessageObjectService;
import client.encryption.MessagePart;
import client.encryption.ParamService;

public class NetworkManager {

	private ServerSocket srvSocket;

	public NetworkManager() throws IOException {
		srvSocket = new ServerSocket(5555);
	}

	public void listen() throws IOException, ClassNotFoundException {
		try {
			while (true) {
				Socket socket = srvSocket.accept();

				if (socket != null) {
					try {
						ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
						MessageObject mo = (MessageObject) inStream.readObject();

						System.out.println("######## PROXY #######");
						mo.getMessageParts().forEach(messagePart -> System.out.println(messagePart.toString()));
						
						ParamService pService = new ParamService("secretKey");
						MessageObjectService mos = new MessageObjectService(pService);

//						System.out.println("########################################");
//						System.out.println("#####ODSIANE I GOTOWE##################");
//
//						mo.getMessageParts().forEach(messagePart -> {
//							if (!messagePart.isChaff()) {
//								System.out.println(messagePart.toString());
//							}
//						});
						byte[] fileBytes = mos.getFileBytes(mo);
						String winnowed = new String(fileBytes);

						writeToFile(winnowed);

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
		File file = new File("/RECEIVED/received.txt");
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
