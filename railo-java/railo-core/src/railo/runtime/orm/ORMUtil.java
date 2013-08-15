package railo.runtime.orm;

import railo.commons.lang.StringUtil;
import railo.commons.lang.SystemOut;
import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.component.Property;
import railo.runtime.config.ConfigImpl;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.util.ComponentUtil;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.ListUtil;
import railo.runtime.type.KeyImpl;

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

	/*public static boolean equals(Object left, Object right) {
		HashSet<Object> done=new HashSet<Object>();
		return _equals(done, left, right);
	}*/
	
	/*private static boolean _equals(HashSet<Object> done,Object left, Object right) {
		
		if(left==right) return true;
		if(left==null || right==null) return false;
		
		// components
		if(left instanceof Component && right instanceof Component){
			return _equals(done,(Component)left, (Component)right);
		}

		// arrays
		if(Decision.isArray(left) && Decision.isArray(right)){
			return _equals(done,Caster.toArray(left,null), Caster.toArray(right,null));
		}

		// struct
		if(Decision.isStruct(left) && Decision.isStruct(right)){
			return _equals(done,Caster.toStruct(left,null), Caster.toStruct(right,null));
		}
		
		try {
			return Operator.equals(left,right,false);
		} catch (PageException e) {
			return false;
		}
	}*/
	
	/*private static boolean _equals(HashSet<Object> done,Collection left, Collection right) {
		if(done.contains(left)) return done.contains(right);
		done.add(left);
		done.add(right);
		
		if(left.size()!=right.size()) return false;
		Iterator<Entry<Key, Object>> it = left.entryIterator();
		Entry<Key, Object> e;
		Object l,r;
		while(it.hasNext()){
			e = it.next();
			l=e.getValue();
			r=right.get(e.getKey(),null);
			if(r==null || !_equals(done,l, r)) return false;
		}
		return true;
	}*/
	
	/*private static boolean _equals(HashSet<Object> done,Component left, Component right) {
		if(done.contains(left)) return done.contains(right);
		done.add(left);
		done.add(right);
	 	
		if(left==null || right==null) return false;
		if(!left.getPageSource().equals(right.getPageSource())) return false;
		Property[] props = ComponentUtil.getProperties(left,true,true,false,false);
		Object l,r;
		props=ComponentUtil.getIDProperties(props);
		for(int i=0;i<props.length;i++){
			l=left.getComponentScope().get(KeyImpl.init(props[i].getName()),null);
			r=right.getComponentScope().get(KeyImpl.init(props[i].getName()),null);
			if(!_equals(done,l, r)) return false;
		}
		return true;
	}*/
	
	
	
	
	public static Object getPropertyValue(Component cfc, String name, Object defaultValue) {
		Property[] props=ComponentUtil.getProperties(cfc,true,true,false,false);
		
		for(int i=0;i<props.length;i++){
			if(!props[i].getName().equalsIgnoreCase(name)) continue;
			return cfc.getComponentScope().get(KeyImpl.getInstance(name),null);
		}
		return defaultValue;
	}

	public static boolean isRelated(Property prop) {
		String fieldType = Caster.toString(prop.getDynamicAttributes().get(KeyConstants._fieldtype,"column"),"column");
		if(StringUtil.isEmpty(fieldType,true)) return false;
		fieldType=fieldType.toLowerCase().trim();
		
		if("one-to-one".equals(fieldType)) 		return true;
		if("many-to-one".equals(fieldType)) 	return true;
		if("one-to-many".equals(fieldType)) 	return true;
		if("many-to-many".equals(fieldType)) 	return true;
		return false;
	}
	
	public static Struct convertToSimpleMap(String paramsStr) {
		paramsStr=paramsStr.trim();
        if(!StringUtil.startsWith(paramsStr, '{') || !StringUtil.endsWith(paramsStr, '}'))
        	return null;
        	
		paramsStr = paramsStr.substring(1, paramsStr.length() - 1);
		String items[] = ListUtil.listToStringArray(paramsStr, ','); 
		
		Struct params=new StructImpl();
		String arr$[] = items;
		int index;
        for(int i = 0; i < arr$.length; i++)	{
            String pair = arr$[i];
            index = pair.indexOf('=');
            if(index == -1) return null;
            
            params.setEL(
            		KeyImpl.init(deleteQuotes(pair.substring(0, index).trim()).trim()), 
            		deleteQuotes(pair.substring(index + 1).trim()));
        }

        return params;
    }
	
	private static String deleteQuotes(String str)	{
        if(StringUtil.isEmpty(str,true))return "";
        char first=str.charAt(0);
        if((first=='\'' || first=='"') && StringUtil.endsWith(str, first))
        	return str.substring(1, str.length() - 1);
        return str;
    }
}
