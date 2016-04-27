package data.model;

import java.util.ArrayList;
import java.util.List;

public class MessageBufferModel {
	private List<String> messages;
	
	public MessageBufferModel() {
		this.messages = new ArrayList<>();
	}
	
	public void queue(String message) {
		this.messages.add(message);
	}
	
	public String pop() {
		return this.messages.remove(0);
	}
	
	public int size() {
		return this.messages.size();
	}
}
