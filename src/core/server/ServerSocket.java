package core.server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import core.client.ClientSocketThread;

public class ServerSocket {
	private List<ClientSocketThread> clients;
	private int port;
	private java.net.ServerSocket socket;
	private boolean run = true;
	public ServerSocket(int port) {
		this.port = port;
		this.clients = new ArrayList<>();
	}
	public ServerSocket() {
		new ServerSocket(8080);
	}
	public void stop() {
		this.run = false;
	}
	
	@Override
	public void run() {
		try {
			this.socket = new java.net.ServerSocket(this.port);
			System.out.println("Server has started on port: " + this.socket.getLocalPort());
			System.out.println("Waiting for clients...");
			while(this.run) {
				try {
					// A client socket will represent a connection between the client and this server
					Socket clientSocket = this.socket.accept();
					System.out.println("A Connection Established: " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());
					this.clients.add(new ClientSocketThread(clientSocket));
				}  catch (IOException e) {
					System.out.println(e.getMessage());
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
