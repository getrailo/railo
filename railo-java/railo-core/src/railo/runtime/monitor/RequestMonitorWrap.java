package railo.runtime.monitor;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

import railo.commons.lang.ExceptionUtil;
import railo.runtime.PageContext;
import railo.runtime.config.ConfigWeb;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Query;

public class RequestMonitorWrap extends MonitorWrap implements RequestMonitor {
	private static final Class[] PARAMS_LOG = new Class[]{PageContext.class,boolean.class};

	private Method log;
	private Method getData;

	private Method getDataRaw;



	public RequestMonitorWrap(Object monitor) {
		super(monitor,TYPE_REQUEST);
	}

	@Override
	public void log(PageContext pc, boolean error) throws IOException {

		try {
			if(log==null) {
				log=monitor.getClass().getMethod("log", PARAMS_LOG);
			}
			log.invoke(monitor, new Object[]{pc,Caster.toBoolean(error)});
		} catch (Exception e) {
			throw ExceptionUtil.toIOException(e);
		} 
	}

	public Query getData(ConfigWeb config,Map<String,Object> arguments) throws PageException{
		try {
			if(getData==null) {
				getData=monitor.getClass().getMethod("getData", new Class[]{ConfigWeb.class,Map.class});
			}
			return (Query) getData.invoke(monitor, new Object[]{config,arguments});
		} catch (Exception e) {
			throw Caster.toPageException(e);
		} 
	}

	
	/*
	public Query getData(ConfigWeb config,long minAge, long maxAge, int maxrows) throws IOException{
		try {
			if(getData==null) {
				getData=monitor.getClass().getMethod("getData", new Class[]{long.class,long.class,int.class});
			}
			return (Query) getData.invoke(monitor, new Object[]{new Long(minAge),new Long(maxAge),new Integer(maxrows)});
		} catch (Exception e) {
			throw ExceptionUtil.toIOException(e);
		} 
	}
	
	public Query getDataRaw(ConfigWeb config, long minAge, long maxAge) throws IOException {
		try {
			if(getDataRaw==null) {
				getDataRaw=monitor.getClass().getMethod("getDataRaw", new Class[]{ConfigWeb.class,long.class,long.class});
			}
			return (Query) getDataRaw.invoke(monitor, new Object[]{config,new Long(minAge),new Long(maxAge)});
		} catch (Exception e) {e.printStackTrace();
			throw ExceptionUtil.toIOException(e);
		} 
	}*/
}
