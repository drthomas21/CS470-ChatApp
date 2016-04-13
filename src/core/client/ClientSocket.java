package core.client;

import java.io.IOException;
import java.net.UnknownHostException;

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
		//TODO: setup I/O steams
	}
	
	@Override
	public void run() {
		while(this.run) {
			
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
}
