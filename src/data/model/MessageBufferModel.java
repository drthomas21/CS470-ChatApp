package data.model;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageBufferModel {
	private HashMap<String,ArrayList<String>> messages;
	
	public MessageBufferModel() {
		this.messages = new HashMap<String,ArrayList<String>>();
	}
	
	public void queue(String to, String message) {
		if(!this.messages.containsKey(to)) {
			this.messages.put(to, new ArrayList<String>());
		}
		
		//TODO: add lock
		ArrayList<String> buffer = this.messages.get(to);
		buffer.add(message);
		this.messages.put(to, buffer);
		//TODO: remove lock
	}
	
	public String pop(String to) {
		String item = null;
		if(!this.messages.containsKey(to)) {
			//TODO: add lock
			ArrayList<String> buffer = this.messages.get(to);
			item = buffer.remove(0);
			this.messages.put(to, buffer);
			//TODO: remove lock
		}
		return item;
	}
}
