package core.server;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import core.BaseSocket;
import data.model.MessageBufferModel;

public class ClientSocketThread extends BaseSocket {
	private Socket socket;
	private String clientAddress;
	private int clientPort;
	private MessageBufferModel messageBuffer;
	private Scanner reader;
	public ClientSocketThread(Socket clientSocket) throws IOException {
		this.socket = clientSocket;
		this.socket.setSoTimeout(1000);
		this.clientAddress = clientSocket.getInetAddress().getHostAddress();
		this.clientPort = clientSocket.getPort();
		this.messageBuffer = new MessageBufferModel();
		this.reader = new Scanner(socket.getInputStream());
	}

	public void run() {
		while(this.run && this.socket.isConnected()) {
			//Read socket input
			try {
				while(socket.getInputStream().available() > 0 && reader.hasNext()) {
					System.out.println("Message received from " + this.clientAddress+System.lineSeparator()+"Sender's Port: "+this.clientPort + System.lineSeparator()+"Message: \"" + reader.nextLine() + "\"");
				}
			} catch (IOException e) {
				if(e.getLocalizedMessage().compareToIgnoreCase("Socket Closed") != 0) {
					e.printStackTrace();
				}
			}
			
			//Write to socket output
			while(messageBuffer.size() > 0) {
				try {
					this.socket.getOutputStream().write(messageBuffer.pop().getBytes());
					this.socket.getOutputStream().flush();
				} catch (IOException e) {
					if(e.getLocalizedMessage().compareToIgnoreCase("Socket Closed") != 0) {
						e.printStackTrace();
					}
				}
			}
		}
		try {
			this.socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void closeSocket() throws IOException {
		this.socket.close();
	}
	
	@Override
	public boolean isConnected() {
		try {
			return this.socket.isConnected() || this.socket.getInetAddress().isReachable(300);
		} catch (IOException e) {
			return false;
		}
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
