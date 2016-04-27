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
	public ClientSocket(String address, int port) {
		this.serverAddress = address;
		this.serverPort = port;
		this.messageBuffer = new MessageBufferModel();
	}
	
	public void connect() throws IOException, UnknownHostException {
		socket = new java.net.Socket(this.serverAddress,this.serverPort);
	}
	
	@Override
	public void run() {
		while(this.run && this.socket.isConnected()) {
			//Read socket input
			try {
				Scanner reader = new Scanner(socket.getInputStream());
				while(reader.hasNext()) {
					System.out.println(this.serverAddress+":"+this.serverPort + " - " + reader.nextLine());
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
			
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
