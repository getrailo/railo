package railo.runtime.engine;

import railo.runtime.config.ConfigServer;

public class ShutdownHook extends Thread {
	
	private ConfigServer cs;

	public ShutdownHook(ConfigServer cs) {
		this.cs=cs;
	}
	
	public void run() {
		
		// TODO Server.cfc->onServerEnd
		
		// try to update jars, doing this here because on windows the files could be locked
		/*try {
			JarLoader.download(cs, Admin.UPDATE_JARS);
		}
		catch (Throwable t) {
			SystemOut.printDate(cs.getErrWriter(),ExceptionUtil.getStacktrace(t, true));
		}*/
		
		
	}
}
