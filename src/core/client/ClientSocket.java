package core.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import core.BaseSocket;
import data.model.MessageBufferModel;

public class ClientSocket extends BaseSocket {
	public static final String MESSAGE_PREFIX = "msg:";
	private String hostAddress;
	private int hostPort;
	private java.net.Socket socket;
	private MessageBufferModel messageBuffer;
	private Scanner reader;
	private long timestamp;
	
	public ClientSocket(String address, int port) {
		this.hostAddress = address;
		this.hostPort = port;
		this.messageBuffer = new MessageBufferModel();
	}
	
	public ClientSocket(Socket clientSocket) throws IOException {
		this.socket = clientSocket;
		this.socket.setSoTimeout(1000);
		this.hostAddress = clientSocket.getInetAddress().getHostAddress();
		this.hostPort = clientSocket.getPort();
		this.messageBuffer = new MessageBufferModel();
		this.reader = new Scanner(socket.getInputStream());
	}

	public void connect() throws IOException, UnknownHostException {
		socket = new java.net.Socket(this.hostAddress,this.hostPort);
		socket.setSoTimeout(1000);
		reader = new Scanner(socket.getInputStream());
	}

	@Override
	public void run() {
		timestamp = System.currentTimeMillis();
		while(this.run && !this.socket.isClosed() && System.currentTimeMillis() - timestamp < 1000) {
			//Read socket input
			try {
				while(socket.getInputStream().available() > 0 && reader.hasNext()) {
					timestamp = System.currentTimeMillis();
					String message = reader.nextLine();
					if(message.compareTo("1") != 0) {
						System.out.println("Message received from " + this.hostAddress+System.lineSeparator()+"Sender's Port: "+this.hostPort + System.lineSeparator()+"Message: \"" + message.replaceFirst(MESSAGE_PREFIX, "") + "\"");
					}
				}
				//reader.close();
			} catch (IOException e) {
				break;
			}			

			//Write to socket output
			if(messageBuffer.size() > 0) {
				while(messageBuffer.size() > 0) {
					try {
						this.socket.getOutputStream().write((MESSAGE_PREFIX + messageBuffer.pop()+System.lineSeparator()).getBytes());
						this.socket.getOutputStream().flush();
					} catch (IOException e) {
						break;
					}
				}
			} else {
				//Send heart beat
				try {
					this.socket.getOutputStream().write(("1"+System.lineSeparator()).getBytes());
					this.socket.getOutputStream().flush();
				} catch (IOException e) {
					break;
				}
			}
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		this.run = false;
		
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
		return !this.socket.isClosed();
	}

	@Override
	public int getPort() {
		return this.hostPort;
	}

	@Override
	public String getAddress() {
		return this.hostAddress;
	}

	public void sendMessage(String message) {
		messageBuffer.queue(message);
	}
}
