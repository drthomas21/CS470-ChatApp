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
import core.client.ClientSocket;

public class ServerSocket extends BaseSocket {
	private int port;
	private NetworkInterface iNetInterface;
	private InetAddress iNetAddress;
	private java.net.ServerSocket socket;
	private List<ClientSocket> clients;
	
	public ServerSocket(int port) {
		this.port = port;
		this.clients = new ArrayList<>();
		this.iNetAddress = null;
		this.iNetInterface = null;
	}
	public ServerSocket() {
		this(8080);
	}
	
	public int getPort() {
		return this.port;
	}
	
	public List<ClientSocket> getClients() {
		List<ClientSocket> _clients = new ArrayList<>();
		Iterator<ClientSocket> itr = this.clients.iterator();
		while(itr.hasNext()) {
			ClientSocket client = itr.next();
			if(client.isConnected()) {
				_clients.add(client);
			} else {
				itr.remove();
			}			
		}
		return _clients;
	}
	
	public String getAddress() {
		return this.socket.getInetAddress().getHostAddress();
	}
	
	@Override
	public void run() {
		try {
			InetAddress iNetAddress = this.getInetAddress();
			if(iNetAddress == null) {
				throw new IOException("No external network interface card detected");
			} else {
				System.out.println("Using Network Interface: " + this.iNetInterface.getName());
				System.out.println("Using Address: " + this.iNetAddress.getHostAddress());
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
						ClientSocket client = new ClientSocket(clientSocket);
						client.start();
						this.clients.add(client);
					} catch(IOException e) {
						System.out.println("Server failed to setup I/O streams with client");
					}					
				} catch(java.net.SocketTimeoutException e) {
					//Do nothing, socket waits for 1 second for any handshakes
				} catch (IOException e) {
					System.out.println(e.getLocalizedMessage());
				}
			}			
			this.socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getLocalizedMessage());
		}		
	}
	
	@Override
	public boolean isConnected() {
		Iterator<ClientSocket> _itr1 = clients.iterator();
		while(_itr1.hasNext()) {
			BaseSocket client = _itr1.next();
			if(client != null && !client.isConnected()) {
				client.stopThread();
				_itr1.remove();
			}
		}		
		
		return this.run && this.socket != null && !this.socket.isClosed();
	}
	
	@Override
	protected void closeSocket() throws IOException {
		Iterator<ClientSocket> itr = this.clients.iterator();
		while(itr.hasNext()) {
			itr.next().stopThread();
		}
	}
	
	@Override
	public void sendMessage(String message) {
		Iterator<ClientSocket> itr = this.clients.iterator();
		while(itr.hasNext()) {
			itr.next().sendMessage(message);
		}
	}
	
	public void setNetworkInterface(String name) throws SocketException {
		this.iNetInterface = NetworkInterface.getByName(name);
		if(this.iNetInterface == null) {
			throw new SocketException("Network Interface ["+name+"] does not exists");
		}
	}
	
	public void setAddress(String address) throws SocketException {
		if(this.iNetInterface == null) {
			this.getInetAddress(address);
			
			if(this.iNetAddress == null) {
				throw new SocketException("No interfaces have the address ["+address+"]");
			}
		} else {
			Enumeration<InetAddress> addresses = this.iNetInterface.getInetAddresses();
			while(addresses.hasMoreElements()) {
				this.iNetAddress = addresses.nextElement();
				String _address = iNetAddress.getHostAddress();
				if(address.compareTo(_address) == 0){
					break;
				}
				
				iNetAddress = null;
			}
			
			if(this.iNetAddress == null) {
				throw new SocketException("Network interface ["+this.iNetInterface.getName()+"] does not have the address ["+address+"]");
			}
		}
	}
	
	private InetAddress getInetAddress() throws SocketException {
		if(iNetAddress != null) {
			return iNetAddress;
		}
		
		Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
		while(nics.hasMoreElements()) {
			this.iNetInterface = nics.nextElement();
			if(iNetAddress == null && (this.iNetInterface.getName().startsWith("wlan") || this.iNetInterface.getName().startsWith("eth"))) {
				Enumeration<InetAddress> addresses = this.iNetInterface.getInetAddresses();
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
	
	private InetAddress getInetAddress(String address) throws SocketException {
		if(iNetAddress != null) {
			return iNetAddress;
		}
		
		Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
		while(nics.hasMoreElements()) {
			this.iNetInterface = nics.nextElement();
			if(iNetAddress == null && (this.iNetInterface.getName().startsWith("wlan") || this.iNetInterface.getName().startsWith("eth"))) {
				Enumeration<InetAddress> addresses = this.iNetInterface.getInetAddresses();
				while(addresses.hasMoreElements()) {
					iNetAddress = addresses.nextElement();
					String _address = iNetAddress.getHostAddress();
					if(address.matches(_address)){
						break;
					}
					
					iNetAddress = null;
				}
			}				
		}
		
		return iNetAddress;
	}
}
