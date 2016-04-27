package core.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
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
				System.out.println(e.getMessage());
				e.printStackTrace();
			}

			//Write to socket output
			while(messageBuffer.size() > 0) {
				try {
					String message = messageBuffer.pop();
					socket.getOutputStream().write(message.getBytes(Charset.forName("UTF-8")));
				} catch (IOException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
		}
		try {
			this.socket.close();
			reader.close();
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
		return this.socket.isConnected();
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
