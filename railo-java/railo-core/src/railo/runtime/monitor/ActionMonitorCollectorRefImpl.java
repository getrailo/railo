package railo.runtime.monitor;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import railo.runtime.PageContext;
import railo.runtime.config.ConfigServer;
import railo.runtime.config.ConfigWeb;

public class ActionMonitorCollectorRefImpl implements ActionMonitorCollector {
	
	private List<Object> monitors=new ArrayList<Object>();
	private Method init;
	private Method logc;
	private Method getName;
	private Method logpc;
	
	@Override
	public void addMonitor(ConfigServer cs,Object monitor, String name, boolean log) throws IOException {
		monitor=init(monitor,cs,name,log);
		if(monitor!=null)monitors.add(monitor);
	}

	@Override
	public void log(PageContext pc, String type, String label, long executionTime, Object data) {
		
		Iterator<Object> it = monitors.iterator();
		while(it.hasNext()){
			log(it.next(),pc, type, label, executionTime, data);
		}
	}

	@Override
	public void log(ConfigWeb config, String type, String label, long executionTime, Object data) {
		
		Iterator<Object> it = monitors.iterator();
		while(it.hasNext()){
			log(it.next(),config, type, label, executionTime, data);
		}
	}
	
	@Override
	public Object getActionMonitor(String name) {
		Iterator<Object> it = monitors.iterator();
		Object am;
		while(it.hasNext()){
			am = it.next();
			if(name.equalsIgnoreCase(getName(am))) return am;
		}
		return null;
	}




	private String getName(Object am) {
		if(getName==null){
			try {
				getName=am.getClass().getMethod("getName", new Class[]{});
			} 
			catch(Throwable t) {
				t.printStackTrace();
				return null;
			}
		}
		
		try {
			return (String) getName.invoke(am, new Object[]{});
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
		return null;
	}
	
	private void log(Object monitor, PageContext pc, String type, String label, long executionTime, Object data) {
		if(logpc==null){
			try {
				logpc=monitor.getClass().getMethod("log", new Class[]{PageContext.class,String.class,String.class,long.class,Object.class});
			} 
			catch (Throwable t) {
				t.printStackTrace();
				return;
			}
		}
		
		try {
			logpc.invoke(monitor, new Object[]{pc,type,label,executionTime,data});
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	private void log(Object monitor, ConfigWeb config, String type, String label, long executionTime, Object data) {
		if(logc==null){
			try {
				logc=monitor.getClass().getMethod("log", new Class[]{ConfigWeb.class,String.class,String.class,long.class,Object.class});
			} 
			catch (Throwable t) {
				t.printStackTrace();
				return;
			}
		}
		
		try {
			logc.invoke(monitor, new Object[]{config,type,label,executionTime,data});
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
	

	private Object init(Object monitor, ConfigServer cs, String name, boolean log) {
		if(init==null){
			try {
				init=monitor.getClass().getMethod("init", new Class[]{ConfigServer.class,String.class,boolean.class});
			} 
			catch (Throwable t) {
				t.printStackTrace();
				return null;
			}
		}
		
		try {
			return init.invoke(monitor, new Object[]{cs,name,log});
		}
		catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}
}
