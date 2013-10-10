package railo.runtime.engine;

import java.io.IOException;
import java.io.PrintWriter;

import railo.commons.io.SystemUtil;
import railo.commons.lang.ExceptionUtil;
import railo.commons.lang.SystemOut;
import railo.commons.net.JarLoader;
import railo.runtime.config.ConfigServer;
import railo.runtime.tag.Admin;

public class ShutdownHook extends Thread {
	
	private ConfigServer cs;

	public ShutdownHook(ConfigServer cs) {
		this.cs=cs;
	}
	
	public void run() {
		
		// TODO Server.cfc->onServerEnd
		
		// try to update jars, doing this here because on windows the files could be locked
		try {
			JarLoader.download(cs, Admin.UPDATE_JARS);
		}
		catch (Throwable t) {
			SystemOut.printDate(cs.getErrWriter(),ExceptionUtil.getStacktrace(t, true));
		}
		
		
	}
}
