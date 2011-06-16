package railo.runtime.orm;

import java.util.HashSet;

import railo.commons.lang.SystemOut;
import railo.runtime.Component;
import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.component.Property;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.PageException;
import railo.runtime.op.Decision;
import railo.runtime.op.Operator;
import railo.runtime.orm.hibernate.HBMCreator;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.util.ComponentUtil;

public class ORMUtil {

	public static ORMSession getSession(PageContext pc) throws PageException {
		return getSession(pc,true);
	}
	
	public static ORMSession getSession(PageContext pc, boolean create) throws PageException {
		return ((PageContextImpl) pc).getORMSession(create);
	}

	public static ORMEngine getEngine(PageContext pc) throws PageException {
		ConfigImpl config=(ConfigImpl) pc.getConfig();
		return config.getORMEngine(pc);
	}

	/**
	 * 
	 * @param pc
	 * @param force if set to false the engine is on loaded when the configuration has changed
	 * @throws PageException
	 */
	public static void resetEngine(PageContext pc, boolean force) throws PageException {
		ConfigImpl config=(ConfigImpl) pc.getConfig();
		config.resetORMEngine(pc,force);
	}
	
	public static void printError(Throwable t, ORMEngine engine) {
		printError(t, engine, t.getMessage());
	}

	public static void printError(String msg, ORMEngine engine) {
		printError(null, engine, msg);
	}

	private static void printError(Throwable t, ORMEngine engine,String msg) {
		SystemOut.printDate("{"+engine.getLabel().toUpperCase()+"} - "+msg,SystemOut.ERR);
		if(t==null)t=new Throwable();
		t.printStackTrace(SystemOut.getPrinWriter(SystemOut.ERR));
	}

	public static boolean equals(Object left, Object right) {
		HashSet<Object> done=new HashSet<Object>();
		return _equals(done, left, right);
	}
	
	private static boolean _equals(HashSet<Object> done,Object left, Object right) {
		
		if(left==right) return true;
		if(left==null || right==null) return false;
		
		// components
		if(left instanceof Component && right instanceof Component){
			return _equals(done,(Component)left, (Component)right);
		}

		// arrays
		if(Decision.isArray(left) && Decision.isArray(right)){
			return _equals(done,(Component)left, (Component)right);
		}

		// struct
		if(Decision.isStruct(left) && Decision.isStruct(right)){
			return _equals(done,(Struct)left, (Struct)right);
		}
		
		try {
			return Operator.equals(left,right,false);
		} catch (PageException e) {
			return false;
		}
	}
	
	private static boolean _equals(HashSet<Object> done,Collection left, Collection right) {
		if(done.contains(left)) return done.contains(right);
		done.add(left);
		done.add(right);
		
		if(left.size()!=right.size()) return false;
		Key[] keys = left.keys();
		Object l,r;
		for(int i=0;i<keys.length;i++){
			l=left.get(keys[i],null);
			r=right.get(keys[i],null);
			if(r==null || !_equals(done,l, r)) return false;
		}
		return true;
	}
	
	private static boolean _equals(HashSet<Object> done,Component left, Component right) {
		if(done.contains(left)) return done.contains(right);
		done.add(left);
		done.add(right);
		
		
		Component cpl =ComponentUtil.toComponent(left,null);
		Component cpr = ComponentUtil.toComponent(right,null);
		
		if(cpl==null || cpr==null) return false;
		if(!cpl.getPageSource().equals(cpr.getPageSource())) return false;
		Property[] props = cpl.getProperties(true);
		Object l,r;
		props=HBMCreator.getIds(null,null,props,null,true);
		for(int i=0;i<props.length;i++){
			l=cpl.getComponentScope().get(KeyImpl.init(props[i].getName()),null);
			r=cpr.getComponentScope().get(KeyImpl.init(props[i].getName()),null);
			if(!_equals(done,l, r)) return false;
		}
		return true;
	}
}
