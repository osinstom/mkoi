package mkoi.server;

import java.io.IOException;

public class Main {

	public static void main(String[] args) {

		NetworkManager netMgr = null;
		try {
			netMgr = new NetworkManager();
			System.out.println("Server running");
		} catch (IOException e) {
			System.out.println("NET MGR not started");
		}
		
		try {
			if(netMgr!=null) 
				netMgr.listen();
			
		} catch (IOException e) {
			System.out.println("Error while receivng message");
			System.out.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

}
