package core.server;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Scanner;
import core.BaseSocket;
import data.model.MessageBufferModel;

public class ClientSocketThread extends BaseSocket {
	private Socket socket;
	private boolean isConnected = true;
	private String clientAddress;
	private int clientPort;
	private MessageBufferModel messageBuffer;
	public ClientSocketThread(Socket clientSocket) throws IOException {
		this.socket = clientSocket;
		this.clientAddress = clientSocket.getInetAddress().getHostAddress();
		this.clientPort = clientSocket.getPort();
		this.messageBuffer = new MessageBufferModel();
	}

	public void run() {
		while(isConnected && this.socket.isConnected()) {
			//Read socket input
			try {
				Scanner reader = new Scanner(socket.getInputStream());
				while(reader.hasNext()) {
					System.out.println(this.clientAddress+":"+this.clientPort + " - " + reader.nextLine());
				}
				reader.close();
			} catch (IOException e) {
				//TODO: handle exception
				//Failed to read from socket
			}
			
			//Write to socket output
			while(messageBuffer.size() > 0) {
				try {
					socket.getOutputStream().write(messageBuffer.pop().getBytes(Charset.forName("UTF-8")));
				} catch (IOException e) {
					// TODO handle exception
					//Failed to write to socket
				}
			}
		}
	}
	
	@Override
	protected void closeSocket() throws IOException {
		this.socket.close();
	}
	
	@Override
	public boolean isConnected() {
		return this.socket.isConnected();
	}
	
	@Override
	public int getPort() {
		return this.clientPort;
	}

	@Override
	public String getAddress() {
		return this.clientAddress;
	}
	
	public void sendMessage(String message) {
		messageBuffer.queue(message);
	}
}
