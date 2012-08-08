package railo.cli;

import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Closer extends Thread {

	private String name;
	private Registry reg;
	private long idleTime;
	private CLIInvokerImpl invoker;

	public Closer(Registry reg, CLIInvokerImpl invoker, String name, long idleTime) {
		this.reg=reg;
		this.name=name;
		this.idleTime=idleTime;
		this.invoker=invoker;
	}

	public void run() {
		// idle
		do{
			sleepEL(idleTime);
		}
		while(invoker.lastAccess()+idleTime>System.currentTimeMillis());
		
		
		try {
			reg.unbind(name);
			UnicastRemoteObject.unexportObject(invoker,true);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		
	}

	private void sleepEL(long millis) {
		try {
			sleep(millis);
		} catch (Throwable t) {t.printStackTrace();}
	}

}
