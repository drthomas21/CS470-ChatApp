package core.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import data.model.MessageBufferModel;

public class ClientSocketThread extends Thread {
	private Socket socket;
	private boolean isConnected = true;
	private InputStream inputStream;
	private OutputStream outputStream; // For use with outgoing responses
	public ClientSocketThread(Socket clientSocket, MessageBufferModel buffer) throws IOException {
		this.socket = clientSocket;
		this.inputStream = clientSocket.getInputStream();
		this.outputStream = clientSocket.getOutputStream();
	}

	public void run() {
		while(isConnected) {
			//TODO: Read stream
			
			//TODO: write stream
		}
	}
}
