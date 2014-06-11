package railo.runtime.exp;

import java.io.PrintWriter;

import railo.commons.io.log.Log;
import railo.commons.io.log.LogUtil;
import railo.commons.lang.ExceptionUtil;
import railo.runtime.PageContext;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.op.Caster;

/**
 * Handle Excpetions
 */
public final class ExceptionHandler {

	public static void log(Config config, Throwable t) {
		
		PageException pe=Caster.toPageException(t);
		//pe.printStackTrace(config.getErrWriter()); 
		
		// apllication Log
		//LogUtil.log(((ConfigImpl)config).getLog("application"),Log.LEVEL_ERROR, "",pe);
		
		// exception.log
		//String st = ExceptionUtil.getStacktrace(pe,true);
		LogUtil.log(((ConfigImpl)config).getLog("exception"),Log.LEVEL_ERROR, "",pe);
		
		
	}

	public static void printStackTrace(PageContext pc, Throwable t) {
		PrintWriter pw = (pc.getConfig()).getErrWriter();
		t.printStackTrace(pw);
		pw.flush();
	}

	public static void printStackTrace(Throwable t) {
		PageContext pc = ThreadLocalPageContext.get();
		if(pc!=null)printStackTrace(pc,t);
		else t.printStackTrace();
	}
}