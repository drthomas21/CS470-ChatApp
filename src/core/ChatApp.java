package core;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Scanner;
import core.client.ClientSocket;

public class ChatApp {
	private static HashMap<Integer,ClientSocket> connections;
	private static int connNum = 0;
	private static core.server.ServerSocket server;
	protected static void connectToServer(String address, int port) {
		ClientSocket socket = new ClientSocket(address,port);
		try {
			socket.connect();
			connNum++;
			connections.put(connNum,socket);
		} catch(UnknownHostException e) {
			System.out.println(e.getLocalizedMessage());
		} catch(IOException e) {
			System.out.println(e.getLocalizedMessage());
		}		
	}
	protected static void listConnections() {
		System.out.println("id: IP address\tPort No.");
		for(Integer i : connections.keySet()) {
			System.out.println(i + ": " + connections.get(i).getAddress() + "\t"+connections.get(i).getPort());
		}
	}
	protected static void removeConnection(Integer id) {
		if(connections.containsKey(id)) {
			connections.get(id).stopThread();
			connections.remove(id);
			System.out.println("Connection terminated");
		} else {
			System.out.println("Connection not found");
		}
	}
	public static void main(String args[]) {
		connections = new HashMap<>();
		Scanner reader = new Scanner(System.in);
		String command = "";
		int port = 8080;
		if(args.length > 0) {
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
			} else if(command.compareToIgnoreCase("exit") != 0) {
				System.out.println("Invalid Command");
			}
		}
		
		ChatApp.server.stopThread();
		reader.close();
	}
}
