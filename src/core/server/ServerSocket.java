package core.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import core.BaseSocket;

public class ServerSocket extends BaseSocket {
	private int port;
	private java.net.ServerSocket socket;
	private List<ClientSocketThread> clients;
	
	public ServerSocket(int port) {
		this.port = port;
		this.clients = new ArrayList<>();
	}
	public ServerSocket() {
		new ServerSocket(8080);
	}
	
	public int getPort() {
		return this.port;
	}
	
	public List<BaseSocket> getClients() {
		List<BaseSocket> _clients = new ArrayList<>();
		Iterator<ClientSocketThread> itr = this.clients.iterator();
		while(itr.hasNext()) {
			_clients.add(itr.next());
		}
		return _clients;
	}
	
	public String getAddress() {
		return this.socket.getInetAddress().getHostAddress();
	}
	
	private InetAddress getInetAddress() throws SocketException {
		NetworkInterface ni = null;
		InetAddress iNetAddress = null;
		Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
		while(nics.hasMoreElements()) {
			ni = nics.nextElement();
			if(iNetAddress == null && (ni.getName().startsWith("wlan") || ni.getName().startsWith("eth"))) {
				Enumeration<InetAddress> addresses = ni.getInetAddresses();
				while(addresses.hasMoreElements()) {
					iNetAddress = addresses.nextElement();
					String address = iNetAddress.getHostAddress();
					if(address.matches("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}") && address.compareTo("127.0.0.1") != 0 && address.compareTo("0.0.0.0") != 0){
						break;
					}
					
					iNetAddress = null;
				}
			}				
		}
		
		return iNetAddress;
	}
	
	@Override
	public void run() {
		try {
			InetAddress iNetAddress = this.getInetAddress();
			if(iNetAddress == null) {
				throw new IOException("No external network interface card detected");
			}
			this.socket = new java.net.ServerSocket(this.port, 0,iNetAddress);
			System.out.println("Server has started on port: " + this.socket.getLocalPort());
			System.out.println("Waiting for clients...");
			this.socket.setSoTimeout(1000);
			while(this.run) {
				try {
					// A client socket will represent a connection between the client and this server
					Socket clientSocket = this.socket.accept();
					try {
						ClientSocketThread client = new ClientSocketThread(clientSocket);
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
			this.socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getLocalizedMessage());
			e.printStackTrace();
		}		
	}
	
	@Override
	public boolean isConnected() {
		for(ClientSocketThread client : clients) {
			if(!client.isConnected()) {
				client.stopThread();
				clients.remove(client);
			}
		}
		
		return this.run && this.socket != null && !this.socket.isClosed();
	}
	
	@Override
	protected void closeSocket() throws IOException {
		//Do Nothing
	}
	
	@Override
	public void sendMessage(String message) {
		//Do Nothing
	}
}
