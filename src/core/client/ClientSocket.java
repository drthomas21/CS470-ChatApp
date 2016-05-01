package core.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;

import core.BaseSocket;
import data.model.MessageBufferModel;

public class ClientSocket extends BaseSocket {
	private String serverAddress;
	private int serverPort;
	private java.net.Socket socket;
	private MessageBufferModel messageBuffer;
	private Scanner reader;
	public ClientSocket(String address, int port) {
		this.serverAddress = address;
		this.serverPort = port;
		this.messageBuffer = new MessageBufferModel();
	}

	public void connect() throws IOException, UnknownHostException {
		socket = new java.net.Socket(this.serverAddress,this.serverPort);
		socket.setSoTimeout(1000);
		reader = new Scanner(socket.getInputStream());
	}

	@Override
	public void run() {
		while(this.run && this.socket.isConnected()) {
			//Read socket input
			try {
				while(socket.getInputStream().available() > 0 && reader.hasNext()) {
					System.out.println("Message received from " + this.serverAddress+System.lineSeparator()+"Sender's Port: "+this.serverPort + System.lineSeparator()+"Message: \"" + reader.nextLine() + "\"");
				}
				//reader.close();
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
		return this.serverPort;
	}

	@Override
	public String getAddress() {
		return this.serverAddress;
	}

	public void sendMessage(String message) {
		messageBuffer.queue(message);
	}
}
