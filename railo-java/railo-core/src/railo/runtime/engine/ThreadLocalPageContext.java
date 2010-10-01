package railo.runtime.engine;

import java.util.TimeZone;

import railo.runtime.PageContext;
import railo.runtime.config.Config;

/**
 * class to handle thread local PageContext, 
 * do use pagecontext in classes that have no method argument pagecontext
 */
public final class ThreadLocalPageContext {

	private static final TimeZone DEFAULT_TIMEZONE = TimeZone.getDefault();
	private static ThreadLocal<PageContext> pcThreadLocal=new ThreadLocal<PageContext>();

	/**
	 * register a pagecontext for he current thread
	 * @param pc PageContext to register
	 */
	public static void register(PageContext pc) {
		pcThreadLocal.set(pc);
	}

	/**
	 * returns pagecontext registered for the current thread
	 * @return pagecontext for the current thread or null 
	 * if no pagecontext is regisred for the current thread
	 */
	public static PageContext get() {//print.dumpStack();
		return pcThreadLocal.get();
	}
	
	public static Config getConfig() {
		PageContext pc = get();
		if(pc!=null) return pc.getConfig();
		return ThreadLocalConfig.get();
		
	}

	/**
	 * release the pagecontext for the current thread
	 */
	public static void release() {
		pcThreadLocal.set(null);
	}

	public static Config getConfig(PageContext pc) {
		if(pc==null)return getConfig();
	    return pc.getConfig();
	}

	public static Config getConfig(Config config) {
		if(config==null)return getConfig();
	    return config;
	}
	
	public static TimeZone getTimeZone(PageContext pc) {
		// pc
		pc = get(pc);
		if(pc!=null){
			if(pc.getTimeZone()!=null)return  pc.getTimeZone();
			return DEFAULT_TIMEZONE;
		}
		
		// config
		Config config = getConfig((Config)null);
		if(config!=null && config.getTimeZone()!=null) {
			return config.getTimeZone();
		}
	    return DEFAULT_TIMEZONE;
	}
	
	public static TimeZone getTimeZone(Config config) {
		PageContext pc = get();
		if(pc!=null && pc.getTimeZone()!=null)
			return  pc.getTimeZone();
			
		config=getConfig(config);
		if(config!=null && config.getTimeZone()!=null) {
			return config.getTimeZone();
		}
	    return DEFAULT_TIMEZONE;
	}
	
	public static TimeZone getTimeZone(TimeZone timezone) {
		if(timezone!=null) return timezone;
	    return getTimeZone((PageContext)null);
	}
	
	public static TimeZone getTimeZone() {
		return getTimeZone((PageContext)null);
	}

	public static PageContext get(PageContext pc) {
		if(pc==null)return get();
	    return pc;
	}

}