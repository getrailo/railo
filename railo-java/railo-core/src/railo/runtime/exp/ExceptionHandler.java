package railo.runtime.exp;

import java.io.PrintWriter;

import railo.commons.io.log.Log;
import railo.commons.lang.ExceptionUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.config.Config;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.op.Caster;

/**
 * Handle Excpetions
 */
public final class ExceptionHandler {

	public static void log(Config config, Throwable t) {
		
		PageException pe=Caster.toPageException(t);
		pe.printStackTrace(config.getErrWriter()); 
		
		// apllication Log
		StringBuffer message=new StringBuffer(pe.getMessage());
		if(!StringUtil.isEmpty(pe.getDetail())) message.append("; ").append(pe.getDetail());
		config.getApplicationLogger().log(Log.LEVEL_ERROR, "",message.toString());
		
		// exception.log
		String st = ExceptionUtil.getStacktrace(pe,true);
		config.getExceptionLogger().log(Log.LEVEL_ERROR, "",st);
		
		
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