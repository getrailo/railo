package railo.runtime.orm;

import railo.commons.lang.SystemOut;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.PageException;

public class ORMUtil {
	
	
	
	
	
	
	
	/*public static void checkRestriction(PageContext pc) {
		boolean enable = false;
		try {
			String str = Caster.toString(pc.serverScope().get("enableORM", null), null);
			enable="dinfao".equals(str);
		} 
		catch (PageException e) {}
		//enable=false;
		if(!enable)
			throw new PageRuntimeException(new railo.runtime.exp.SecurityException("orm functionality is not supported"));
		
	}*/
	
	public static ORMSession getSession(PageContext pc) throws PageException {
		return ((PageContextImpl) pc).getORMSession();
	}

	public static ORMEngine getEngine(PageContext pc) throws PageException {
		//checkRestriction(pc);
		ConfigImpl config=(ConfigImpl) pc.getConfig();
		return config.getORMEngine(pc);
	}

	public static void resetEngine(PageContext pc) throws PageException {
		//checkRestriction(pc);
		ConfigImpl config=(ConfigImpl) pc.getConfig();
		config.resetORMEngine(pc);
	}
	
	public static void printError(Throwable t, ORMEngine engine) throws ORMException {
		printError(t, engine, t.getMessage());
	}

	public static void printError(String msg, ORMEngine engine) throws ORMException {
		printError(null, engine, msg);
	}
	

	private static void printError(Throwable t, ORMEngine engine,String msg) throws ORMException {
		SystemOut.printDate("{"+engine.getLabel().toUpperCase()+"} - "+msg,SystemOut.ERR);
		if(t==null)t=new Throwable();
		t.printStackTrace(SystemOut.getPrinWriter(SystemOut.ERR));
	}
}
