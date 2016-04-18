package core;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
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
			connections.add(socket);
		} catch(UnknownHostException e) {
			System.out.println(e.getLocalizedMessage());
		} catch(IOException e) {
			System.out.println(e.getLocalizedMessage());
		}		
	}
	protected static List<BaseSocket> getConnections() {
		List<BaseSocket> sockets = new ArrayList<>();
		for(BaseSocket socket : connections) {
			sockets.add(socket);
		}
		for(BaseSocket socket : server.getClients()){
			sockets.add(socket);
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
		BaseSocket socket = _connections.get(idx);
		if(socket != null) {
			socket.stopThread();
			connections.remove(idx);
			System.out.println("Connection terminated");
		} else {
			System.out.println("Connection not found");
		}
	}
	public static void main(String args[]) {
		connections = new ArrayList<>();
		Scanner reader = new Scanner(System.in);
		String command = "";
		int port = 8080;
		if(args.length > 0) {
			for(int i = 0; i < args.length; i++){
				System.out.println("["+i+"]: " + args[i]);
			}
			//port = Integer.valueOf(args[0]);
		}
		ChatApp.server = new core.server.ServerSocket(port);
		ChatApp.server.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while(command.compareToIgnoreCase("exit") != 0) {
			//Check connections
			for(ClientSocket socket : connections) {
				if(!socket.isConnected()) {
					connections.remove(socket);
				}
			}
			server.isConnected();
			
			System.out.print("Command: ");
			command = reader.nextLine();
			if(command.compareToIgnoreCase("help") == 0) {
				//TODO: add help message
			} else if(command.compareToIgnoreCase("myip") == 0) {
				System.out.println(ChatApp.server.getAddress());
			} else if(command.compareToIgnoreCase("myport") == 0) {
				System.out.println(ChatApp.server.getPort());
			} else if(command.length() >= 7 && command.substring(0, 6).compareToIgnoreCase("connect") == 0) {
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
			} else if(command.length() >= 8 && command.substring(0,7).compareToIgnoreCase("terminate") == 0) {
				String[] parts = command.trim().split(" ");
				if(parts.length == 2) {
					int id;
					try {
						id = Integer.valueOf(parts[2]);
					} catch(NumberFormatException e) {
						id = 0;
					}
					if(id <= 0) {
						System.out.println("Invalid Arguments: terminate <id>");
					} else {
						removeConnection(id);
					}
				} else {
					System.out.println("Invalid Arguments: terminate <id>");
				}
			} else if(command.length() > 4 && command.substring(0,4).compareToIgnoreCase("send") == 0) {
				//TODO: add ability to send message
				String[] parts = command.trim().split(" ",3);
				if(parts.length == 3) {
					
				} else {
					System.out.println("Invalid Arguments: send <id> <message>");
				}
			} else if(command.compareToIgnoreCase("exit") != 0) {
				System.out.println("Invalid Command");
			}
		}
		
		ChatApp.server.stopThread();
		reader.close();
	}
}
