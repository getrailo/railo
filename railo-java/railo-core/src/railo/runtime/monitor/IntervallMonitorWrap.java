package railo.runtime.monitor;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

import railo.commons.lang.ExceptionUtil;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Query;

public class IntervallMonitorWrap extends MonitorWrap implements IntervallMonitor {
	private static final Object[] PARAMS_LOG = new Object[0];

	private Method log;
	private Method getData;

	public IntervallMonitorWrap(Object monitor) {
		super(monitor,TYPE_INTERVALL);
	}

	@Override
	public void log() throws IOException {

		try {
			if(log==null) {
				log=monitor.getClass().getMethod("log", new Class[0]);
			}
			log.invoke(monitor, PARAMS_LOG);
		} catch (Exception e) {e.printStackTrace();
			throw ExceptionUtil.toIOException(e);
		} 
	}

	public Query getData(Map<String,Object> arguments) throws PageException{
		try {
			if(getData==null) {
				getData=monitor.getClass().getMethod("getData", new Class[]{Map.class});
			}
			return (Query) getData.invoke(monitor, new Object[]{arguments});
		} catch (Exception e) {
			throw Caster.toPageException(e);
		} 
	}

	/*public Query getData(long minAge, long maxAge, int maxrows) throws IOException{
		try {
			if(getData==null) {
				getData=monitor.getClass().getMethod("getData", new Class[]{long.class,long.class,int.class});
			}
			return (Query) getData.invoke(monitor, new Object[]{new Long(minAge),new Long(maxAge),new Integer(maxrows)});
		} catch (Exception e) {
			throw ExceptionUtil.toIOException(e);
		} 
	}*/

}
