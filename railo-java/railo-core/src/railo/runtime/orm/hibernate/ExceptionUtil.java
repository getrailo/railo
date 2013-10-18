package railo.runtime.orm.hibernate;

import java.lang.reflect.Method;

import railo.commons.io.res.util.ResourceUtil;
import railo.loader.engine.CFMLEngineFactory;
import railo.runtime.Component;
import railo.runtime.db.DataSource;
import railo.runtime.exp.PageException;
import railo.runtime.orm.ORMSession;
import railo.runtime.type.Collection.Key;

public class ExceptionUtil {


	private static Method setAdditional;


	public static PageException createException(SessionFactoryData data, Component cfc, String msg, String detail) {
		
		PageException pe = createException((ORMSession)null,cfc,msg,detail);
		if(data!=null)setAddional(pe,data);
		return pe;
	}
	public static PageException createException(SessionFactoryData data, Component cfc, Throwable t) {
		PageException pe = createException((ORMSession)null,cfc,t);
		if(data!=null)setAddional(pe,data);
		return pe;
	}

	
	public static PageException createException(ORMSession session,Component cfc,Throwable t) {
		PageException pe = CFMLEngineFactory.getInstance().getExceptionUtil().createApplicationException(t.getMessage());
		pe.setStackTrace(t.getStackTrace());
		if(session!=null)setAddional(session,pe);
		if(cfc!=null)setContext(pe,cfc);
		return pe;
	}
	

	public static PageException createException(ORMSession session,Component cfc,String message,String detail) {
		PageException pe = CFMLEngineFactory.getInstance().getExceptionUtil().createApplicationException(message);
		if(session!=null)setAddional(session,pe);
		if(cfc!=null)setContext(pe,cfc);
		return pe;
	}
	

	private static void setContext(PageException pe,Component cfc) {
		if(cfc!=null && getPageDeep(pe)==0)pe.addContext(cfc.getPageSource(), 1, 1, null);
	}
	

	private static void setAddional(PageException pe,SessionFactoryData data) {
		String[] names = data.getEntityNames();
		setAdditional(pe,CommonUtil.createKey("Entities"), CommonUtil.toList(names, ", "));
		setAddional(data.getDataSource(),pe);
	}
	
	private static void setAddional(ORMSession session,PageException pe) {
		String[] names = session.getEntityNames();
		
		setAdditional(pe, CommonUtil.createKey("Entities"),  CommonUtil.toList(names, ", "));
		setAddional(session.getDataSource(),pe);
	}
	
	private static void setAddional(DataSource ds,PageException pe) {
		if(ds!=null){
			String dsn=ds.getName();
			if(dsn!=null)setAdditional(pe, CommonUtil.createKey("_Datasource"), dsn);
		}
	}
	
	private static int getPageDeep(PageException pe) {
		StackTraceElement[] traces = getStackTraceElements(pe);
		
		String template="",tlast;
		StackTraceElement trace=null;
		int index=0;
		for(int i=0;i<traces.length;i++) {
			trace=traces[i];
			tlast=template;
			template=trace.getFileName();
			if(trace.getLineNumber()<=0 || template==null || ResourceUtil.getExtension(template,"").equals("java")) continue;
			if(!(tlast==null?"":tlast).equals(template))index++;
			
		}
		return index;
	}
	
	private static StackTraceElement[] getStackTraceElements(Throwable t) {
    	StackTraceElement[] st=getStackTraceElements(t,true);
    	if(st==null) st= getStackTraceElements(t,false);
    	return st;
    }
    
    private static StackTraceElement[] getStackTraceElements(Throwable t, boolean onlyWithCML) {
    	StackTraceElement[] st;
    	Throwable cause=t.getCause();
    	if(cause!=null){
    		st = getStackTraceElements(cause,onlyWithCML);
        	if(st!=null) return st;
    	}
    	
    	st=t.getStackTrace();
    	if(!onlyWithCML || hasCFMLinStacktrace(st)){
    		return st;
    	}
    	return null;
	}
    
    
    private static boolean hasCFMLinStacktrace(StackTraceElement[] traces) {
		for(int i=0;i<traces.length;i++) {
			if(traces[i].getFileName()!=null && !traces[i].getFileName().endsWith(".java")) return true;
		}
		return false;
	}
    

	public static void setAdditional(PageException pe, Key name, Object value) { 
		try{
			if(setAdditional==null || setAdditional.getDeclaringClass()!=pe.getClass()) {
				setAdditional=pe.getClass().getMethod("setAdditional", new Class[]{Key.class,Object.class});
			}
			setAdditional.invoke(pe, new Object[]{name,value});
		}
		catch(Throwable t){}
	}
}
