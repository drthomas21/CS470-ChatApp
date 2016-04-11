package core.client;

import java.net.Socket;

public class ClientSocketThread extends Thread {
	private Socket socket;
	public ClientSocketThread(Socket clientSocket) {
		this.socket = clientSocket;
	}

}
