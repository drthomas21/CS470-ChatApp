package core;

abstract public class BaseSocket extends Thread {
	protected boolean run = true;
	
	public final void stopThread() {
		run = false;
	}
	
	abstract public int getPort();	
	abstract public String getAddress();
	
	
}
