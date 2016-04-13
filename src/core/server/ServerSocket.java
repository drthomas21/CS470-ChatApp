package core.server;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import core.BaseSocket;
import data.model.MessageBufferModel;

public class ServerSocket extends BaseSocket {
	private int port;
	private MessageBufferModel messageBuffer;
	private java.net.ServerSocket socket;
	private List<ClientSocketThread> clients;
	
	public ServerSocket(int port) {
		this.port = port;
		this.clients = new ArrayList<>();
		this.messageBuffer = new MessageBufferModel();
	}
	public ServerSocket() {
		new ServerSocket(8080);
	}
	
	public int getPort() {
		return this.port;
	}
	
	@SuppressWarnings("static-access")
	public String getAddress() {
		try {
			return this.socket.getInetAddress().getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			return "0.0.0.0";
		}
	}
	
	@Override
	public void run() {
		try {
			this.socket = new java.net.ServerSocket(this.port);
			System.out.println("Server has started on port: " + this.socket.getLocalPort());
			System.out.println("Waiting for clients...");
			this.socket.setSoTimeout(1000);
			while(this.run) {
				try {
					// A client socket will represent a connection between the client and this server
					Socket clientSocket = this.socket.accept();
					System.out.println("A Connection Established: " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());
					try {
						ClientSocketThread client = new ClientSocketThread(clientSocket,this.messageBuffer);
						client.start();
						this.clients.add(client);
					} catch(IOException e) {
						System.out.println("Server failed to setup I/O streams with client");
					}					
				} catch(java.net.SocketTimeoutException e) {
					//Do nothing, socket waits for 1 second for any handshakes
				} catch (IOException e) {
					System.out.println("Client failed to establish a connection");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
