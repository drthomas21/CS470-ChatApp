package utility.parser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Scanner;

import utility.vendors.douglascrockford.json.JSONException;
import utility.vendors.douglascrockford.json.JSONObject;

public class JSONFileParser {
	private static HashMap<String,String> serverConfig = new HashMap<>();
	public static void parseFile(String filename) {
		try {
			Scanner scan = new Scanner(new FileReader(filename));
			String jsonStr = "";
			while(scan.hasNext()) {
				jsonStr += scan.nextLine();
			}
			scan.close();

			JSONObject rootObject = new JSONObject(jsonStr); // Parse the JSON to a JSONObject

			//Parse Server Config
			JSONObject serverConfig = rootObject.getJSONObject("server");
			if(serverConfig != null) {
				for(String name : JSONObject.getNames(serverConfig))
					JSONFileParser.serverConfig.put(name,serverConfig.getString(name));
			}
		} catch (JSONException e) {
			// JSON Parsing error
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static HashMap<String,String> getServerConfig() {
		return serverConfig;
	}
}
