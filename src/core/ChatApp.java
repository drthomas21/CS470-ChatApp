package core;

import core.server.ServerSocket;
import utility.parser.JSONFileParser;

public class ChatApp {
	private static core.server.ServerSocket server;
	public static void main(String args[]) {
		JSONFileParser.parseFile("config.json");
		ChatApp.server = new core.server.ServerSocket(Integer.valueOf(JSONFileParser.getServerConfig().get("port")));
		ChatApp.server.start();
	}
}
