package core;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Scanner;
import core.client.ClientSocket;

public class ChatApp {
	private static List<ClientSocket> connections;
	private static core.server.ServerSocket server;
	protected static void connectToServer(String address, int port) {
		ClientSocket socket = new ClientSocket(address,port);
		try {
			socket.connect();
			socket.start();
			connections.add(socket);
		} catch(UnknownHostException e) {
			System.out.println(e.getLocalizedMessage());
		} catch(IOException e) {
			System.out.println(e.getLocalizedMessage());
		}		
	}
	protected static List<BaseSocket> getConnections() {
		server.isConnected();
		List<BaseSocket> sockets = new ArrayList<>();
		try {
			for(BaseSocket socket : connections) {
				if(socket.isConnected()) {
					sockets.add(socket);
				} else {
					socket.stopThread();
					connections.remove(socket);
				}
			}
			for(BaseSocket socket : server.getClients()){
				if(socket.isConnected()) {
					sockets.add(socket);
				}
				
			}
		} catch (ConcurrentModificationException e) {
			return getConnections();
		}
		
		
		return sockets;
	}
	protected static void listConnections() {
		System.out.println("id: IP address\tPort No.");
		List<BaseSocket> _connections = getConnections();
		for(int i=0;i<_connections.size();i++){
			System.out.println(i + ": " + _connections.get(i).getAddress() + "\t"+_connections.get(i).getPort());
		}
	}
	protected static void removeConnection(Integer idx) {
		List<BaseSocket> _connections = getConnections();
		
		if(idx < _connections.size()) {
			BaseSocket socket = _connections.get(idx);
			socket.stopThread();
			System.out.println("Connection terminated");
		} else {
			System.out.println("Connection not found");
		}
	}
	protected static void sendMessage(Integer idx, String message) {
		List<BaseSocket> _connections = getConnections();
		
		if(idx < _connections.size()) {
			BaseSocket socket = _connections.get(idx);
			socket.sendMessage(message);
		} else {
			System.out.println("Connection not found");
		}
	}
	public static void main(String args[]) {
		connections = new ArrayList<>();
		Scanner reader = new Scanner(System.in);
		String command = "", nic = "",ipaddress = "";
		int port = 8080;
		
		if(args.length > 0) {
			port = Integer.valueOf(args[0]);
		}
		if(args.length > 2) {
			nic = args[1];
			ipaddress = args[2];
		}
		
		ChatApp.server = new core.server.ServerSocket(port);
		if(!nic.isEmpty()) {
			try {
				ChatApp.server.setNetworkInterface(nic,ipaddress);
			} catch (SocketException e1) {
				System.out.println("Network Interface ["+nic+"] does not exists");
			}
		}
		ChatApp.server.start();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while(command.compareToIgnoreCase("exit") != 0) {			
			System.out.print("Command: ");
			command = reader.nextLine();
			if(command.compareToIgnoreCase("help") == 0) {
				System.out.println(
						"help: Display information about the available user interface options or command manual." + System.lineSeparator() +
						"myip: Display the IP address of this process." + System.lineSeparator() +
						"myport: Display the port on which this process is listening for incoming connections." + System.lineSeparator() +
						"connect <destination> <port no>: This command establishes a new TCP connection to the specified <destination> at the specified < port no>." + System.lineSeparator() +
						"list: Display a numbered list of all the connections this process is part of." + System.lineSeparator() +
						"terminate <connection id>: This command will terminate the connection listed under the specified number when LIST is used to display all connections." + System.lineSeparator() +
						"send <connection id> <message>: This will send the <message> to the host on the connection that is designated by the <connection id>." + System.lineSeparator() +
						"exit: Close all connections and terminate this process."
				);
			} else if(command.compareToIgnoreCase("myip") == 0) {
				System.out.println(ChatApp.server.getAddress());
			} else if(command.compareToIgnoreCase("myport") == 0) {
				System.out.println(ChatApp.server.getPort());
			} else if(command.length() >= 7 && command.substring(0, 7).compareToIgnoreCase("connect") == 0) {
				String[] parts = command.trim().split(" ");
				if(parts.length == 3) {
					String serverAddress = parts[1];
					int serverPort;
					try {
						serverPort = Integer.valueOf(parts[2]);
					} catch(NumberFormatException e) {
						serverPort = 0;
					}
					if(serverPort <= 0) {
						System.out.println("Invalid Arguments: connect <host> <port>");
					} else {
						connectToServer(serverAddress,serverPort);
					}
				} else {
					System.out.println("Invalid Arguments: connect <host> <port>");
				}
				
			} else if(command.compareToIgnoreCase("list") == 0) {
				ChatApp.listConnections();
			} else if(command.length() >= 8 && command.substring(0,9).compareToIgnoreCase("terminate") == 0) {
				String[] parts = command.trim().split(" ");
				if(parts.length == 2) {
					int id;
					try {
						id = Integer.valueOf(parts[1]);
						removeConnection(id);
					} catch(NumberFormatException e) {
						System.out.println("Invalid Id: terminate <id>");
					}
				} else {
					System.out.println("Invalid Arguments: terminate <id>");
				}
			} else if(command.length() > 4 && command.substring(0,4).compareToIgnoreCase("send") == 0) {
				String[] parts = command.trim().split(" ",3);
				if(parts.length == 3) {
					int id;
					try {
						id = Integer.valueOf(parts[1]);
						sendMessage(id,parts[2]);
					} catch(NumberFormatException e) {
						System.out.println("Invalid Id: send <id> <message>");
					}
				} else {
					System.out.println("Invalid number of Arguments: send <id> <message>");
				}
			} else if(command.compareToIgnoreCase("exit") != 0) {
				System.out.println("Invalid Command");
			}
		}
		System.out.println("Shutting down");
		for(BaseSocket socket : getConnections()) {
			socket.stopThread();
		}
		ChatApp.server.stopThread();
		reader.close();
	}
}
