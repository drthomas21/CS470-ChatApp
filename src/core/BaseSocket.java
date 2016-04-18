package core;

import java.io.IOException;

abstract public class BaseSocket extends Thread {
	protected boolean run = true;
	
	public final void stopThread() {
		run = false;
		try {
			this.closeSocket();
		} catch (IOException e) {
			//TODO: failure with closing socket
		}
	}
	
	abstract protected void closeSocket() throws IOException;
	abstract public int getPort();	
	abstract public String getAddress();
	abstract public boolean isConnected();	
}
